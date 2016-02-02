package cz.martlin.hg5.logic.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implements loading and saving of {@link Configuration}.
 * 
 * @author martin
 *
 */
public class ConfigLoader implements Serializable {
	private static final long serialVersionUID = 7148464013475790035L;

	private final Logger LOG = LogManager.getLogger(getClass());

	private static final File FILE = new File(".homeguard-config.properties");

	private static final String COMMENT = "Homeguard configuration file";

	/**
	 * Loads config file into given config instance.
	 * 
	 * @param config
	 * @return true if succeeds
	 */
	public boolean load(Configuration config) {
		Properties props = new Properties();
		boolean success = true;

		success &= load(FILE, props);
		success &= setTo(config, props);

		LOG.info("Config file loaded, succes? " + success);

		return success;
	}

	/**
	 * Saves given config into file.
	 * 
	 * @param config
	 * @return true if succeeds
	 */
	public boolean save(Configuration config) {
		Properties props = new Properties();
		boolean success = true;

		success &= setTo(props, config);
		success &= save(props, FILE);

		LOG.info("Config file saved, succes? " + success);

		return success;
	}

	/**
	 * Sets given properties into given config
	 * 
	 * @param props
	 * @param config
	 * @return true if succeeds
	 */
	private boolean setTo(Properties props, Configuration config) {
		props.put("samplesInterval", //
				Integer.toString(config.getSamplesInterval()));
		props.put("sampleLenght", //
				Integer.toString(config.getSampleLenght()));

		props.put("warningNoiseThreshold", //
				Double.toString(config.getWarningNoiseThreshold()));
		props.put("criticalNoiseThreshold", //
				Double.toString(config.getCriticalNoiseThreshold()));
		props.put("warningNoiseAmount", //
				Double.toString(config.getWarningNoiseAmount()));
		props.put("criticalNoiseAmount", //
				Double.toString(config.getCriticalNoiseAmount()));

		props.put("logsRootDir", //
				config.getLogsRootDir().getPath());
		props.put("defaultDescription", //
				config.getDefaultDescription());

		return true;
	}

	/**
	 * Sets given config into given properties
	 * 
	 * @param config
	 * @param props
	 * @return true if succeeds
	 */
	private boolean setTo(Configuration config, Properties props) {
		try {
			config.setSamplesInterval(//
					Integer.parseInt(props.getProperty("samplesInterval")));
			config.setSampleLenght(//
					Integer.parseInt(props.getProperty("sampleLenght")));

			config.setWarningNoiseThreshold(//
					Double.parseDouble(props.getProperty("warningNoiseThreshold")));
			config.setCriticalNoiseThreshold(//
					Double.parseDouble(props.getProperty("criticalNoiseThreshold")));
			config.setWarningNoiseAmount(//
					Double.parseDouble(props.getProperty("warningNoiseAmount")));
			config.setCriticalNoiseAmount(//
					Double.parseDouble(props.getProperty("criticalNoiseAmount")));

			config.setLogsRootDir(//
					new File(props.getProperty("logsRootDir")));
			config.setDefaultDescription(//
					props.getProperty("defaultDescription"));
		} catch (Exception e) {
			LOG.error("Error during loading config file: ", e);
			return false;
		}

		return true;
	}

	/**
	 * Saves given properties into file. If fails logs and returns false.
	 * 
	 * @param props
	 * @param file
	 * @return true if succeeds
	 */
	private boolean save(Properties props, File file) {
		Writer writer = null;
		try {
			writer = new FileWriter(file);
			props.store(writer, COMMENT);
			return true;
		} catch (IOException e) {
			LOG.fatal("Cannot save config", e);
			return false;
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	/**
	 * Loads from file into given properties. If fails logs and returns false.
	 * 
	 * @param file
	 * @param props
	 * @return true if succeeds
	 */
	private boolean load(File file, Properties props) {
		Reader reader = null;
		try {
			reader = new FileReader(file);
			props.load(reader);
			return true;
		} catch (IOException e) {
			LOG.fatal("Cannot load config", e);
			return false;
		} finally {
			IOUtils.closeQuietly(reader);
		}

	}

}
