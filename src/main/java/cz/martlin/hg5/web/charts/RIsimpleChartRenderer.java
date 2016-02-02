package cz.martlin.hg5.web.charts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.martlin.hg5.logic.data.SoundTrack;
import cz.martlin.hg5.logic.processV1.ImprovedAudioProcessor;
import cz.martlin.hg5.web.HomeguardSingleton;

/**
 * Implements creating of very-the-fuck simple technique of creating time
 * diagrams of samples. Each set of samples is divided into groups (for each
 * resulting image column 1 pixel width) and theese samples are aggregated. Then
 * is each group's min, average and max value rendered as 1px width bar with
 * given colors.
 * 
 * @author martin
 *
 */
public class RIsimpleChartRenderer implements Serializable {
	private static final long serialVersionUID = -1115620835373096499L;
	private final Logger LOG = LogManager.getLogger(getClass());

	public static final String FORMAT = "png";
	public static final Color MINS_DEFAULT_COLOR = Color.BLUE;
	public static final Color AVGS_DEFAULT_COLOR = Color.MAGENTA;
	public static final Color MAXS_DEFAULT_COLOR = Color.RED;
	public static final Color BG_DEFAULT_COLOR = Color.WHITE;

	private static final ImprovedAudioProcessor processor = new ImprovedAudioProcessor(HomeguardSingleton.getConfig());

	private final Map<SoundTrack, byte[]> cache = new HashMap<>();

	/**
	 * If the chart of given track have rendered chart yet, is used instead of
	 * calculating new, else is created and cached.
	 * 
	 * @param track
	 * @param width
	 * @param height
	 * @param mins
	 * @param avgs
	 * @param maxs
	 * @param background
	 * @return chart as PNG binary data
	 */
	public byte[] getChartCached(SoundTrack track, int width, int height, Color mins, Color avgs, Color maxs,
			Color background) {
		LOG.info("Getting chart of track (from cache, currently cached {} charts): {}", cache.size(), track);

		byte[] chart = cache.get(track);

		if (chart == null) {
			chart = getChart(track, width, height, mins, avgs, maxs, background);
			cache.put(track, chart);
		}

		return chart;
	}

	/**
	 * Creates chart for given track.
	 * 
	 * @param track
	 * @param width
	 * @param height
	 * @param mins
	 * @param avgs
	 * @param maxs
	 * @param background
	 * @return chart as PNG data
	 */
	public byte[] getChart(SoundTrack track, int width, int height, Color mins, Color avgs, Color maxs,
			Color background) {
		LOG.info("Creating chart of track: {}", track);

		double[] samples = processor.samplesOfTrack(track);

		BufferedImage image = createChart(samples, width, height, mins, avgs, maxs, background);
		byte[] data = exportImage(image);

		return data;
	}

	/**
	 * Creates chart of given samples set.
	 * 
	 * @param samples
	 * @param width
	 * @param height
	 * @param mins
	 * @param avgs
	 * @param maxs
	 * @param background
	 * @return
	 */
	private BufferedImage createChart(double[] samples, int width, int height, Color mins, Color avgs, Color maxs,
			Color background) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.getGraphics();

		g.setColor(background);
		g.fillRect(0, 0, width, height);

		drawSamples(samples, g, width, height, mins, avgs, maxs);

		return img;
	}

	/**
	 * Does the main - draws samples with given graphics.
	 * 
	 * @param samples
	 * @param g
	 * @param width
	 * @param height
	 * @param mins
	 * @param avgs
	 * @param maxs
	 */
	private void drawSamples(double[] samples, Graphics g, int width, int height, Color mins, Color avgs, Color maxs) {
		double samplesPerColumn = (double) samples.length / width;

		for (int col = 0; col < width; col++) {
			double min = 1.0, max = 0.0, sum = 0.0;

			for (int spci = 0; spci < samplesPerColumn; spci++) {
				int si = (int) (samplesPerColumn * col + spci);

				if (si > samples.length) {
					break;
				}

				double sample = samples[si];
				min = Math.min(min, sample);
				max = Math.max(max, sample);
				sum += sample;
			}

			g.setColor(maxs);
			int maxsVal = (int) (max * height);
			g.drawLine(col, height, col, height - maxsVal);

			g.setColor(avgs);
			int avgsVal = (int) ((sum / samplesPerColumn) * height);
			g.drawLine(col, height, col, height - avgsVal);

			g.setColor(mins);
			int minsVal = (int) (min * height);
			g.drawLine(col, height, col, height - minsVal);
		}
	}

	/**
	 * Saves given image as PNG and reads theese bytes.
	 * 
	 * @see http://stackoverflow.com/questions/8937241/convert-bufferedimage-to-
	 *      byte-without-losing-quality-and-increasing-size
	 * @param image
	 * @return
	 */
	private byte[] exportImage(BufferedImage image) {

		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();

			ImageIO.write(image, FORMAT, baos);

			baos.flush();
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (IOException e) {
			LOG.error("Cannot create chart", e);
			return null;
		} finally {
			IOUtils.closeQuietly(baos);
		}
	}

}
