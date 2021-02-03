package havis.custom.harting.assignmentcontrol;

import havis.custom.harting.assignmentcontrol.config.ConfigurationManager;
import havis.custom.harting.assignmentcontrol.config.Environment;
import havis.custom.harting.assignmentcontrol.model.Configuration;
import havis.custom.harting.assignmentcontrol.model.Tag;
import havis.middleware.ale.service.ec.ECReports;
import havis.middleware.ale.service.pc.PCOpReport;
import havis.middleware.ale.service.pc.PCReports;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;

public class Main implements EventCycleListener, PortCycleListener {

	private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

	private ECThread ecThread;
	private PCThread pcThread;
	private AssignmentChecker assignment;
	private Trigger trigger;

	private ScheduledFuture<?> schedule;
	private ScheduledExecutorService scheduler;

	private ConfigurationManager manager;
	private HttpRequest httpRequest = new HttpRequest();

	private McTracker mcTracker;
	private Thread tagWatcherThread;

	public Main(ConfigurationManager manager) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
		this.manager = manager;

		assignment = new AssignmentChecker(this.httpRequest, this.manager.get());
		trigger = new Trigger(this.httpRequest, manager.get());

		tagWatcherThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LOGGER.log(Level.FINE, "Tag watcher thread started...");
					while (!tagWatcherThread.isInterrupted()) {
						if (ecThread != null) {
							Integer tagTimeout = Main.this.manager.get().getTagTimeout();
							if ((tagTimeout == null) || (tagTimeout < 1)) {
								break;
							}

							ecThread.lookup(tagTimeout);
						}
						Thread.sleep(1000);
					}
					LOGGER.log(Level.FINE, "Tag watcher thread end...");
				} catch (InterruptedException e) {
					tagWatcherThread.interrupt();
					LOGGER.log(Level.WARNING, "Tag watcher thread interrupted", e);
				}
			}
		});
	}

	public Configuration getConfiguration() {
		return manager.get();
	}

	public void setConfiguration(Configuration configuration) throws Exception {
		manager.set(configuration);

		assignment.setConfiguration(this.manager.get());
		trigger.setConfiguration(this.manager.get());

		assignment.reinit();
	}

	public synchronized void start(BundleContext context) throws Exception {
		mcTracker = new McTracker(context, trigger);
		mcTracker.open();

		if ((this.manager.get().getTagTimeout() != null) && (this.manager.get().getTagTimeout() > 0)) {
			tagWatcherThread.start();
		}

		if (ecThread == null) {
			ecThread = new ECThread(this);
			ecThread.start();
		}

		if (pcThread == null) {
			pcThread = new PCThread(this);
			pcThread.start();
		}

		assignment.reinit();
	}

	public synchronized void stop() {

		try {
			trigger.systemInactive();
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "System deactivation failed", exc);
		}

		try {
			if (!tagWatcherThread.isInterrupted()) {
				tagWatcherThread.interrupt();
			}
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Interrupt Tag watcher thread failed", exc);
		}

		if (ecThread != null) {
			ecThread.setRunning(false);
			try {
				ecThread.join();
			} catch (InterruptedException e) {
			} finally {
				ecThread = null;
			}
		}

		if (pcThread != null) {
			pcThread.setRunning(false);
			try {
				pcThread.join();
			} catch (InterruptedException e) {
			} finally {
				pcThread = null;
			}
		}

		cancelTimer();

		if (mcTracker != null) {
			mcTracker.close();
			mcTracker = null;
		}
	}

	public Queue<ECReports> getECQueue() {
		return ecThread.getECQueue();
	}

	public Queue<PCReports> getPCQueue() {
		return pcThread.getPCQueue();
	}

	@Override
	public void onEcReport(boolean newTagsAvailable, final List<Tag> tags) {
		try {
			if (newTagsAvailable) {
				Thread thread = new Thread() {
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						synchronized (tags) {
							if (tags.size() > 0) {
								try {
									LOGGER.log(Level.FINE, "Assignment check...");
									boolean assigned = assignment.check(tags);

									if (assigned) {
										trigger.sendAccept();
									}

									LOGGER.log(Level.FINE, assigned ? "Assigned" : "Not Assigned");
									assignment.logAssignmentState(manager.get().getLocationName(), assigned);
								} catch (Exception exc) {
									LOGGER.log(Level.SEVERE, "Assignment check failed", exc);
								}
							}
						}
					}
				};
				thread.start();
			}
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Exception while proccessing ec report occured", exc);
		}
	}

	@Override
	public void onPcReport(List<PCOpReport> opReportList) {
		boolean activeSwitchState = false;
		boolean trigger1State = false;
		boolean trigger2State = false;

		for (PCOpReport pcOpReport : opReportList) {
			switch (pcOpReport.getOpName()) {
			case Environment.TRIGGER_1:
				trigger1State = pcOpReport.isState();
				break;
			case Environment.TRIGGER_2:
				trigger2State = pcOpReport.isState();
				break;
			case Environment.ACTIVE_SWITCH:
				activeSwitchState = pcOpReport.isState();
				break;
			}
		}

		boolean activeSwitchAvailable = manager.get().isActiveSwitchAvailable();
		boolean active = (activeSwitchState && activeSwitchAvailable) || !activeSwitchAvailable;

		try {
			if (active) {
				trigger.systemActive();

				boolean scanTrigger = manager.get().isScanTrigger();
				boolean inputOn = active && ((scanTrigger && (trigger1State || trigger2State)) || (!scanTrigger));

				if (inputOn) {
					cancelTimer();
					trigger.startScan();
				} else {
					startTimer();
				}
			} else {
				trigger.systemInactive();
				stopScan();
			}
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "ALE Middleware Trigger execution failed", exc);
		}
	}

	public void sendAccept() throws Exception {
		trigger.sendAccept();
	}

	private void stopScan() {
		try {
			// Pause reading the tags
			trigger.stopScan();

			if (ecThread != null) {
				ecThread.clearTagList();
			}
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Error while stopping scan occured", exc);
		}
	}

	private void startTimer() {
		Long triggerHoldTime = manager.get().getTriggerHoldTime();

		if ((triggerHoldTime != null) && (triggerHoldTime > 0)) {
			try {
				cancelTimer();

				scheduler = Executors.newSingleThreadScheduledExecutor();

				schedule = scheduler.schedule(new Runnable() {
					@Override
					public void run() {
						stopScan();
					}
				}, triggerHoldTime, TimeUnit.MILLISECONDS);

			} catch (Throwable throwable) {
				LOGGER.log(Level.SEVERE, "Exception on shutdown scheduler", throwable);
			}
		} else {
			stopScan();
		}
	}

	private void cancelTimer() {
		if (schedule != null) {
			schedule.cancel(true);
			schedule = null;
		}

		if (scheduler != null) {
			scheduler.shutdownNow();
			scheduler = null;
		}
	}
}