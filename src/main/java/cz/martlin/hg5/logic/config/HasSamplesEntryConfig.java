package cz.martlin.hg5.logic.config;

public interface HasSamplesEntryConfig {

	/**
	 * Value of sample level which is considered as warning.
	 * 
	 * @return number from 0.0 to 1.0
	 */
	double getWarningNoiseThreshold();

	/**
	 * Value of sample level which is considered as error.
	 * 
	 * @return number from 0.0 to 1.0
	 */
	double getCriticalNoiseThreshold();

	/**
	 * Allowed ratio of warnings's samples.
	 * 
	 * @return number from 0.0 to 1.0
	 */
	double getWarningNoiseAmount();

	/**
	 * Allowed ratio of errors' samples.
	 * 
	 * @return number from 0.0 to 1.0
	 */
	double getCriticalNoiseAmount();

}