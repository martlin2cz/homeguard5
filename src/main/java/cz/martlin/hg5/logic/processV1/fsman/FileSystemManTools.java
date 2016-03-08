package cz.martlin.hg5.logic.processV1.fsman;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.data.GuardingReport;
import cz.martlin.hg5.logic.data.ReportItem;
import cz.martlin.hg5.logic.data.SoundTrack;
import cz.martlin.hg5.logic.processV1.ImprovedAudioProcessor;

public class FileSystemManTools implements Serializable {
	private static final long serialVersionUID = 7701899175538486900L;

	private final Logger LOG = LogManager.getLogger(getClass());

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private final ImprovedAudioProcessor processor;

	private final Configuration config;

	public FileSystemManTools(Configuration config) {
		this.config = config;
		this.processor = new ImprovedAudioProcessor(config);
	}

	/**
	 * Returns list of log files of given day (or all, if dayOrNull is really
	 * null).
	 * 
	 * @param dayOrNull
	 * @return
	 */
	public Set<File> logsFiles(Calendar dayOrNull) {
		File logsDir = logsDir();
		try {
			Set<File> files = Files.list(logsDir.toPath()).map((path) -> path.toFile()).collect(Collectors.toSet());
			if (dayOrNull != null) {
				files = files.stream().filter((File file) -> isLogFileAtDay(file, dayOrNull))
						.collect(Collectors.toSet());
			}
			return files;
		} catch (IOException e) {
			LOG.error("Cannot load logs", e);
			return null;
		}
	}

	/**
	 * Parses given logFile and loads its content - {@link GuardingReport}
	 * instance.
	 * 
	 * @param logFile
	 * @return
	 */
	public GuardingReport parseLogFile(File logFile) {
		FileReader fr = null;
		try {
			fr = new FileReader(logFile);
			List<String> lines = IOUtils.readLines(fr);
			return deserializeLogFileLines(lines);
		} catch (Exception e) {
			LOG.error("Cannot read content of log file " + logFile, e);
			return null;
		} finally {
			IOUtils.closeQuietly(fr);
		}
	}

	/**
	 * Parses given list of lines of log file.
	 * 
	 * @param lines
	 * @return
	 */
	private GuardingReport deserializeLogFileLines(List<String> lines) {
		GuardingReport report = new GuardingReport();

		for (String line : lines) {
			if (line.startsWith("startedAt") || line.startsWith("stoppedAt") || line.startsWith("description")) {
				deserializeMetadata(line, report);
			} else {
				ReportItem item = deserializeReportItem(line);
				report.addReportItem(item);
			}
		}

		return report;

	}

	/**
	 * Parses given line with metadata and sets into given report
	 * 
	 * @param line
	 * @param report
	 */
	private void deserializeMetadata(String line, GuardingReport report) {
		Map<String, String> map = parseMap(line);

		report.setStarted(parseDate(map.get("startedAt")));
		report.setStopped(parseDate(map.get("stoppedAt")));
		report.setDescription(map.get("description"));
	}

	/**
	 * Serializes given report's metadata into line with metadata.
	 * 
	 * @param report
	 * @return
	 */
	public String serializeMetadata(GuardingReport report) {
		Map<String, Object> map = new LinkedHashMap<>();

		map.put("startedAt", formatDate(report.getStartedAt()));
		map.put("stoppedAt", formatDate(report.getStoppedAt()));
		map.put("description", report.getDescription());

		return serializeMap(map);
	}

	/**
	 * Serializes given report item into line of log file.
	 * 
	 * @param item
	 * @return
	 */
	public String serializeReportItem(ReportItem item) {
		Map<String, Object> map = new LinkedHashMap<>();

		map.put("recordedAt", formatDate(item.getRecordedAt()));
		map.put("lenghtInSeconds", item.getLenghtInSeconds());
		map.put("samplesCount", item.getSamplesCount());
		map.put("criticalMaxNoiseAmount", item.getCriticalNoiseAmount());
		map.put("criticalNoiseThreshold", item.getCriticalNoiseThreshold());
		map.put("criticalSamplesCount", item.getCriticalSamplesCount());
		map.put("maxWarningNoiseAmount", item.getWarningNoiseAmount());
		map.put("warningNoiseThreshold", item.getWarningNoiseThreshold());
		map.put("warningSamplesCount", item.getWarningSamplesCount());

		return serializeMap(map);
	}

	/**
	 * Parses given line as report item.
	 * 
	 * @param line
	 * @return
	 */
	public ReportItem deserializeReportItem(String line) {
		Map<String, String> map = parseMap(line);

		Calendar recordedAt = parseDate(map.get("recordedAt"));
		int samplesCount = Integer.parseInt(map.get("samplesCount"));
		int lenghtInSeconds = Integer.parseInt(map.getOrDefault("lenghtInSeconds", "0"));

		double maxCriticalNoiseAmount = Double
				.parseDouble(map.getOrDefault("criticalMaxNoiseAmount", map.get("errorMaxNoiseAmount")));
		double criticalNoiseThreshold = Double
				.parseDouble(map.getOrDefault("criticalNoiseThreshold", map.get("errorNoiseThreshold")));
		int criticalSamplesCount = Integer
				.parseInt(map.getOrDefault("criticalSamplesCount", map.get("errorSamplesCount")));
		double maxWarningNoiseAmount = Double.parseDouble(map.get("maxWarningNoiseAmount"));
		double warningNoiseThreshold = Double.parseDouble(map.get("warningNoiseThreshold"));
		int warningSamplesCount = Integer.parseInt(map.get("warningSamplesCount"));

		
		return new ReportItem(recordedAt, lenghtInSeconds, samplesCount, warningNoiseThreshold,
				criticalNoiseThreshold, maxWarningNoiseAmount, maxCriticalNoiseAmount, warningSamplesCount,
				criticalSamplesCount);
	}

	/**
	 * Tries to find file of soundtrack and mine samples of its content.
	 * 
	 * @param recordedAt
	 * @return
	 */
	public double[] tryToLoadSamplesOfItem(Calendar recordedAt) {
		File file = wavFile(recordedAt);
		if (!file.exists()) {
			return null;
		}

		SoundTrack track = loadSoundTrack(file);
		return processor.samplesOfTrack(track);
	}

	/**
	 * Saves given sound track into given file.
	 * 
	 * @param file
	 * @param track
	 * @return true if success
	 */
	public boolean saveSoundTrack(File file, SoundTrack track) {
		InputStream bais = null;
		AudioInputStream ais = null;
		try {
			bais = new ByteArrayInputStream(track.getBytes());
			ais = new AudioInputStream(bais, track.getFormat(), track.getBytes().length);
			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
			return true;
		} catch (IOException e) {
			LOG.error("Cannot write WAV", e);
			return false;
		} finally {
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(ais);
		}
	}

	/**
	 * Loads sound track from given file.
	 * 
	 * @param file
	 * @return
	 */
	public SoundTrack loadSoundTrack(File file) {
		AudioInputStream ais = null;
		try {
			ais = AudioSystem.getAudioInputStream(file);
			AudioFormat format = ais.getFormat();
			byte[] bytes = IOUtils.toByteArray(ais);
			return new SoundTrack(bytes, format);
		} catch (IOException | UnsupportedAudioFileException e) {
			LOG.error("Cannot read wav", e);
			return null;
		} finally {
			IOUtils.closeQuietly(ais);
		}
	}

	/**
	 * Writes next line into given file.
	 * 
	 * @param file
	 * @param message
	 * @return true if success
	 */
	public boolean appendLine(File file, String message) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
			writer.write(message);
			writer.write(System.lineSeparator());
			return true;
		} catch (IOException e) {
			LOG.error("Cannot append line", e);
			return false;
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	/**
	 * Loads binary data of given wav file (for playing as normal wav file).
	 * 
	 * @param file
	 * @return
	 */
	public byte[] loadRawSoundTrackWavBytes(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return IOUtils.toByteArray(fis);
		} catch (IOException e) {
			LOG.error("Cannot load wav content", e);
			return null;
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

	/**
	 * Returns true, if is given file log file of report of recording started at
	 * given day.
	 * 
	 * @param file
	 * @param day
	 * @return
	 */
	public boolean isLogFileAtDay(File file, Calendar day) {
		String dayStr = DAY_FORMAT.format(day.getTime());
		return file.getName().contains(dayStr);
	}

	public File logFile(GuardingReport report) {
		File logsDir = logsDir();
		File logFile = new File(logsDir, logFileName(report.getStartedAt()));
		return logFile;
	}

	private File logsDir() {
		return new File(config.getLogsRootDir(), "logs");
	}

	public File wavFile(Calendar recordedAt) {
		File samplesDir = samplesDir();
		File sampleFile = new File(samplesDir, wavFileName(recordedAt));
		return sampleFile;
	}

	private File samplesDir() {
		return new File(config.getLogsRootDir(), "samples");
	}

	private String logFileName(Calendar startedAt) {
		return "log-" + formatDate(startedAt) + ".log";
	}

	private String wavFileName(Calendar recordedAt) {
		return "sample-" + formatDate(recordedAt) + ".wav";
	}

	/**
	 * Parses line with format key1=value1,[tab]key2=value2,... into map
	 * 
	 * @param line
	 * @return
	 */
	private static Map<String, String> parseMap(String line) {
		String[] parts = line.split(",\t");

		if (parts[parts.length - 1].endsWith(",")) {
			String part = parts[parts.length - 1];
			parts[parts.length - 1] = part.substring(0, part.length() - 1);
		}

		Map<String, String> map = new HashMap<>(parts.length);
		for (String part : parts) {
			String[] keyval = part.split("=");
			if (keyval.length != 2) {
				throw new IllegalArgumentException(part + " is not a pair");
			}
			map.put(keyval[0], keyval[1]);
		}

		return map;
	}

	/**
	 * Serializes map into format key1=value1,[tab]key2=value2,...
	 * 
	 * @param map
	 * @return
	 */
	public static String serializeMap(Map<String, Object> map) {
		StringBuilder stb = new StringBuilder();

		for (Entry<String, Object> entry : map.entrySet()) {
			String value = entry.getValue() != null ? entry.getValue().toString() : null;
			stb.append(entry.getKey());
			stb.append("=");
			stb.append(value);
			stb.append(",\t");
		}

		return stb.toString();
	}

	public String formatDate(Calendar date) {
		if (date == null) {
			return null;
		} else {
			return FORMAT.format(date.getTime());
		}
	}

	public Calendar parseDate(String string) {
		if (string == null || "null".equals(string)) {
			return null;
		}

		Calendar cal = GregorianCalendar.getInstance();
		try {
			cal.setTime(FORMAT.parse(string));
		} catch (ParseException e) {
			LOG.error("Cannot parse datetime: " + string, e);
			return null;
		}
		return cal;
	}

}
