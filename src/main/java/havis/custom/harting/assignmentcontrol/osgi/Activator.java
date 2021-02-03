package havis.custom.harting.assignmentcontrol.osgi;

import havis.custom.harting.assignmentcontrol.Main;
import havis.custom.harting.assignmentcontrol.config.ConfigurationManager;
import havis.custom.harting.assignmentcontrol.rest.RESTApplication;

import java.util.Date;
import java.util.Hashtable;
import java.util.Queue;
import java.util.logging.Logger;

import javax.ws.rs.core.Application;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	private final static Logger log = Logger.getLogger(Activator.class.getName());

	private final static String QUEUE_NAME = "name";
	// subscriber URI: queue://ec-scan
	private final static String EC_QUEUE_VALUE = "ec-scan";
	// subscriber URI: queue://pc-signallost
	private final static String PC_QUEUE_VALUE = "pc-signal";

	private ServiceRegistration<?> ecQueue;
	private ServiceRegistration<?> pcQueue;
	private ServiceRegistration<Application> restApp;

	private Main main;

	@SuppressWarnings("serial")
	@Override
	public void start(BundleContext context) throws Exception {
		long now = new Date().getTime();

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(Activator.class.getClassLoader());
			main = new Main(new ConfigurationManager());
		} finally {
			Thread.currentThread().setContextClassLoader(loader);
		}

		main.start(context);

		restApp = context.registerService(Application.class, new RESTApplication(main), null);

		// register pc queue
		pcQueue = context.registerService(Queue.class, main.getPCQueue(), new Hashtable<String, String>() {
			{
				put(QUEUE_NAME, PC_QUEUE_VALUE);
			}
		});

		// register ec queue
		ecQueue = context.registerService(Queue.class, main.getECQueue(), new Hashtable<String, String>() {
			{
				put(QUEUE_NAME, EC_QUEUE_VALUE);
			}
		});

		log.info("Bundle start took " + (new Date().getTime() - now) + "ms");
	}

	@Override
	public void stop(BundleContext context) throws Exception {

		if (ecQueue != null) {
			ecQueue.unregister();
			ecQueue = null;
		}

		if (pcQueue != null) {
			pcQueue.unregister();
			pcQueue = null;
		}

		if (restApp != null) {
			restApp.unregister();
			restApp = null;
		}

		if (main != null) {
			main.stop();
			main = null;
		}
	}
}