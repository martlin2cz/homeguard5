package cz.martlin.hg5.logic.guard;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.martlin.hg5.logic.config.ConfigLoader;
import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.data.GuardingReport;
import cz.martlin.hg5.logic.data.SoundTrack;
import cz.martlin.hg5.logic.processV1.ImprovedAudioProcessor;
import cz.martlin.hg5.logic.processV1.fsman.FileSystemReportsManager;

/**
 * The main class for performing of guarding and access to resources (records,
 * soundtrack files, logs, ...). Each UI (or other user-accessed layer) should
 * use this class to access to internal guarding stuff. Methods are not
 * commented, sorry, see source and its impl's doc.
 * 
 * @author martin
 *
 */
public class Homeguard implements Serializable {
	private static final long serialVersionUID = 786637193042907969L;

	private final static ConfigLoader configLoader = new ConfigLoader();
	private final GuardingPerformer performer;
	private final FileSystemReportsManager filesMan;
	private final ImprovedAudioProcessor audioProcessor;

	private final Configuration config;

	public Homeguard(Configuration config) {
		this.config = config;
		this.performer = new GuardingPerformer(config);
		this.filesMan = new FileSystemReportsManager(config);
		this.audioProcessor = new ImprovedAudioProcessor(config);
	}

	public Configuration getConfig() {
		return config;
	}

	///////////////////////////////////////////////////////////////////////////

	public synchronized void start() {
		performer.start();
	}

	public synchronized void stop() {
		performer.stop();
	}

	public synchronized boolean isRunning() {
		return performer.isGuardingRunning();
	}

	///////////////////////////////////////////////////////////////////////////

	public GuardingReport getReport(Calendar startedAt) {
		return filesMan.loadReport(startedAt);
	}

	public boolean saveReportsMetadata(GuardingReport report) {
		return filesMan.saveMetadataOfReport(report);
	}

	public synchronized Set<GuardingReport> allReports() {
		return filesMan.loadAllReports();
	}

	public synchronized Set<GuardingReport> reportsAt(Calendar day) {
		return filesMan.loadReportsAtDay(day);
	}

	public synchronized GuardingReport lastReport() {
		return filesMan.loadLastReport();
	}

	public synchronized GuardingReport currentReport() {
		return performer.getCurrentInstance().getReport();
	}

	///////////////////////////////////////////////////////////////////////////
	public SoundTrack getTrack(Calendar recordedAt) {
		return filesMan.loadSoundTrack(recordedAt);
	}

	public double[] getJustSimplySamplesOfTrack(SoundTrack track) {
		return audioProcessor.samplesOfTrack(track);
	}

	public byte[] loadRawWavBytesOfTrack(Calendar recordedAt) {
		return filesMan.loadRawSoundTrackWAV(recordedAt);
	}
	///////////////////////////////////////////////////////////////////////////

	public synchronized void setConfigTo(Configuration config) {
		getConfig().setTo(config);
	}

	public synchronized boolean saveConfig() {
		return configLoader.save(getConfig());
	}

	public synchronized boolean loadConfig() {
		return configLoader.load(getConfig());
	}

	///////////////////////////////////////////////////////////////////////////

	public static Homeguard tryToLoadConfigAndCreate() {
		Configuration config = new Configuration();

		boolean success = configLoader.load(config);

		if (!success) {
			Logger log = LogManager.getLogger(Homeguard.class);
			log.warn("Config file could not be loaded. Will be used default");
		}

		return new Homeguard(config);
	}

}
