package cz.martlin.hg5.web;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.martlin.hg5.logic.config.ConfigLoader;
import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.guard.Homeguard;

public class HomeguardSingleton implements Serializable {
	private static final long serialVersionUID = -8284209919770953819L;
	private static final Logger LOG = LogManager.getLogger(HomeguardSingleton.class);

	private static final ConfigLoader CONFIG_LOADER = new ConfigLoader();

	private static Homeguard homeguard = null;

	private HomeguardSingleton() {
	}

	public static Homeguard getHomeguard() {
		if (homeguard == null) {
			Configuration config = new Configuration();

			boolean success = CONFIG_LOADER.load(config);
			if (!success) {
				LOG.warn("Config file could not be loaded. Will be used default");
			}
			
			homeguard = new Homeguard(config);
		}

		return homeguard;
	}

	public static Configuration getConfig() {
		return getHomeguard().getConfig();
	}

}
