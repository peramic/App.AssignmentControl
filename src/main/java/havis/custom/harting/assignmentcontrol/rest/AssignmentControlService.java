package havis.custom.harting.assignmentcontrol.rest;

import havis.custom.harting.assignmentcontrol.Main;
import havis.custom.harting.assignmentcontrol.exception.AssignmentControlException;
import havis.custom.harting.assignmentcontrol.model.Configuration;
import havis.net.rest.shared.Resource;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("custom/harting/assignmentcontrol")
public class AssignmentControlService extends Resource {
	private final static Logger LOGGER = Logger.getLogger(AssignmentControlService.class.getName());
	private Main main;

	public AssignmentControlService(Main main) {
		this.main = main;
	}

	@PermitAll
	@GET
	@Path("config")
	@Produces({ MediaType.APPLICATION_JSON })
	public Configuration getConfiguration() {
		return main.getConfiguration();
	}

	@PermitAll
	@PUT
	@Path("config")
	@Consumes({ MediaType.APPLICATION_JSON })
	public void setConfig(Configuration configuration) throws AssignmentControlException {
		if (configuration == null) {
			throw new AssignmentControlException("Set config failed! Configuration object is null.");
		} else {
			try {
				main.setConfiguration(configuration);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Set configuration failed", e);
				throw new AssignmentControlException("Set configuration failed", e);
			}
		}
	}

	@PermitAll
	@PUT
	@Path("accept")
	public void sendAccept() throws AssignmentControlException {
		try {
			main.sendAccept();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Send accept failed", e);
			throw new AssignmentControlException("Send accept failed", e);
		}
	}
}