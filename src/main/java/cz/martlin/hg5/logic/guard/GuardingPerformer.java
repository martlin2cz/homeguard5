package cz.martlin.hg5.logic.guard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.process.AbstractReporter;
import cz.martlin.hg5.logic.process.Interruptable;
import cz.martlin.hg5.logic.processV1.FileSystemReporter;

/**
 * Represents class which is starting point of running particular guarding
 * instance. Contains own thread in which instance runs.
 * 
 * @author martin
 *
 */
public class GuardingPerformer implements Serializable {
	private static final long serialVersionUID = 4187081166801559376L;

	private final List<AbstractReporter> reporters;
	private final Configuration config;

	private GuardInstanceThread thread = null;
	private GuardingInstance instance = null;

	public GuardingPerformer(Configuration config) {
		this.config = config;
		this.reporters = reporters(config);
	}

	/**
	 * Creates list of used reporters.
	 * 
	 * @param config
	 * @return
	 */
	private static List<AbstractReporter> reporters(Configuration config) {
		List<AbstractReporter> reporters = new ArrayList<>();

		reporters.add(new FileSystemReporter(config));

		return reporters;
	}

	/**
	 * If there is no currently running instance, initializes and starts new
	 * guarding instance.
	 */
	public synchronized void start() {
		if (instance == null) {
			instance = new GuardingInstance(config, reporters);
			
			thread = new GuardInstanceThread(config, instance);
			thread.start();
		}
	}

	/**
	 * If there is currently running instance, stops it.
	 */
	public synchronized void stop() {
		if (instance != null) {
			thread.interrupt();
			// try {
			// thread.join();
			// } catch (InterruptedException e) {
			// }
			instance = null;
			thread = null;
		}
	}

	/**
	 * Returns current instance or null if there is no.
	 * 
	 * @return
	 */
	public synchronized GuardingInstance getCurrentInstance() {
		return instance;
	}

	/**
	 * Returns true if there is some currently running instance.
	 * 
	 * @return
	 */
	public synchronized boolean isGuardingRunning() {
		return (instance != null);
	}

	/**
	 * Thread which encapsulates guarding instance.
	 * 
	 * @author martin
	 *
	 */
	public static class GuardInstanceThread extends Thread implements Interruptable {
		private final GuardingInstance instance;

		public GuardInstanceThread(Configuration config, GuardingInstance instance) {
			super("GuardInstanceT");

			this.instance = instance;
		}

		@Override
		public void interrupt() {
			super.interrupt();
			instance.interrupt();
		}

		@Override
		public void run() {
			instance.run();
		}
	}
}
