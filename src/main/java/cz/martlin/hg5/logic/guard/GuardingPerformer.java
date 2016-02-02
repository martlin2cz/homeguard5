package cz.martlin.hg5.logic.guard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.process.GuardingInstance;
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

	public GuardingPerformer(Configuration config) {
		this.config = config;
		this.reporters = reporters(config);
	}

	/**
	 * If there is no currently running instance, initializes and starts new
	 * guarding instance.
	 */
	public synchronized void start() {
		if (thread == null) {
			thread = new GuardInstanceThread(config, reporters);
			thread.start();
		}
	}

	/**
	 * If there is currently running instance, stops it.
	 */
	public synchronized void stop() {
		if (thread != null) {
			thread.interrupt();
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
			thread = null;
		}
	}

	/**
	 * Returns current instance or null if there is no.
	 * 
	 * @return
	 */
	public synchronized GuardingInstance getCurrentInstance() {
		if (thread != null) {
			return thread.getInstance();
		} else {
			return null;
		}
	}

	/**
	 * Returns true if there is some currently running instance.
	 * 
	 * @return
	 */
	public synchronized boolean isGuardingRunning() {
		return (thread != null);
	}

	private static List<AbstractReporter> reporters(Configuration config) {
		List<AbstractReporter> reporters = new ArrayList<>();

		reporters.add(new FileSystemReporter(config));

		return reporters;
	}

	/**
	 * Thread which encapsulates guarding instance.
	 * 
	 * @author martin
	 *
	 */
	public static class GuardInstanceThread extends Thread implements Interruptable {
		private final GuardingInstance instance;

		public GuardInstanceThread(Configuration config, List<AbstractReporter> reporters) {
			super("GuardInstanceT");
			instance = new GuardingInstance(config, reporters);
		}

		/**
		 * Returns instance running in this thread.
		 * 
		 * @return
		 */
		public GuardingInstance getInstance() {
			return instance;
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
