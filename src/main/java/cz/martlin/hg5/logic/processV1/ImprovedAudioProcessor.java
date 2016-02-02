package cz.martlin.hg5.logic.processV1;

import java.io.Serializable;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.data.ReportItem;
import cz.martlin.hg5.logic.data.SoundTrack;
import cz.martlin.hg5.logic.process.AbstractAudioProcessor;

/**
 * Implements Audio processor by counting "warning" an "error" samples.
 * 
 * @author martin
 *
 */
public class ImprovedAudioProcessor implements Serializable, AbstractAudioProcessor {
	private static final long serialVersionUID = -1156332853195061753L;
	private final Logger LOG = LogManager.getLogger(getClass());

	private final Configuration config;

	public ImprovedAudioProcessor(Configuration config) {
		this.config = config;
	}

	/**
	 * Counts number of samples, which's levels are higher than threshold.
	 * 
	 * @param sample
	 * @param threshold
	 * @return
	 */
	private int countWithLeastThan(double samples[], double threshold) {
		int count = 0;

		for (double value : samples) {
			if (value >= threshold) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Converts bytes of track into array of concrete samples (doubles from 0.0
	 * to 1.0). Requires 8bits per sample track format.
	 * 
	 * @param track
	 * @return
	 */
	public double[] samplesOfTrack(SoundTrack track) {
		if (track.getFormat().getSampleSizeInBits() != 8) {
			LOG.warn("Trying to process track with sample size different than 8bits per sample.");
		}

		byte[] bytes = track.getBytes();
		double[] result = new double[bytes.length];

		for (int i = 0; i < bytes.length; i++) {
			int value = toCorrectSignAndEndianOrWhat(bytes[i]);
			result[i] = ((double) Math.abs(value) / Byte.MAX_VALUE);
		}

		return result;
	}

	/**
	 * Does some sample conversion (experimentally found), like this:
	 * <ul>
	 * <li>-1 to +127</li>
	 * <li>-50 to +77</li>
	 * <li>-128 to +0</li>
	 * <li>+127 to -0</li>
	 * <li>+50 to -77</li>
	 * <li>+0 to -128</li>
	 * </ul>
	 * 
	 * @param bytes
	 * @param i
	 * @return
	 */
	private int toCorrectSignAndEndianOrWhat(byte b) {
		if (b >= 0 && b <= Byte.MAX_VALUE) {
			return (b - Byte.MAX_VALUE);
		} else if (b >= Byte.MIN_VALUE && b < 0) {
			return b - Byte.MIN_VALUE;
		} else {
			throw new IllegalStateException("Out of range: " + b);
		}
	}

	@Override
	public ReportItem analyzeSample(Calendar recordedAt, SoundTrack track) {
		LOG.info("Analyzing sound track {} recorded at {}", track, recordedAt.getTime());

		double[] samples = samplesOfTrack(track);

		int warnings = countWithLeastThan(samples, config.getWarningNoiseThreshold());
		int criticals = countWithLeastThan(samples, config.getCriticalNoiseThreshold());

		ReportItem item = ReportItem.createWithSamples(recordedAt, samples, config, warnings, criticals);
		LOG.info("Sample record analized with result {}", item);
		return item;
	}
}
