package cz.martlin.hg5.ws.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import cz.martlin.hg5.Homeguard;
import cz.martlin.hg5.logic.data.GuardingReport;
import cz.martlin.hg5.web.HomeguardSingleton;
import cz.martlin.hg5.ws.WebServiceProcessor;
import cz.martlin.hg5.ws.WebServiceUtils;

/**
 * Implements processor of simple report summary generator. Generates only plain
 * string with simple description of recorded samples of given report.
 * 
 * @author martin
 *
 */
public class ReportSummaryService implements WebServiceProcessor {

	private static final String MIME = "text/plain";
	private static final String REPORT_SPEC_ATTR_NAME = "report";
	private static final String CURRENT_REP_ATTR_VAL = "current";
	private static final String LAST_REP_ATTR_VAL = "last";
	private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM., HH:mm:ss");

	public ReportSummaryService() {
	}

	@Override
	public String getContentType() {
		return MIME;
	}

	@Override
	public byte[] process(Map<String, String[]> request) throws Exception {
		Homeguard homeguard = HomeguardSingleton.get();
		String which = WebServiceUtils.getString(REPORT_SPEC_ATTR_NAME, request);

		GuardingReport report;
		if (which == null) {
			if (homeguard.isRunning()) {
				report = homeguard.currentReport();
			} else {
				report = homeguard.lastReport();
			}
		} else if (CURRENT_REP_ATTR_VAL.equals(which)) {
			if (homeguard.isRunning()) {
				report = homeguard.currentReport();
			} else {
				report = null;
			}
		} else if (LAST_REP_ATTR_VAL.equals(which)) {
			report = homeguard.lastReport();
		} else {
			Calendar when = WebServiceUtils.parseDateTime(REPORT_SPEC_ATTR_NAME, REPORT_SPEC_ATTR_NAME, request);
			report = homeguard.getReport(when);
		}

		return createMessage(report).getBytes();
	}

	private String createMessage(GuardingReport reportOrNull) {
		if (reportOrNull == null) {
			return "No report avaible";
		}
		GuardingReport report = reportOrNull;

		return reportToString(report);
	}

	private String reportToString(GuardingReport report) {
		StringBuilder stb = new StringBuilder();

		stb.append("Zaznamenávání bylo spuštěno ");
		stb.append(FORMAT.format(report.getStartedAt().getTime()));

		if (report.getStoppedAt() != null) {
			stb.append(" a zastaveno ");
			stb.append(FORMAT.format(report.getStoppedAt().getTime()));
			stb.append(". ");
		} else {
			stb.append("a ještě stále beží. ");
		}

		stb.append("Bylo provedeno celkem ");
		stb.append(report.getItemsCount());
		stb.append(" záznamů, z nichž bylo ");
		stb.append(report.getWarningsCount());
		stb.append(" (");
		stb.append(String.format("%.0f%%", report.getWarningsRatio() * 100));
		stb.append(") varovných a ");
		stb.append(report.getCriticalCount());
		stb.append(" (");
		stb.append(String.format("%.0f%%", report.getCriticalsRatio() * 100));
		stb.append(") dokonce kritických.");

		return stb.toString();
	}

}
