package cz.martlin.hg5.logic.processV1;

import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.data.GuardingReport;
import cz.martlin.hg5.logic.data.ReportItem;
import cz.martlin.hg5.logic.data.SoundTrack;
import cz.martlin.hg5.logic.process.AbstractReporter;
import cz.martlin.hg5.logic.processV1.fsman.FileSystemReportsManager;

/**
 * Implements records reporter via {@link FileSystemReportsManager}. Each entry
 * logs into log file and warning entries additionally saves into WAV files.
 * 
 * @author martin
 *
 */
public class FileSystemReporter implements AbstractReporter {

	private final FileSystemReportsManager manager;

	public FileSystemReporter(Configuration config) {
		this.manager = new FileSystemReportsManager(config);
	}

	@Override
	public void reportStart(GuardingReport report) {
		manager.saveMetadataOfReport(report);
	}

	@Override
	public void reportEnd(GuardingReport report) {
		manager.saveMetadataOfReport(report);
	}

	@Override
	public void reportItem(GuardingReport report, ReportItem item, SoundTrack track) {
		manager.saveItemOfReport(report, item);

		if (item.isWarning()) {
			manager.saveSoundTrack(item.getRecordedAt(), track);
		}
	}

	@Override
	public void reportChanged(GuardingReport report) {
		manager.saveMetadataOfReport(report);
	}

}
