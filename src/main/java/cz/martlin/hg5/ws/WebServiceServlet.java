package cz.martlin.hg5.ws;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.martlin.hg5.ws.services.AudioRecordService;
import cz.martlin.hg5.ws.services.CommandService;
import cz.martlin.hg5.ws.services.EchoService;
import cz.martlin.hg5.ws.services.ReportItemChartRenderer;

/**
 * Servlet implementing simple web service.
 * 
 * @author martin
 *
 */
public class WebServiceServlet extends HttpServlet {
	private static final int UNKNOWN_SERVICE_CODE = 404;

	private static final int SERVICE_FAILED_CODE = 500;

	private static final long serialVersionUID = -2971057431348059536L;

	private final Logger LOG = LogManager.getLogger(getClass());

	private static final Map<String, WebServiceProcessor> processors = initializeProcessors();

	public WebServiceServlet() {
	}

	/**
	 * Initializes list processes and their names.
	 * 
	 * @return
	 */
	private static Map<String, WebServiceProcessor> initializeProcessors() {
		Map<String, WebServiceProcessor> processors = new HashMap<>();

		EchoService echo = new EchoService();
		AudioRecordService audio = new AudioRecordService();
		ReportItemChartRenderer graph = new ReportItemChartRenderer();
		CommandService command = new CommandService();

		processors.put("echo", echo);

		processors.put("record", audio);
		processors.put("record.wav", audio);

		processors.put("chart", graph);
		processors.put("chart.png", graph);

		processors.put("cmd", command);
		processors.put("command", command);
		processors.put("command.txt", command);

		return processors;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		path = path.substring(1);

		WebServiceProcessor processor = processors.get(path);
		if (processor != null) {
			process(path, processor, req, resp);
		} else {
			unknownServiceError(path, req, resp);
		}
	}

	/**
	 * Processes given process request with given processor.
	 * 
	 * @param path
	 * @param processor
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void process(String path, WebServiceProcessor processor, HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOG.info("Invoked web service {}  with params {}", processor.getClass().getName(),
				req.getParameterMap().keySet());

		byte[] bytes;
		try {
			bytes = processor.process(req.getParameterMap());
		} catch (Exception e) {
			errorInProcess(path, processor, req, resp, e);
			return;
		}

		String contentType = processor.getContentType();
		resp.setContentType(contentType);

		OutputStream ous = resp.getOutputStream();
		ous.write(bytes);
		resp.setContentLength(bytes.length);
	}

	/**
	 * Sends error reponse with "unknown serice".
	 * 
	 * @param path
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void unknownServiceError(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		LOG.error("Tried to invoke unhandled service {} with params {}", path, req.getParameterMap().keySet());

		StringBuilder msg = new StringBuilder();
		msg.append("Uknown service ");
		msg.append(path);
		msg.append(". Use one of folowing services: ");
		msg.append(processors.keySet());
		msg.append(".");

		resp.sendError(UNKNOWN_SERVICE_CODE, msg.toString());

	}

	/**
	 * Sends error response with "error in processing of process".
	 * 
	 * @param path
	 * @param processor
	 * @param req
	 * @param resp
	 * @param e
	 * @throws IOException
	 */
	private void errorInProcess(String path, WebServiceProcessor processor, HttpServletRequest req,
			HttpServletResponse resp, Exception e) throws IOException {

		LOG.error("Handling of service " + path + " with params " + req.getParameterMap().keySet() + " failed.", e);

		StringBuilder msg = new StringBuilder();
		msg.append("Service process failed with error: ");
		msg.append(e);
		msg.append(".");

		resp.sendError(SERVICE_FAILED_CODE, msg.toString());
	}

}
