package cz.martlin.hg5.web;

import java.io.Serializable;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.martlin.hg5.HomeGuardAppParams;
import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.data.GuardingReport;
import cz.martlin.hg5.logic.guard.Homeguard;

@ApplicationScoped
@ManagedBean(name = "mainForm")
public class MainForm implements Serializable {
	private static final long serialVersionUID = -5102412853399452864L;
	private final Logger LOG = LogManager.getLogger(getClass());

	private final Homeguard homeguard = HomeguardSingleton.getHomeguard();

	public MainForm() {
	}

	public Configuration getConfig() {
		return homeguard.getConfig();
	}

	public Homeguard getHomeguard() {
		return homeguard;
	}

	public GuardingReport getCurrentReport() {
		return homeguard.currentReport();
	}

	public GuardingReport getLastReport() {
		return homeguard.lastReport();
	}

	public boolean isRunning() {
		return homeguard.isRunning();
	}

	public String getAppName() {
		return HomeGuardAppParams.APP_NAME;
	}

	public String getVersion() {
		return HomeGuardAppParams.VERSION;
	}

	public String getAuthor() {
		return HomeGuardAppParams.AUTHOR;
	}

	/////////////////////////////////////////////////////////////////////////////

	public void start() {
		homeguard.start();
	}

	public void stop() {
		homeguard.stop();
	}

	public void settingsChanged() {
		LOG.info("Configuration changed to {}", getConfig());

	}

}
