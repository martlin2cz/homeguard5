package cz.martlin.hg5.logic.data;

import java.io.Serializable;
import java.util.Calendar;

import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.config.HasSamplesEntryConfig;
import cz.martlin.hg5.logic.processV1.fsman.FileSystemManTools;

/**
 * Encapsulation of soundtrack {@link SoundTrack}. Has various metadata of
 * soundtrack (and, optionally, particular array of samples). Each report item
 * can be warning and/or critical. This is given this way: The item is [X] if
 * the count of [X] samples ([X]SamplesCount) is higer than max[X]NoiseAmount.
 * The sample is [X] if its value is higher than [X]NoiseThreshold. The samples
 * array is loaded lazyly.
 * 
 * @author martin
 *
 */
public class ReportItem implements Serializable, HasSamplesEntryConfig {
	private static final long serialVersionUID = -3552670994980179448L;

	private final Calendar recordedAt;
	private final int lenghtInSeconds;

	private final int samplesCount;

	private final double warningNoiseThreshold;
	private final double criticalNoiseThreshold;

	private final double maxWarningNoiseAmount;
	private final double maxCriticalNoiseAmount;

	private final int warningSamplesCount;
	private final int criticalSamplesCount;

	public ReportItem(Calendar recordedAt, int lenghtInSeconds, int samplesCount, double warningNoiseThreshold,
			double criticalNoiseThreshold, double maxWarningNoiseAmount, double maxCriticalNoiseAmount,
			int warningSamplesCount, int criticalSamplesCount) {
		super();
		this.recordedAt = recordedAt;
		this.lenghtInSeconds = lenghtInSeconds;
		this.samplesCount = samplesCount;
		this.warningNoiseThreshold = warningNoiseThreshold;
		this.criticalNoiseThreshold = criticalNoiseThreshold;
		this.maxWarningNoiseAmount = maxWarningNoiseAmount;
		this.maxCriticalNoiseAmount = maxCriticalNoiseAmount;
		this.warningSamplesCount = warningSamplesCount;
		this.criticalSamplesCount = criticalSamplesCount;
	}

	public Calendar getRecordedAt() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(recordedAt.getTime());
		return cal;
	}

	public Calendar getRecordingStarted() {
		return getRecordedAt();
	}

	public Calendar getRecordingStopped() {
		Calendar cal = getRecordedAt();
		cal.add(Calendar.SECOND, lenghtInSeconds);
		return cal;
	}

	public int getLenghtInSeconds() {
		return lenghtInSeconds;
	}

	public double[] getSamples(Configuration config) {
		return loadSamples(config, recordedAt);
	}

	public int getSamplesCount() {
		return samplesCount;
	}

	public double getWarningNoiseThreshold() {
		return warningNoiseThreshold;
	}

	public double getCriticalNoiseThreshold() {
		return criticalNoiseThreshold;
	}

	public double getWarningNoiseAmount() {
		return maxWarningNoiseAmount;
	}

	public double getCriticalNoiseAmount() {
		return maxCriticalNoiseAmount;
	}

	public int getWarningSamplesCount() {
		return warningSamplesCount;
	}

	public int getCriticalSamplesCount() {
		return criticalSamplesCount;
	}

	public double getWarningSamplesRatio() {
		return (double) warningSamplesCount / samplesCount;
	}

	public double getCriticalSamplesRatio() {
		return (double) criticalSamplesCount / samplesCount;
	}

	public boolean isWarning() {
		return getWarningSamplesRatio() > maxWarningNoiseAmount;
	}

	public boolean isCritical() {
		return getCriticalSamplesRatio() > maxCriticalNoiseAmount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + criticalSamplesCount;
		long temp;
		temp = Double.doubleToLongBits(criticalNoiseThreshold);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + lenghtInSeconds;
		temp = Double.doubleToLongBits(maxCriticalNoiseAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(maxWarningNoiseAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((recordedAt == null) ? 0 : recordedAt.hashCode());
		result = prime * result + samplesCount;
		temp = Double.doubleToLongBits(warningNoiseThreshold);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + warningSamplesCount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportItem other = (ReportItem) obj;
		if (criticalSamplesCount != other.criticalSamplesCount)
			return false;
		if (Double.doubleToLongBits(criticalNoiseThreshold) != Double.doubleToLongBits(other.criticalNoiseThreshold))
			return false;
		if (lenghtInSeconds != other.lenghtInSeconds)
			return false;
		if (Double.doubleToLongBits(maxCriticalNoiseAmount) != Double.doubleToLongBits(other.maxCriticalNoiseAmount))
			return false;
		if (Double.doubleToLongBits(maxWarningNoiseAmount) != Double.doubleToLongBits(other.maxWarningNoiseAmount))
			return false;
		if (recordedAt == null) {
			if (other.recordedAt != null)
				return false;
		} else if (!recordedAt.equals(other.recordedAt))
			return false;
		if (samplesCount != other.samplesCount)
			return false;
		if (Double.doubleToLongBits(warningNoiseThreshold) != Double.doubleToLongBits(other.warningNoiseThreshold))
			return false;
		if (warningSamplesCount != other.warningSamplesCount)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReportItem [recordedAt=" + recordedAt.getTime() + //
				", getWarningSamplesRatio()=" + getWarningSamplesRatio() + //
				", getCriticalSamplesRatio()=" + getCriticalSamplesRatio() + //
				", isWarning()=" + isWarning() + //
				", isCritical()=" + isCritical() + //
				", lenghtInSeconds=" + lenghtInSeconds + //
				", samplesCount=" + samplesCount + //
				", warningNoiseThreshold=" + warningNoiseThreshold + //
				", criticalNoiseThreshold=" + criticalNoiseThreshold + //
				", maxWarningNoiseAmount=" + maxWarningNoiseAmount + //
				", maxCriticalNoiseAmount=" + maxCriticalNoiseAmount + //
				", warningSamplesCount=" + warningSamplesCount + ", " + //
				"criticalSamplesCount=" + criticalSamplesCount + "]";
	}

	private static double[] loadSamples(Configuration config, Calendar recordedAt) {
		final FileSystemManTools man = new FileSystemManTools(config);

		double[] samples = man.tryToLoadSamplesOfItem(recordedAt);
		return samples;
	}

	public static ReportItem createWithSamples(Calendar recordedAt, int samplesCount, Configuration config,
			int warningSamplesCount, int criticalSamplesCount) {

		int lenghtInSeconds = config.getSampleLenght();
		double warningNoiseThreshold = config.getWarningNoiseThreshold();
		double criticalNoiseThreshold = config.getCriticalNoiseThreshold();
		double maxWarningNoiseAmount = config.getWarningNoiseAmount();
		double maxCriticalNoiseAmount = config.getCriticalNoiseAmount();

		return new ReportItem(recordedAt, lenghtInSeconds, samplesCount, warningNoiseThreshold, criticalNoiseThreshold,
				maxWarningNoiseAmount, maxCriticalNoiseAmount, warningSamplesCount, criticalSamplesCount);
	}

	public static ReportItem createWithoutSamples(Calendar recordedAt, int lenghtInSeconds, int samplesCount,
			Configuration config, int warningSamplesCount, int criticalSamplesCount) {

		double warningNoiseThreshold = config.getWarningNoiseThreshold();
		double criticalNoiseThreshold = config.getCriticalNoiseThreshold();
		double maxWarningNoiseAmount = config.getWarningNoiseAmount();
		double maxCriticalNoiseAmount = config.getCriticalNoiseAmount();

		return new ReportItem(recordedAt, lenghtInSeconds, samplesCount, warningNoiseThreshold, criticalNoiseThreshold,
				maxWarningNoiseAmount, maxCriticalNoiseAmount, warningSamplesCount, criticalSamplesCount);
	}

}
