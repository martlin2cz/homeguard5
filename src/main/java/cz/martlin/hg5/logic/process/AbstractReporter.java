package cz.martlin.hg5.logic.process;

import cz.martlin.hg5.logic.data.GuardingReport;
import cz.martlin.hg5.logic.data.ReportItem;
import cz.martlin.hg5.logic.data.SoundTrack;

/**
 * Performs some sort of reporting of records (sends to server, makes beep,
 * ...).
 * 
 * @author martin
 *
 */
public interface AbstractReporter {

	/**
	 * Somehow reports start of recording.
	 * 
	 * @param report
	 */
	public void reportStart(GuardingReport report);

	/**
	 * Somehow reports given item (in context of given report) and/or given
	 * soundrack if is required.
	 * 
	 * @param report
	 * @param item
	 * @param track
	 */
	public void reportItem(GuardingReport report, ReportItem item, SoundTrack track);

	/**
	 * Somehow reports end of recording.
	 * 
	 * @param report
	 */
	public void reportEnd(GuardingReport report);

	/**
	 * Somehow report fact, that given report has been modified (changed start,
	 * end or description).
	 * 
	 * @param report
	 */
	public void reportChanged(GuardingReport report);

}
