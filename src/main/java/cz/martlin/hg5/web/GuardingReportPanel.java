package cz.martlin.hg5.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.chart.LineChartModel;

import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.data.GuardingReport;
import cz.martlin.hg5.logic.processV1.FileSystemReporter;
import cz.martlin.hg5.web.charts.GuardingReportChart;

@ViewScoped
@ManagedBean(name = "guardingReportPanel")
public class GuardingReportPanel implements Serializable {
	private static final long serialVersionUID = 1329899742372452384L;
	private static final GuardingReportChart REPORT_CHARTS = new GuardingReportChart();
	// private final Logger LOG = LogManager.getLogger(getClass());
	//
	// private static final SimpleDateFormat DATE_FORMAT = new
	// SimpleDateFormat();

	private List<GuardingReport> reportsAtDay = null;
	private GuardingReport report;

	public GuardingReportPanel() {
	}

	@PostConstruct
	public void init() {
		Calendar today = Calendar.getInstance();
		Set<GuardingReport> reports = HomeguardSingleton.getHomeguard().reportsAt(today);
		reportsAtDay = new ArrayList<>(reports);
	}

	public List<GuardingReport> getReportsAtDay() {
		return reportsAtDay;
	}

	public GuardingReport getReport() {
		return report;
	}

	public boolean isReportShown() {
		return report != null;
	}

	public void showCurrentReport() {
		GuardingReport current = HomeguardSingleton.getHomeguard().currentReport();
		this.report = current;
	}

	public void showLastReport() {
		GuardingReport last = HomeguardSingleton.getHomeguard().lastReport();
		this.report = last;
	}

	public void loadReportsAtDay(SelectEvent event) {
		Calendar day = Calendar.getInstance();
		day.setTime((Date) event.getObject());

		Set<GuardingReport> reports = HomeguardSingleton.getHomeguard().reportsAt(day);
		this.reportsAtDay = new ArrayList<>(reports);
	}

	public void hideReport() {
		this.report = null;
	}

	public void showReport(Calendar date) {
		GuardingReport report = HomeguardSingleton.getHomeguard().getReport(date);
		this.report = report;
	}

	public void saveDescription() {
		Configuration config = HomeguardSingleton.getConfig();
		FileSystemReporter reporter = new FileSystemReporter(config);
		reporter.reportChanged(this.report);
		Utils.info("Uloženo", "Změna uložena");
	}

	public LineChartModel getModelForReport(GuardingReport report) {
		return REPORT_CHARTS.getModel(report);
	}

}
