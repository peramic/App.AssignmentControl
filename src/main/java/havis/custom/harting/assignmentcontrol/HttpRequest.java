package havis.custom.harting.assignmentcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;

/**
 * Class for triggering a http requests.
 * 
 */
public class HttpRequest {

	private final static Logger LOGGER = Logger.getLogger(HttpRequest.class.getName());
	
	/**
	 * Splits a http query to "key value" pairs.
	 * 
	 * @param uri
	 *            HTTP URI
	 * @return HTTP-Query key value parameter as Map<String, String>
	 * @throws UnsupportedEncodingException
	 */
	public Map<String, String> splitQuery(URI uri) throws UnsupportedEncodingException {
		final Map<String, String> query_pairs = new LinkedHashMap<String, String>();

		try {
			if (uri.getRawQuery() != null) {
				final String[] pairs = uri.getRawQuery().split("&");

				for (String pair : pairs) {
					final int idx = pair.indexOf("=");
					final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
					final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
					query_pairs.put(key, value);
				}
			}
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "Expand URI Query failed", exc);
		}

		return query_pairs;
	}

	/**
	 * @see HttpRequest#send(String, URL, String)
	 */
	public String send(URL url, Integer timeout) throws Exception {
		return send(null, url, null, null, timeout);
	}

	/**
	 * Sends json payload to given url via POST method.
	 * 
	 * If parameter 'json' is not null then http header will be set to
	 * 
	 * <pre>
	 * Request Method='POST' 
	 * Accept-Charset='UTF-8'
	 * Content-Type='application/json'
	 * Accept='application/json'
	 * </pre>
	 * 
	 * otherwise no http header will be explicitly set and the payload in this
	 * case is empty.
	 * 
	 * @param json
	 *            object to be send
	 * @param url
	 *            http address
	 * @param authorization
	 *            Base64 authorization string
	 * @return response of requested message. In this case it will be the id of
	 *         the tag in the remote database.
	 * @throws Exception
	 */
	public String post(String json, URL url, String authorization, Integer timeOut) throws Exception {
		return send(json, url, "POST", authorization, timeOut);
	}

	/**
	 * Sends json payload to given url via PUT method.
	 * 
	 * If parameter 'json' is not null then http header will be set to
	 * 
	 * <pre>
	 * Request Method='PUT' 
	 * Accept-Charset='UTF-8'
	 * Content-Type='application/json'
	 * Accept='application/json'
	 * </pre>
	 * 
	 * otherwise no http header will be explicitly set and the payload in this
	 * case is empty.
	 * 
	 * @param json
	 *            object to be send
	 * @param url
	 *            http address
	 * @param authorization
	 *            Base64 authorization string
	 * @return response of requested message. In this case it will be the id of
	 *         the tag in the remote database.
	 * @throws Exception
	 */
	public String put(String json, URL url, String authorization, Integer timeOut) throws Exception {
		return send(json, url, "PUT", authorization, timeOut);
	}

	/**
	 * Sends json payload to given url.
	 * 
	 * If parameter 'json' is not null then http header will be set to
	 * 
	 * <pre>
	 * Request Method='POST|POST|GET' 
	 * Accept-Charset='UTF-8'
	 * Content-Type='application/json'
	 * Accept='application/json'
	 * </pre>
	 * 
	 * otherwise no http header will be explicitly set and the payload in this
	 * case is empty.
	 * 
	 * @param json
	 *            object to be send
	 * @param url
	 *            http address
	 * @param method
	 *            to set
	 * @param authorization
	 *            Base64 authorization string
	 * @return response of requested message. In this case it will be the id of
	 *         the tag in the remote database.
	 * @throws Exception
	 */
	private String send(String json, URL url, String method, String authorization, Integer timeOut) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		if (timeOut != null) {
			connection.setConnectTimeout(timeOut);
		}

		try {
			if (json != null) {
				// add rest request header
				connection.setRequestMethod(method);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");
				connection.setRequestProperty("Accept-Charset", "utf-8");

				if (authorization != null) {
					connection.setRequestProperty("Authorization", "Basic " + authorization);
				}

				connection.setDoOutput(true);
				// add payload to request
				connection.getOutputStream().write(json.getBytes("UTF-8"));
			}

			int responseCode = connection.getResponseCode();

			if ((responseCode == HttpURLConnection.HTTP_OK) || (responseCode == HttpURLConnection.HTTP_NO_CONTENT)) {
				return getBody(connection.getInputStream());
			} else {

				String responseMessage = connection.getResponseMessage();
				String message = String.format("HTTP operation failed invoking '%s' with status code: %d [%s]", url, responseCode, responseMessage);
				String subMessage = "";

				try {
					subMessage = getBody(connection.getErrorStream());
				} catch (Exception exc) {
					subMessage = String.format("Retrieve ErrorMessage failed. Reason: %s", exc.getMessage());
				}

				message = String.format("%s. Error message: >>>%s<<<", message, subMessage);

				throw new WebApplicationException(message, responseCode);
			}

		} finally {
			connection.disconnect();
		}
	}

	/**
	 * Returns the content of the stream.
	 * 
	 * @param stream
	 *            to be read
	 * 
	 * @return human readable stream content
	 * 
	 * @throws IOException
	 * @link InputStream
	 */
	private String getBody(InputStream stream) throws IOException {
		// retrieve response body
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(stream));
			StringBuffer buffer = new StringBuffer();
			String line;

			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}

			return buffer.toString();
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}
}
