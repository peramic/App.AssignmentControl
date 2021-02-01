package havis.custom.harting.assignmentcontrol;

import havis.custom.harting.assignmentcontrol.config.Environment;
import havis.custom.harting.assignmentcontrol.osgi.Activator;
import havis.middleware.ale.base.exception.ALEException;
import havis.middleware.ale.config.service.mc.Path;
import havis.middleware.ale.service.mc.MC;
import havis.middleware.ale.service.mc.MCEventCycleSpec;
import havis.middleware.ale.service.mc.MCPortCycleSpec;
import havis.middleware.ale.service.mc.MCSubscriberSpec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.fasterxml.jackson.databind.ObjectMapper;

class McTracker extends ServiceTracker<MC, MC> {
	private static final Logger LOGGER = Logger.getLogger(McTracker.class.getName());

	private Trigger trigger;

	McTracker(BundleContext context, Trigger trigger) {
		super(context, MC.class, null);

		this.trigger = trigger;
	}

	@Override
	public MC addingService(ServiceReference<MC> reference) {
		LOGGER.info("ALE Middleware started");
		MC mc = super.addingService(reference);

		try {

			if (new File(Environment.LOCK).createNewFile()) {
				mc = create(mc);
			}

			trigger.systemReady();
			trigger.observe();
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "Port Trigger failed on Middleware startup", exc);
		} finally {
			close();
		}

		return mc;
	}

	@Override
	public void removedService(ServiceReference<MC> reference, MC service) {
		super.removedService(reference, service);
		LOGGER.info("ALE Middleware stopped");
	}

	@SuppressWarnings("unchecked")
	private MC create(MC mC) throws IOException, ALEException {
		MC mc = mC;

		ObjectMapper mapper = new ObjectMapper();

		Thread.currentThread().setContextClassLoader(Activator.class.getClassLoader());

		for (java.nio.file.Path path : Files.newDirectoryStream(Paths.get(Environment.SPEC, "ec"))) {
			Map<String, Object> map = mapper.readValue(path.toFile(), Map.class);
			String id = mc.add(Path.Service.EC.EventCycle, mapper.convertValue(map.get("eventCycle"), MCEventCycleSpec.class));
			for (MCSubscriberSpec spec : mapper.convertValue(map.get("subscribers"), MCSubscriberSpec[].class)) {
				mc.add(Path.Service.EC.Subscriber, spec, id);
			}
		}
		for (java.nio.file.Path path : Files.newDirectoryStream(Paths.get(Environment.SPEC, "pc"))) {
			Map<String, Object> map = mapper.readValue(path.toFile(), Map.class);
			String id = mc.add(Path.Service.PC.PortCycle, mapper.convertValue(map.get("portCycle"), MCPortCycleSpec.class));
			for (MCSubscriberSpec spec : mapper.convertValue(map.get("subscribers"), MCSubscriberSpec[].class))
				mc.add(Path.Service.PC.Subscriber, spec, id);
		}

		return mc;
	}
}
