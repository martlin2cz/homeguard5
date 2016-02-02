package cz.martlin.hg5.logic.process;

import java.util.Calendar;

import cz.martlin.hg5.logic.data.ReportItem;
import cz.martlin.hg5.logic.data.SoundTrack;

/**
 * Represents class which does some magic stuff with samples, counts something
 * or something like that.
 * 
 * @author martin
 *
 */
public interface AbstractAudioProcessor {

	/**
	 * For given raw recorded {@link SoundTrack} creates item of report with
	 * various informations about given track.
	 * 
	 * @param recordedAt
	 * @param track
	 * @return
	 */
	public ReportItem analyzeSample(Calendar recordedAt, SoundTrack track);

}