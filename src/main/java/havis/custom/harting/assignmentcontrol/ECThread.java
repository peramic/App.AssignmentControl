package havis.custom.harting.assignmentcontrol;

import havis.custom.harting.assignmentcontrol.model.Tag;
import havis.middleware.ale.service.ec.ECReport;
import havis.middleware.ale.service.ec.ECReportGroup;
import havis.middleware.ale.service.ec.ECReportGroupListMember;
import havis.middleware.ale.service.ec.ECReportMemberField;
import havis.middleware.ale.service.ec.ECReports;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Queue Thread, which should be registered, for receiving of "ec reports".
 * 
 */
public class ECThread extends Thread {

	private final static Logger log = Logger.getLogger(ECThread.class.getName());
	private final BlockingQueue<ECReports> ecQueue = new LinkedBlockingQueue<>();

	private boolean running = true;
	private EventCycleListener eventCycleListener;
	private List<Tag> tagList = new ArrayList<>();

	/**
	 * Initialization Constructor.
	 * 
	 * @param eventCycleListener
	 *            {@link EventCycleListener}
	 */
	public ECThread(EventCycleListener eventCycleListener) {
		super("EC-Thread");

		this.eventCycleListener = eventCycleListener;
	}

	@Override
	public void run() {
		try {
			while (running) {
				try {
					ECReports reports = ecQueue.poll(100, TimeUnit.MILLISECONDS);

					if (reports != null) {
						try {
							evaluate(reports);
						} catch (Throwable e) {
							log.log(Level.FINE, "Failed to evaluate EC reports", e);
						}
					}
				} catch (ClassCastException cce) {
					log.log(Level.FINE, "EC Poll returns wrong type", cce);
				}
			}
		} catch (InterruptedException e) {

		}
	}

	/**
	 * @return list of ECReports
	 */
	public Queue<ECReports> getECQueue() {
		return ecQueue;
	}

	/**
	 * @param running
	 *            to be set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * Clears the tag list. Should be called when "ScanTag"-EC was paused.
	 */
	void clearTagList() {
		synchronized (tagList) {
			tagList.clear();
		}

		log.log(Level.FINE, "Tag list cleared");
	}

	/**
	 * Remove tags from the tag list, when time elapsed
	 */
	void lookup(long timeout) {
		synchronized (tagList) {
			Date now = new Date();
			Iterator<Tag> iterator = tagList.iterator();

			while (iterator.hasNext()) {
				Tag tag = iterator.next();
				Date passt = tag.getTimestamp();
				long diff = now.getTime() - passt.getTime();
				
				if (diff > timeout) {
					log.log(Level.FINE, "Try to remove tag " + tag.toString());
					iterator.remove();
					log.log(Level.FINE, "Tag " + tag.toString() + " removed");
				}
			}
		}
	}

	/**
	 * Notifies the {@link EventCycleListener#onEcReport(Set<Tag>)} on EC
	 * Report.
	 * 
	 * @param reports
	 *            {@link ECReport}
	 */
	private void evaluate(ECReports reports) {
		if (reports.getReports() != null) {
			boolean newTagsAvailable = false;

			for (ECReport ecReport : reports.getReports().getReport()) {
				for (ECReportGroup group : ecReport.getGroup()) {
					if (group.getGroupList() != null) {
						List<ECReportGroupListMember> memberList = group.getGroupList().getMember();

						for (ECReportGroupListMember member : memberList) {
							if (member.getTag() != null) {
								String tag = member.getTag().getValue();
								if (member.getExtension() != null && member.getExtension().getFieldList() != null) {

									for (ECReportMemberField field : member.getExtension().getFieldList().getField()) {
										switch (field.getName()) {
										case "tidBank":
											String tid = field.getValue();

											if (tid != null) {
												synchronized (tagList) {
													Tag tagObj = new Tag(tag, tid, new Date());
													int indexOf = tagList.indexOf(tagObj);
													boolean isAvailable = (indexOf > -1);
													newTagsAvailable = !isAvailable || newTagsAvailable;

													if (isAvailable) {
														tagList.get(indexOf).setTimestamp(new Date());
													} else {
														tagList.add(tagObj);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			synchronized (tagList) {
				if(tagList.size() > 0) {
					eventCycleListener.onEcReport(newTagsAvailable, tagList);
				}
			}
		}
	}
}
