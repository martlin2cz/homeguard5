package cz.martlin.hg5.web.charts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import cz.martlin.hg5.logic.data.SoundTrack;
import cz.martlin.hg5.web.HomeguardSingleton;

/**
 * Old, charts generator for simple tracks, similar to
 * {@link GuardingReportChart}. Deprecated since is too slow.
 * 
 * @author martin
 * @deprecated too slow and practically unuseable, replaced with
 *             {@link RIsimpleChartRenderer}
 * @see RIsimpleChartRenderer
 *
 */
public class RIchartRenderer implements Serializable {
	private static final long serialVersionUID = -569957807078171698L;

	private final Logger LOG = LogManager.getLogger(getClass());

	public RIchartRenderer() {
	}

	/**
	 * Renders chart for given track.
	 * 
	 * @param track
	 * @param width
	 * @param height
	 * @return
	 */
	public byte[] getChartForTrack(SoundTrack track, int width, int height) {
		LOG.info("Renderuje se graf pro record: {},", track);

		CategoryDataset dataset = createDataset(track);
		JFreeChart chart = ChartFactory.createBarChart("Záznam", "x", "y", dataset, PlotOrientation.VERTICAL, true,
				false, false);

		File file;
		try {
			file = File.createTempFile("chart", ".png");
			ChartUtilities.saveChartAsPNG(file, chart, width, height);
		} catch (IOException e) {
			LOG.error("Cannot create graph", e);
			return null;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return IOUtils.toByteArray(fis);
		} catch (IOException e) {
			LOG.error("Cannot load graph", e);
			return null;
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

	/**
	 * Creates dataset of given track.
	 * 
	 * @param track
	 * @return
	 */
	private CategoryDataset createDataset(SoundTrack track) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		double[] samples = HomeguardSingleton.get().getJustSimplySamplesOfTrack(track);

		final String series = "Záznam";
		int i = 0;

		for (double sample : samples) {
			dataset.setValue(sample, series, new Integer(i));
			i++;
		}

		return dataset;
	}

}
