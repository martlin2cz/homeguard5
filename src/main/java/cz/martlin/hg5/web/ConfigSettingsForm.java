package cz.martlin.hg5.web;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import cz.martlin.hg5.Homeguard;
import cz.martlin.hg5.logic.config.Configuration;

@RequestScoped
@ManagedBean(name = "configSettingsForm")
public class ConfigSettingsForm implements Serializable {
	private static final long serialVersionUID = 3501199313655052697L;

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
		Homeguard hg = HomeguardSingleton.get();

		hg.setConfigTo(config);
		boolean success = hg.saveConfig();
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
		Homeguard hg = HomeguardSingleton.get();

		boolean success = hg.loadConfig();
		if (success) {
			this.config.setTo(hg.getConfig());
			Utils.info("Načteno", "Konfigurační soubor byl načten znovu");
		} else {
			Utils.error("Chyba", "Konfigurační soubor se nepodařilo načíst");
		}
	}

	public void redefault() {
		config.setTo(new Configuration());
		Utils.info("Obnoveno", "Configurace nastavena na výchozí (ale zatím neuložena)");
	}

}
