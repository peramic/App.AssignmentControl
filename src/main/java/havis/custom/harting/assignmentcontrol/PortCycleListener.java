package havis.custom.harting.assignmentcontrol;

import havis.middleware.ale.service.pc.PCOpReport;

import java.util.List;

/**
 * PC report listener
 * 
 */
public interface PortCycleListener {
	/**
	 * Will be called each time by {@link PCThread} on PC report.
	 * 
	 * @param opReportList
	 *            Operation reports
	 */
	void onPcReport(List<PCOpReport> opReportList);
}
