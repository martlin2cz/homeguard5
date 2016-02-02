package cz.martlin.hg5.web.charts;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;

import cz.martlin.hg5.logic.data.GuardingReport;
import cz.martlin.hg5.logic.data.ReportItem;

public class GuardingReportChart implements Serializable {
	private static final long serialVersionUID = 9168115122709337077L;

	private final Logger LOG = LogManager.getLogger(getClass());

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd, HH:mm:SS");

	public GuardingReportChart() {
	}

	
	public LineChartModel getModel(GuardingReport report) {

		LOG.info("Konstruuje se model grafu pro report: {},", report);
		LineChartModel model = new LineChartModel();

		model.setTitle("Záznamy");
		model.setLegendPosition("e");
		model.setAnimate(true);

		initXaxis(model, report);
		initYaxis(model);

		addReportDataSeries(model, report);

		return model;
	}

	private void addReportDataSeries(LineChartModel model, GuardingReport report) {
		ChartSeries warns = new ChartSeries();
		ChartSeries errs = new ChartSeries();

		warns.setLabel("varovné záznamy");
		errs.setLabel("kritické záznamy");

		for (ReportItem item : report.getItems()) {
			String when = DATE_FORMAT.format(item.getRecordedAt().getTime());
			warns.set(when, item.getWarningSamplesRatio());
			errs.set(when, item.getCriticalSamplesRatio());
		}

		model.addSeries(warns);
		model.addSeries(errs);
	}

	private void initXaxis(LineChartModel model, GuardingReport report) {
		DateAxis axis = createXaxis(report);
		model.getAxes().put(AxisType.X, axis);
	}

	private void initYaxis(LineChartModel model) {
		Axis axis = model.getAxis(AxisType.Y);
		axis.setLabel("Úroveň záznamu");
		axis.setMin(0.0);
		// axis.setMin(1.0);
	}

	private DateAxis createXaxis(GuardingReport report) {
		DateAxis xaxis = new DateAxis("Čas záznamu");
		xaxis.setTickFormat("%a %#d., %#H:%M");
		xaxis.setTickAngle(-50);
		// xaxis.setTickInterval("1");

		Calendar started = Calendar.getInstance();
		Calendar ended = Calendar.getInstance();

		if (report.getItemsCount() > 0) {
			started.setTime(report.getItems().get(0).getRecordedAt().getTime());
			ended.setTime(report.getItems().get(report.getItemsCount() - 1).getRecordedAt().getTime());
		}

		started.add(Calendar.HOUR, -1);
		ended.add(Calendar.HOUR, 1);

		xaxis.setMin(DATE_FORMAT.format(started.getTime()));
		xaxis.setMax(DATE_FORMAT.format(ended.getTime()));

		return xaxis;
	}
}
