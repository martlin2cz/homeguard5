package cz.martlin.hg5.logic.processV1_2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.util.Log;

import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.data.GuardingReport;
import cz.martlin.hg5.logic.data.ReportItem;
import cz.martlin.hg5.logic.data.SoundTrack;
import cz.martlin.hg5.logic.process.AbstractReporter;
import cz.martlin.hg5.logic.processV1.fsman.FileSystemManTools;
//import cz.martlin.homeguard.dbx.DropboxFilesProcessor;

public class SimpleDropboxReporter implements Serializable, AbstractReporter {
	private static final long serialVersionUID = -3035828287114821484L;
	private final Logger LOG = LogManager.getLogger(getClass());

	// private final DropboxFilesProcessor dbx;
	private final FileSystemManTools tools;

	public SimpleDropboxReporter(Configuration config) {
		tools = new FileSystemManTools(config);
		// dbx = new DropboxFilesProcessor();
	}

	@Override
	public void reportStart(GuardingReport report) {

	}

	@Override
	public void reportItem(GuardingReport report, ReportItem item, SoundTrack track) {
		if (!item.isWarning()) {
			return;
		}

		File file = tools.logFile(report);

		InputStream fis;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			LOG.error("Failed load report file", e);
			return;
		}

		// BufferedInputStream bis = new BufferedInputStream(fis);
		// String name = "report.log";
		// dbx.uploadFile(bis, name);
		
		IOUtils.closeQuietly(fis);	//TODO rly required?
		Log.info("Report file uploaded");
	}

	@Override
	public void reportEnd(GuardingReport report) {

	}

	@Override
	public void reportChanged(GuardingReport report) {

	}

}
