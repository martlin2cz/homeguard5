package cz.martlin.hg5.logic.process;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Calendar;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.data.SoundTrack;

/**
 * Performs recording of audio.
 * 
 * @see http://www.codejava.net/coding/capture-and-record-sound-into-wav-file-
 *      with-java-sound-api
 * @see https://docs.oracle.com/javase/tutorial/sound/capturing.html
 * @author martin
 *
 */
public class AudioRecorder implements Serializable, Interruptable {
	private static final long serialVersionUID = 555299331079895740L;
	private final Logger LOG = LogManager.getLogger(getClass());

	public static final AudioFormat FORMAT = createFormat();

	private TargetDataLine line;
	private boolean interrupted;

	public AudioRecorder(Configuration notUsed) {
		initialize();
	}

	/**
	 * Returns true, if is initialized and ready to use.
	 * 
	 * @return
	 */
	public boolean isReady() {
		return (line != null);
	}

	@Override
	public void interrupt() {
		interrupted = true;
	}

	/**
	 * Initializes recorder. If failed, logs, returns false and
	 * {@link #isReady()} will always return false.
	 * 
	 * @return true if succeed, false if failed
	 */
	private boolean initialize() {
		try {
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);
			if (!AudioSystem.isLineSupported(info)) {
				throw new UnsupportedOperationException("Line " + info + " is not supported");
			}

			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(FORMAT);

			LOG.info("Audio recorder ready to record format {}", FORMAT);
			return true;
		} catch (Exception e) {
			LOG.error("Audio recorder init failed", e);
			line = null;
			return false;
		}
	}

	/**
	 * Records one sound track of given length.
	 * 
	 * @return
	 */
	public SoundTrack record(int seconds) {
		if (!isReady()) {
			LOG.warn("Audio recorder not prepared, won't record any shit");
			return null;
		}

		LOG.info("Starting to record sample with lenght {} seconds", seconds);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] data = new byte[line.getBufferSize() / 5];

		Calendar endAt = Calendar.getInstance();
		endAt.add(Calendar.SECOND, seconds);

		line.start();

		while (!interrupted && Calendar.getInstance().before(endAt)) {
			int numBytesReaden = line.read(data, 0, data.length);
			out.write(data, 0, numBytesReaden);
		}

		LOG.info("Recorded {} bytes (samples) of audio data", out.size());

		SoundTrack track = new SoundTrack(out.toByteArray(), FORMAT);
		return track;
	}

	/**
	 * Creates and returns format used for recording. It is necessary to return
	 * format with 8 bits per sample and signed format.
	 * 
	 * @return
	 */
	private static AudioFormat createFormat() {
		int channels = 2;
		int sampleSizeInBits = 8;
		float sampleRate = 16000;
		boolean bigEndian = true;
		boolean signed = true;

		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		return format;
	}
}
