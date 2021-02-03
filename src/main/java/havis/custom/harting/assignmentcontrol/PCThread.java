package havis.custom.harting.assignmentcontrol;

import havis.middleware.ale.service.ec.ECReport;
import havis.middleware.ale.service.pc.PCEventReport;
import havis.middleware.ale.service.pc.PCOpReport;
import havis.middleware.ale.service.pc.PCOpReports;
import havis.middleware.ale.service.pc.PCReport;
import havis.middleware.ale.service.pc.PCReports;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Queue Thread, which should be registered, for receiving of "pc reports".
 * 
 */
public class PCThread extends Thread {

	private final static Logger log = Logger.getLogger(PCThread.class.getName());
	private final BlockingQueue<PCReports> pcQueue = new LinkedBlockingQueue<>();

	private PortCycleListener portCycleListener;
	private boolean running = true;

	/**
	 * Initialization Constructor.
	 * 
	 * @param portCycleListener
	 *            {@link PortCycleListener}
	 */
	public PCThread(PortCycleListener portCycleListener) {
		super("PC-Thread");

		this.portCycleListener = portCycleListener;
	}

	/**
	 * Notifies the {@link PortCycleListener#onPcReport(String)} on PC Report.
	 * 
	 * @param reports
	 *            {@link ECReport}
	 */
	private void evaluate(PCReports reports) {		
		if (reports.getReports() != null) {
			for (PCReport pcReport : reports.getReports().getReport()) {
				if (pcReport.getEventReports() != null) {
					for (PCEventReport pcEventReport : pcReport.getEventReports().getEventReport()) {
						PCOpReports opReports = pcEventReport.getOpReports();
						List<PCOpReport> opReportList = opReports.getOpReport();

						// Report trigger id.
						portCycleListener.onPcReport(opReportList);
					}
				} else {
					log.log(Level.FINE, "Received no event reports");
				}
			}
		} else {
			log.log(Level.FINE, "Received no PC reports");
		}
	}

	@Override
	public void run() {
		try {
			while (running) {
				PCReports reports = pcQueue.poll(100, TimeUnit.MILLISECONDS);

				if (reports != null) {
					try {
						evaluate(reports);
					} catch (Throwable e) {
						log.log(Level.FINE, "Failed to evaluate PC reports", e);
					}
				}

			}
		} catch (InterruptedException e) {
		}
	}

	public Queue<PCReports> getPCQueue() {
		return pcQueue;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
