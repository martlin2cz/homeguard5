package cz.martlin.hg5.web;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import cz.martlin.hg5.logic.config.ConfigLoader;
import cz.martlin.hg5.logic.config.Configuration;

@RequestScoped
@ManagedBean(name = "configSettingsForm")
public class ConfigSettingsForm implements Serializable {
	private static final long serialVersionUID = 3501199313655052697L;

	private static final ConfigLoader LOADER = new ConfigLoader();

	private final Configuration config = new Configuration();

	public ConfigSettingsForm() {
	}

	@PostConstruct
	public void init() {
		config.setTo(HomeguardSingleton.getConfig());
		checkAndWarn();
	}

	private void checkAndWarn() {
		if (getConfig().getCriticalNoiseThreshold() < getConfig().getWarningNoiseThreshold()) {
			Utils.warn("Chybné prahy", "Pozor, varovný práh by měl být nižší, než kritický");
		}

		if (getConfig().getCriticalNoiseAmount() > getConfig().getWarningNoiseAmount()) {
			Utils.warn("Chybné množtví hluku", "Pozor, kritické množstí hluku by mělo být menší, než varovné");
		}
	}

	public Configuration getConfig() {
		return config;
	}

	public void save() {
		checkAndWarn();

		HomeguardSingleton.getConfig().setTo(config);
		boolean success = LOADER.save(config);
		if (success) {
			Utils.info("Uloženo", "Konfigurační soubor byl uložen");
		} else {
			Utils.error("Chyba", "Konfigurační soubor se nepodařilo uložit");
		}
	}

	public void reset() {
		config.setTo(HomeguardSingleton.getConfig());
	}

	public void reload() {
		boolean success = LOADER.load(config);
		if (success) {
			HomeguardSingleton.getConfig().setTo(config);
			Utils.info("Načteno", "Konfigurační soubor byl načten znovu");
		} else {
			Utils.error("Chyba", "Konfigurační soubor se nepodařilo načíst");
		}

	}

}
