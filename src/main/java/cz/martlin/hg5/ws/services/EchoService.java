package cz.martlin.hg5.ws.services;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;

import cz.martlin.hg5.ws.WebServiceProcessor;

/**
 * Implements simple echo. Just prints timestamp and given parameters. Optionaly
 * can throw an exception (to test exceptioning).
 * 
 * @author martin
 *
 */
public class EchoService implements WebServiceProcessor {

	public EchoService() {
	}

	@Override
	public String getContentType() {
		return "text/plain";
	}

	@Override
	public byte[] process(Map<String, String[]> request) throws Exception {
		StringBuilder stb = new StringBuilder();
		stb.append("At ");
		stb.append(Calendar.getInstance().getTime());
		stb.append(" invoked echo service with params:\n");
		for (Entry<String, String[]> entry : request.entrySet()) {
			stb.append(entry.getKey());
			stb.append("=");
			stb.append(Arrays.asList(entry.getValue()));
			stb.append("\n");

		}
		stb.append("That's all folks.\n");

		stb.append(
				"If you call this service with throw=(some text) will be thrown RuntimeException with given text. Try it!\n");
		String[] error = request.get("throw");
		if (error != null) {
			throw new RuntimeException(Arrays.asList(error).toString());
		}

		return stb.toString().getBytes();
	}

}
