package cz.martlin.hg5.web;

import java.io.Serializable;

import cz.martlin.hg5.Homeguard;
import cz.martlin.hg5.logic.config.Configuration;

/**
 * Class providing singleton access to one Homeguard instance.
 * 
 * @author martin
 *
 */
public class HomeguardSingleton implements Serializable {
	private static final long serialVersionUID = -8284209919770953819L;

	private static Homeguard homeguard = null;

	private HomeguardSingleton() {
	}

	/**
	 * Gets homeguard instance.
	 * 
	 * @return
	 */
	public static synchronized Homeguard get() {
		if (homeguard == null) {
			homeguard = Homeguard.tryToLoadConfigAndCreate();
		}

		return homeguard;
	}

	/**
	 * Just an more describing alias for {@link #get()} (just only calls
	 * {@link #get()} internally).
	 * 
	 * @return
	 */
	public static synchronized Homeguard getHomeguard() {
		return get();
	}

	/**
	 * Returns config of current homeguard instance.
	 * 
	 * @return
	 */
	public static Configuration getConfig() {
		return get().getConfig();
	}

}
