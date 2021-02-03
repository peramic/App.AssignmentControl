package havis.custom.harting.assignmentcontrol.config;

import havis.custom.harting.assignmentcontrol.model.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigurationManager {
	private final static Logger LOGGER = Logger.getLogger(ConfigurationManager.class.getName());
	
	private Configuration configuration;
	private ObjectMapper mapper = new ObjectMapper();

	public ConfigurationManager(){
		try {
			File file = new File(Environment.CONFIG_FILE);
			Configuration readConfiguration = mapper.readValue(file, Configuration.class);
			configuration = validate(readConfiguration);
		} catch (FileNotFoundException e) {
			configuration = getConfiguration();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Loading config file failed!", e);
			configuration = getConfiguration();
		}
	}

	public Configuration get() {
		return configuration;
	}

	public void set(Configuration config) throws IOException, IllegalArgumentException {
		File configFile = new File(Environment.CONFIG_FILE);
		Files.createDirectories(configFile.toPath().getParent(), new FileAttribute<?>[] {});
		mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, config);
		configuration = config;
	}

	private Configuration validate(Configuration config) throws IllegalArgumentException {
		return config;
	}
	
	@SuppressWarnings("deprecation")
	private Configuration getConfiguration() {
		Configuration configuration = new Configuration();
		
		configuration.setAssignmentUri(Environment.ASSIGNMENT_URI.toString());
		configuration.setHttpConnectionTimeOut(Environment.HTTP_CONNECTION_TIMEOUT);
		configuration.setLocationName(Environment.LOCATION_NAME);
		configuration.setLocationId(Environment.LOCATION_ID);
		configuration.setTriggerHoldTime(Environment.TRIGGER_HOLD_TIME);
		configuration.setScanTrigger(Environment.SCAN_TRIGGER);
		configuration.setActiveSwitchAvailable(Environment.ACTIVE_SWITCH_AVAILABLE);

		configuration.setPcOutReadyUrl(Environment.PC_OUT_READY_URL.toString());
		configuration.setPcOutActiveUrl(Environment.PC_OUT_ACTIVE_URL.toString());
		configuration.setPcOutInactiveUrl(Environment.PC_OUT_INACTIVE_URL.toString());
		configuration.setPcOutSendAcceptUrl(Environment.PC_OUT_SEND_ACCEPT_URL.toString());

		configuration.setPcStopscanUrl(Environment.PC_STOP_SCAN_URL.toString());
		configuration.setPcStartscanUrl(Environment.PC_START_SCAN_URL.toString());
		configuration.setPcObserveUrl(Environment.PC_OBSERVE_URL.toString());
		
		configuration.setGateControl(Environment.GATE_CONTROL);
		configuration.setTagTimeout(Environment.TAG_TIMEOUT);
		
		return configuration;
	}
}
