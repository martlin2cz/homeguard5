package cz.martlin.hg5.logic.process;

/**
 * Represents some service or tool, which runs long time (or infinitelly long)
 * and needs or can be interrupted.
 * 
 * @author martin
 *
 */
public interface Interruptable {
	/**
	 * Interrupts this service or tool.
	 */
	public void interrupt();
}
