package havis.custom.harting.assignmentcontrol;

import havis.custom.harting.assignmentcontrol.model.Assignment;
import havis.custom.harting.assignmentcontrol.model.Configuration;
import havis.custom.harting.assignmentcontrol.model.Location;
import havis.custom.harting.assignmentcontrol.model.Tag;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Extension of {@link AssignmentChecker}
 * 
 * Checks tag assignment by remote server.
 * 
 */
public class AssignmentChecker {

	private final static Logger LOGGER = Logger.getLogger(AssignmentChecker.class.getName());

	private final static String LOG_ASSIGNMENT_STATE_PATH = "logAssignmentStatePath";

	private URL checkAssignmentServicePath;
	private URL logAssignmentStateServicePath;

	private HttpRequest httpRequest;

	private String authorization;

	private Configuration configuration;

	/**
	 * Default constructor
	 */
	public AssignmentChecker(HttpRequest httpRequest, Configuration configuration) {
		this.httpRequest = httpRequest;
		this.configuration = configuration;
	}

	/**
	 * Sets the configuration.
	 * 
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Initializes REST address of the remote assignment server.
	 * 
	 * @throws MalformedURLException
	 */
	public void reinit() throws Exception {
		URL url = null;

		try {
			url = new URL(this.configuration.getAssignmentUri());
		} catch (MalformedURLException e) {
			LOGGER.log(Level.SEVERE, String.format("Invalid Assignment URI '%s'", this.configuration.getAssignmentUri()), e);
			url = new URL("http://localhost");
			LOGGER.log(Level.INFO, "Default Assignment URI http://localhost:0 was loaded");
		}

		// authentication data
		String userInfo = url.getUserInfo();

		if ((userInfo != null) && (userInfo.trim().length() > 0)) {
			this.authorization = DatatypeConverter.printBase64Binary(userInfo.getBytes());
		}

		// get http query parameter
		Map<String, String> splitQuery;

		try {
			splitQuery = httpRequest.splitQuery(url.toURI());
		} catch (UnsupportedEncodingException | URISyntaxException e) {
			LOGGER.log(Level.WARNING, String.format("Failure on loading query parameter %s", url), e);
			splitQuery = new HashMap<String, String>();
		}

		String logAssignmentStateServicePath = splitQuery.get(LOG_ASSIGNMENT_STATE_PATH);

		// initialize assignment control rest service url's
		this.checkAssignmentServicePath = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath());

		LOGGER.log(Level.INFO, String.format("Assignment Server URI: %s loaded", this.checkAssignmentServicePath));

		if (logAssignmentStateServicePath != null) {
			this.logAssignmentStateServicePath = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath() + "/" + logAssignmentStateServicePath);

			LOGGER.log(Level.INFO, String.format("Logging URI: %s loaded", this.logAssignmentStateServicePath));
		}
	}

	/**
	 * Sends tag(s) authentication data (tag epc, tag tid and location id) as
	 * json Object to remote assignment server to evaluate the assignment for
	 * tag(s).
	 * 
	 * @param tags
	 *            tag epc's comma separated
	 * @param tids
	 *            tag tid's comma separated
	 * 
	 * 
	 * @return null or empty string when the tag has no assignment to pass the
	 *         location. Valid, not empty string value otherwise.
	 */
	@SuppressWarnings("deprecation")
	public boolean check(List<Tag> tags) throws Exception {

		String json;

		if (this.configuration.isGateControl()) {
			json = gateControlRequest(tags);
		} else {
			json = assignmentControlRequest(tags);
		}

		try {
			httpRequest.post(json, checkAssignmentServicePath, authorization, configuration.getHttpConnectionTimeOut());
			return true;
		} catch (WebApplicationException wae) {
			if (HttpURLConnection.HTTP_NOT_FOUND != wae.getResponse().getStatus()) {
				throw wae;
			}

			LOGGER.log(Level.FINE, "Not assigned", wae);
			return false;
		}

	}

	/**
	 * This method was implemented specially for GateControl.
	 * 
	 * Sends assignment state as json Object to remote assignment server to log
	 * this information on remote server, only if the logAssignmentStatePath is
	 * set as Query parameter.
	 * 
	 * URL example: http://10.10.10.10:8888/application/camel/checkAssignment?logAssignmentStatePath=log
	 * then the log result Path URI will be look like this:
	 * http://10.10.10.10:8888/application/camel/checkAssignment/log
	 * 
	 * Log example on Server side: <### READ ### ... ### access denied ###>
	 * 
	 * @param location
	 *            location name
	 * @param assigned
	 *            location assignment state
	 */
	@Deprecated
	public void logAssignmentState(String location, boolean assigned) {
		Map<String, String> state = new HashMap<>();
		state.put("gateName", location);
		state.put("access", assigned ? "GATE OPENED" : "access DENIED");

		if (this.logAssignmentStateServicePath != null) {
			try {
				String json = new ObjectMapper().writeValueAsString(state);
				httpRequest.put(json, logAssignmentStateServicePath, authorization, configuration.getHttpConnectionTimeOut());
			} catch (Exception exc) {
				LOGGER.log(Level.WARNING, "Unable to log assignment state", exc);
			}
		}
	}

	private String assignmentControlRequest(List<Tag> tags) throws Exception {
		Assignment assignment = new Assignment();
		Location location = new Location(this.configuration.getLocationId(), this.configuration.getLocationName());

		assignment.setLocation(location);
		assignment.setTags(tags);
		
		String writeValueAsString = new ObjectMapper().writeValueAsString(assignment);

		return writeValueAsString;
	}

	@Deprecated
	private String gateControlRequest(List<Tag> tags) throws Exception {
		String epcs = "";
		String tids = "";

		for (Tag tag : tags) {
			epcs += String.format("'%s'", tag.getEpc()) + ",";
			tids += String.format("'%s'", tag.getTid()) + ",";
		}

		if (epcs.endsWith(",") && tids.endsWith(",")) {
			epcs = epcs.substring(0, epcs.length() - 1);
			tids = tids.substring(0, tids.length() - 1);
		}

		Map<String, Object> mainData = new HashMap<>();
		Map<String, Object> tagData = new HashMap<>();

		tagData.put("tags", epcs);
		tagData.put("values", tids);
		tagData.put("gate", this.configuration.getLocationId());

		mainData.put("gateName", this.configuration.getLocationName());
		mainData.put("tagData", tagData);

		String json = new ObjectMapper().writeValueAsString(mainData);

		return json;
	}
}
