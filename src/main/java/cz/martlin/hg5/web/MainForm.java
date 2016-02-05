package cz.martlin.hg5.web;

import java.io.Serializable;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import cz.martlin.hg5.HomeGuardApp;
import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.data.GuardingReport;

@ApplicationScoped
@ManagedBean(name = "mainForm")
public class MainForm implements Serializable {
	private static final long serialVersionUID = -5102412853399452864L;

	public MainForm() {
	}

	public Configuration getConfig() {
		return HomeguardSingleton.get().getConfig();
	}

	public GuardingReport getCurrentReport() {
		return HomeguardSingleton.get().currentReport();
	}

	public GuardingReport getLastReport() {
		return HomeguardSingleton.get().lastReport();
	}

	public boolean isRunning() {
		return HomeguardSingleton.get().isRunning();
	}

	public String getAppName() {
		return HomeGuardApp.getAppName();
	}

	public String getVersion() {
		return HomeGuardApp.getVersion();
	}

	public String getAuthor() {
		return HomeGuardApp.getAuthor();
	}

	/////////////////////////////////////////////////////////////////////////////

	public void start() {
		HomeguardSingleton.get().start();
	}

	public void stop() {
		HomeguardSingleton.get().stop();
	}
}
