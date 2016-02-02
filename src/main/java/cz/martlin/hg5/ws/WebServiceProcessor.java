package cz.martlin.hg5.ws;

import java.util.Map;

/**
 * Implements simple web service. This service is invoked by
 * {@link WebServiceServlet} when its registration name matches requested. This
 * processor processes service and returns bytes of response. Processor can
 * throw exception and service will handle it and put into the response. It is
 * assumed that processor returns for each input the same MIME type and it is
 * the type returned by {@link #getContentType()}.
 * 
 * @author martin
 *
 */
public interface WebServiceProcessor {
	/**
	 * Returns MIME type of result of this process.
	 * 
	 * @return
	 */
	public String getContentType();

	/**
	 * Processes given request and returns response as byte array.
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public byte[] process(Map<String, String[]> request) throws Exception;
}
