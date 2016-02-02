package cz.martlin.hg5.logic.processV1.fsman;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.data.GuardingReport;
import cz.martlin.hg5.logic.data.ReportItem;
import cz.martlin.hg5.logic.data.SoundTrack;

/**
 * Implements Reports manager such that reports are stored in log and wav files
 * on the file system.
 * 
 * @author martin
 *
 */
public class FileSystemReportsManager implements Serializable {
	private static final long serialVersionUID = 1577018812775367330L;
	private final Logger LOG = LogManager.getLogger(getClass());

	private final FileSystemManTools tools;

	public FileSystemReportsManager(Configuration config) {
		this.tools = new FileSystemManTools(config);
	}

	/**
	 * Saves metadata (startedAt, endedAt and description) of given report.
	 * 
	 * @param report
	 */
	public void saveMetadataOfReport(GuardingReport report) {
		LOG.info("Saving report's metadata: {}", report);
		File log = tools.logFile(report);
		String content = tools.serializeMetadata(report);
		tools.appendLine(log, content);
	}

	/**
	 * Saves (given) report item of given report.
	 * 
	 * @param report
	 * @param item
	 */
	public void saveItemOfReport(GuardingReport report, ReportItem item) {
		LOG.info("Saving report's new item: {}", item);
		File log = tools.logFile(report);
		String content = tools.serializeReportItem(item);
		tools.appendLine(log, content);
	}

	/**
	 * Saves given soundrack.
	 * 
	 * @param track
	 */
	public void saveSoundTrack(Calendar recordedAt, SoundTrack track) {
		LOG.info("Saving soundtrack into WAV file: {}", track);
		File wav = tools.wavFile(recordedAt);
		tools.saveSoundTrack(wav, track);
	}

	/**
	 * Loads item of report from line of log file.
	 * 
	 * @param recordedAt
	 * @return
	 */
	public ReportItem loadItemOfReport(String line) {
		LOG.info("Loading report item from line: {}", line);
		return tools.deserializeReportItem(line);
	}

	/**
	 * Loads soundtrack given by datetime of record.
	 * 
	 * @param recordedAt
	 * @return
	 */
	public SoundTrack loadSoundTrack(Calendar recordedAt) {
		LOG.info("Loading record as WAV recorded at {}", recordedAt.getTime());
		File wav = tools.wavFile(recordedAt);
		return tools.loadSoundTrack(wav);
	}

	public byte[] loadRawSoundTrackWAV(Calendar recordedAt) {
		File file = tools.wavFile(recordedAt);
		return tools.loadRawSoundTrackWavBytes(file);
	}

	
	/**
	 * Loads whole report by given start datetime.
	 * 
	 * @param startedAt
	 * @return
	 */
	public GuardingReport loadReport(Calendar startedAt) {
		LOG.debug("Loading report started at: " + startedAt.getTime());
		TreeSet<GuardingReport> reports = loadAllReports();

		GuardingReport report = reports.stream().filter(//
				(GuardingReport r) -> startedAt.equals(r.getStartedAt()//
		)).findAny().get();

		return report;
	}

	/**
	 * Loads all reports.
	 * 
	 * @return
	 */
	public TreeSet<GuardingReport> loadAllReports() {
		LOG.debug("Loading all reports");
		TreeSet<GuardingReport> reports = new TreeSet<>();

		Set<File> logs = tools.logsFiles(null);
		logs.forEach((File logFile) -> reports.add(tools.parseLogFile(logFile)));

		return reports;
	}

	/**
	 * Loads reports started at given day.
	 * 
	 * @param day
	 * @return
	 */
	public TreeSet<GuardingReport> loadReportsAtDay(Calendar day) {
		LOG.debug("Loading reports at day: {}", day.getTime());
		TreeSet<GuardingReport> reports = new TreeSet<>();

		Set<File> logs = tools.logsFiles(day);
		logs.forEach((File logFile) -> reports.add(tools.parseLogFile(logFile)));

		return reports;
	}

	/**
	 * Loads last recorded report. In fact returns report of the last recording
	 * started.
	 * 
	 * @return
	 */
	public GuardingReport loadLastReport() {
		LOG.debug("Loading last report");
		TreeSet<GuardingReport> reports = loadAllReports();
		return reports.last();
		/// TODO FIXME nešlo by to vylepšit?
	}


}
