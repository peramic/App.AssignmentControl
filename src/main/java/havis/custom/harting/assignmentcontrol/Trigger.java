package havis.custom.harting.assignmentcontrol;

import havis.custom.harting.assignmentcontrol.model.Configuration;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sends start and stop trigger to HARTING RFID ALE Middleware.
 * 
 * 
 */
public class Trigger {

	private final static Logger LOGGER = Logger.getLogger(Trigger.class.getName());
	
	private HttpRequest httpRequest;

	private Configuration configuration;
	
	/**
	 * Constructor.
	 */
	public Trigger(HttpRequest httpRequest, Configuration configuration) {
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
	 * Sends a 'Accept' HTTP-Trigger to the ALE Middleware, set the
	 * OutputPort eg. 'HS4' to 'high'.
	 * 
	 */
	public void sendAccept() throws Exception {
		LOGGER.log(Level.FINE, "Trigger 'SendAccept'");
		String url = this.configuration.getPcOutSendAcceptUrl();
		Integer timeout = this.configuration.getHttpConnectionTimeOut();
		
		httpRequest.send(new URL(url), timeout);
		
	}
	
	/**
	 * Sends a 'observe' HTTP-Trigger to the ALE Middleware for observing all ports.
	 */
	public void observe() throws Exception {
		LOGGER.log(Level.FINE, "Trigger 'Observe'");
		String url = this.configuration.getPcObserveUrl();
		Integer timeout = this.configuration.getHttpConnectionTimeOut();
		
		httpRequest.send(new URL(url), timeout);
	}
	
	/**
	 * System Ready Signal.
	 * 
	 * Sends a 'ready' HTTP-Trigger to the ALE Middleware, set the OutputPort eg. 'HS1' to 'high'.
	 */
	public void systemReady() throws Exception {
		LOGGER.log(Level.FINE, "Trigger 'Ready'");
		String url = this.configuration.getPcOutReadyUrl();
		Integer timeout = this.configuration.getHttpConnectionTimeOut();
		
		httpRequest.send(new URL(url), timeout);
	}
	
	/**
	 * System Active Signal.
	 * 
	 * Sends a 'active' HTTP-Trigger to the ALE Middleware, set the OutputPort eg. 'HS2' to 'high'.
	 */
	public void systemActive() throws Exception {
		LOGGER.log(Level.FINE, "Trigger 'Activate'");
		String url = this.configuration.getPcOutActiveUrl();
		Integer timeout = this.configuration.getHttpConnectionTimeOut();
		
		httpRequest.send(new URL(url), timeout);
	}
	
	/**
	 * System Inactive Signal.
	 * 
	 * Sends a 'inactivate' HTTP-Trigger to the ALE Middleware, set the OutputPort eg. 'HS2' to 'low'.
	 */
	public void systemInactive() throws Exception {
		LOGGER.log(Level.FINE, "Trigger 'Inactivate'");
		String url = this.configuration.getPcOutInactiveUrl();
		Integer timeout = this.configuration.getHttpConnectionTimeOut();
		
		httpRequest.send(new URL(url), timeout);
	}

	/**
	 * Sends 'start scan tags' HTTP-Trigger to ALE Middleware, to start reading
	 * the tags.
	 * 
	 * @throws Exception
	 *             on sending http request to ALE Middleware
	 */
	public void startScan() throws Exception {
		LOGGER.log(Level.FINE, "Trigger 'StartScan'");
		String url = this.configuration.getPcStartscanUrl();
		Integer timeout = this.configuration.getHttpConnectionTimeOut();
		
		httpRequest.send(new URL(url), timeout);
	}

	/**
	 * Sends 'stop scan tags' HTTP-Trigger to ALE Middleware, to stop reading
	 * the tags.
	 * 
	 * @throws Exception
	 *             on sending http request to ALE Middleware
	 */
	public void stopScan() throws Exception {
		LOGGER.log(Level.FINE, "Trigger 'StopScan'");
		String url = this.configuration.getPcStopscanUrl();
		Integer timeout = this.configuration.getHttpConnectionTimeOut();
		
		httpRequest.send(new URL(url), timeout);
	}
}
