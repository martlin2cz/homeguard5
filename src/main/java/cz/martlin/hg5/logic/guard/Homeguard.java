package cz.martlin.hg5.logic.guard;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;

import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.data.GuardingReport;
import cz.martlin.hg5.logic.processV1.fsman.FileSystemReportsManager;

public class Homeguard implements Serializable {

	private static final long serialVersionUID = 786637193042907969L;

	private final Configuration config;
	private final GuardingPerformer performer;
	private final FileSystemReportsManager reports;

	public Homeguard(Configuration config) {
		this.config = config;
		this.performer = new GuardingPerformer(config);
		this.reports = new FileSystemReportsManager(config);
	}

	public Configuration getConfig() {
		return config;
	}

	public synchronized void start() {
		performer.start();
	}

	public synchronized void stop() {
		performer.stop();
	}

	public synchronized boolean isRunning() {
		return performer.isGuardingRunning();
	}

	public GuardingReport getReport(Calendar startedAt) {
		return reports.loadReport(startedAt);
	}

	public synchronized Set<GuardingReport> allReports() {
		return reports.loadAllReports();
	}

	public synchronized Set<GuardingReport> reportsAt(Calendar day) {
		return reports.loadReportsAtDay(day);
	}

	public synchronized GuardingReport lastReport() {
		return reports.loadLastReport();
	}

	public synchronized GuardingReport currentReport() {
		return performer.getCurrentInstance().getReport();
	}

}
