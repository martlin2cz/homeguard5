package cz.martlin.hg5.ws.services;

import java.util.Calendar;
import java.util.Map;

import cz.martlin.hg5.web.HomeguardSingleton;
import cz.martlin.hg5.ws.WebServiceProcessor;
import cz.martlin.hg5.ws.WebServiceUtils;

/**
 * For given "at" or "at-unix" datetime parameter finds and load WAV file with
 * corresponding soundtrack.
 * 
 * @author martin
 *
 */
public class AudioRecordService implements WebServiceProcessor {
	private static final String AUDIO_FORMAT = "wav";
	private static final String MIME = "audio/" + AUDIO_FORMAT;

	@Override
	public String getContentType() {
		return MIME;
	}

	@Override
	public byte[] process(Map<String, String[]> request) throws Exception {
		Calendar recordedAt = WebServiceUtils.parseDateTime("at", "at-unix", request);

		byte[] bytes = HomeguardSingleton.get().loadRawWavBytesOfTrack(recordedAt);

		if (bytes == null) {
			throw new IllegalArgumentException("There is no such record at " + recordedAt.getTime());
		} else {
			return bytes;
		}
	}

}
