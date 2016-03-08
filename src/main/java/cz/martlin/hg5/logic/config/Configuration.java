package cz.martlin.hg5.logic.config;

import java.io.File;
import java.io.Serializable;

public class Configuration implements Serializable, HasSamplesEntryConfig {
	private static final long serialVersionUID = -5676212208946459691L;

	private int samplesInterval = 5 * 60 /* 10 */;
	private int sampleLenght = 20 /* 1 */;
	private int samplesGroup = 64;

	private double warningNoiseThreshold = 0.5;
	private double criticalNoiseThreshold = 0.8;

	private double warningNoiseAmount = 0.2;
	private double criticalNoiseAmount = 0.1;

	private File logsRootDir = new File("/home/martin/hg5/");
	private String defaultDescription = "Nepřítomnost";

	public Configuration() {
	}

	public int getSamplesInterval() {
		return samplesInterval;
	}

	public void setSamplesInterval(int samplesInterval) {
		this.samplesInterval = samplesInterval;
	}

	public int getSampleLenght() {
		return sampleLenght;
	}

	public void setSampleLenght(int sampleLenght) {
		this.sampleLenght = sampleLenght;
	}

	public int getSamplesGroup() {
		return samplesGroup;
	}

	public void setSamplesGroup(int samplesGroup) {
		this.samplesGroup = samplesGroup;
	}

	@Override
	public double getWarningNoiseThreshold() {
		return warningNoiseThreshold;
	}

	public void setWarningNoiseThreshold(double warningNoiseThreshold) {
		this.warningNoiseThreshold = warningNoiseThreshold;
	}

	@Override
	public double getCriticalNoiseThreshold() {
		return criticalNoiseThreshold;
	}

	public void setCriticalNoiseThreshold(double errorNoiseThreshold) {
		this.criticalNoiseThreshold = errorNoiseThreshold;
	}

	@Override
	public double getWarningNoiseAmount() {
		return warningNoiseAmount;
	}

	public void setWarningNoiseAmount(double warningNoiseAmount) {
		this.warningNoiseAmount = warningNoiseAmount;
	}

	@Override
	public double getCriticalNoiseAmount() {
		return criticalNoiseAmount;
	}

	public void setCriticalNoiseAmount(double errorNoiseAmount) {
		this.criticalNoiseAmount = errorNoiseAmount;
	}

	public File getLogsRootDir() {
		return logsRootDir;
	}

	public void setLogsRootDir(File logsRootDir) {
		this.logsRootDir = logsRootDir;
	}

	public String getDefaultDescription() {
		return defaultDescription;
	}

	public void setDefaultDescription(String defaultDescription) {
		this.defaultDescription = defaultDescription;
	}

	public void setTo(Configuration other) {
		this.samplesInterval = other.samplesInterval;
		this.sampleLenght = other.sampleLenght;

		this.warningNoiseThreshold = other.warningNoiseThreshold;
		this.criticalNoiseThreshold = other.criticalNoiseThreshold;

		this.warningNoiseAmount = other.warningNoiseAmount;
		this.criticalNoiseAmount = other.criticalNoiseAmount;

		this.logsRootDir = other.logsRootDir;
		this.defaultDescription = other.defaultDescription;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(criticalNoiseAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(criticalNoiseThreshold);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((defaultDescription == null) ? 0 : defaultDescription.hashCode());
		result = prime * result + ((logsRootDir == null) ? 0 : logsRootDir.hashCode());
		result = prime * result + sampleLenght;
		result = prime * result + samplesInterval;
		temp = Double.doubleToLongBits(warningNoiseAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(warningNoiseThreshold);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Configuration other = (Configuration) obj;
		if (Double.doubleToLongBits(criticalNoiseAmount) != Double.doubleToLongBits(other.criticalNoiseAmount))
			return false;
		if (Double.doubleToLongBits(criticalNoiseThreshold) != Double.doubleToLongBits(other.criticalNoiseThreshold))
			return false;
		if (defaultDescription == null) {
			if (other.defaultDescription != null)
				return false;
		} else if (!defaultDescription.equals(other.defaultDescription))
			return false;
		if (logsRootDir == null) {
			if (other.logsRootDir != null)
				return false;
		} else if (!logsRootDir.equals(other.logsRootDir))
			return false;
		if (sampleLenght != other.sampleLenght)
			return false;
		if (samplesInterval != other.samplesInterval)
			return false;
		if (Double.doubleToLongBits(warningNoiseAmount) != Double.doubleToLongBits(other.warningNoiseAmount))
			return false;
		if (Double.doubleToLongBits(warningNoiseThreshold) != Double.doubleToLongBits(other.warningNoiseThreshold))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Configuration [samplesInterval=" + samplesInterval + ", sampleLenght=" + sampleLenght
				+ ", warningNoiseThreshold=" + warningNoiseThreshold + ", criticalNoiseThreshold="
				+ criticalNoiseThreshold + ", warningNoiseAmount=" + warningNoiseAmount + ", criticalNoiseAmount="
				+ criticalNoiseAmount + ", logsRootDir=" + logsRootDir + ", defaultDescription=" + defaultDescription
				+ "]";
	}

}
