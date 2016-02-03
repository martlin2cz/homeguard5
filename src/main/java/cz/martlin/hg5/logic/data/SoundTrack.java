package cz.martlin.hg5.logic.data;

import java.io.Serializable;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;

/**
 * Simple audio track. Has a format and raw bytes of audio data. Assumes format
 * with 8 bits per sample.
 * 
 * @author martin
 *
 */
public class SoundTrack implements Serializable {
	private static final long serialVersionUID = 4225340778055694664L;

	private final byte[] bytes;
	private final AudioFormat format;

	public SoundTrack(byte[] bytes, AudioFormat format) {
		this.bytes = bytes;
		this.format = format;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public AudioFormat getFormat() {
		return format;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + Arrays.hashCode(bytes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SoundTrack other = (SoundTrack) obj;
		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.equals(other.format))
			return false;
		if (!Arrays.equals(bytes, other.bytes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SoundTrack [bytes=" + bytes.length + ", format=" + format + "]";
	}

}
