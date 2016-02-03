package cz.martlin.hg5.ws.services;

import java.util.Map;

import cz.martlin.hg5.web.HomeguardSingleton;
import cz.martlin.hg5.ws.WebServiceProcessor;

/**
 * Processes simple commands (start, stop, status). Response is send back as
 * simple plain text sequence.
 * 
 * @author martin
 *
 */
public class CommandService implements WebServiceProcessor {

	private static final String MIME = "text/plain";

	public CommandService() {
	}

	@Override
	public String getContentType() {
		return MIME;
	}

	@Override
	public byte[] process(Map<String, String[]> request) throws Exception {
		String message;
		if (request.get("start") != null) {
			message = processStart();
		} else if (request.get("stop") != null) {
			message = processStop();
		} else if (request.get("status") != null) {
			message = processStatus();
		} else {
			throw new IllegalArgumentException("Unknown command. Allowed are start, stop and status.");
		}

		return message.getBytes();
	}

	private String processStart() {
		HomeguardSingleton.get().start();
		return "Homeguard started and running";
	}

	private String processStop() {
		HomeguardSingleton.get().stop();
		return "Homeguard stopped and not running";
	}

	private String processStatus() {
		if (HomeguardSingleton.get().isRunning()) {
			return "Homeguard is started and running";
		} else {
			return "Homeguard is stopped and not running";
		}
	}

}
