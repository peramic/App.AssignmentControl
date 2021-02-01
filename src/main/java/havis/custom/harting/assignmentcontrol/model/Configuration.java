package havis.custom.harting.assignmentcontrol.model;

public class Configuration {
	private String assignmentUri;
	private Integer httpConnectionTimeOut;
	private String locationName;
	private Integer locationId;
	private Long triggerHoldTime;
	private boolean scanTrigger;
	private boolean activeSwitchAvailable;
	private boolean gateControl;
	private Integer tagTimeout;

	private String pcOutReadyUrl;
	private String pcOutActiveUrl;
	private String pcOutInactiveUrl;
	private String pcOutSendAcceptUrl;

	private String pcStopscanUrl;
	private String pcStartscanUrl;
	private String pcObserveUrl;

	public String getAssignmentUri() {
		return assignmentUri;
	}

	public void setAssignmentUri(String assignmentUri) {
		this.assignmentUri = assignmentUri;
	}

	public Integer getHttpConnectionTimeOut() {
		return httpConnectionTimeOut;
	}

	public void setHttpConnectionTimeOut(Integer httpConnectionTimeOut) {
		this.httpConnectionTimeOut = httpConnectionTimeOut;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public Long getTriggerHoldTime() {
		return triggerHoldTime;
	}

	public void setTriggerHoldTime(Long triggerHoldTime) {
		this.triggerHoldTime = triggerHoldTime;
	}

	public boolean isScanTrigger() {
		return scanTrigger;
	}

	public void setScanTrigger(boolean scanTrigger) {
		this.scanTrigger = scanTrigger;
	}

	public boolean isActiveSwitchAvailable() {
		return activeSwitchAvailable;
	}

	public void setActiveSwitchAvailable(boolean activeSwitchAvailable) {
		this.activeSwitchAvailable = activeSwitchAvailable;
	}

	public void setPcOutReadyUrl(String pcOutReadyUrl) {
		this.pcOutReadyUrl = pcOutReadyUrl;
	}

	public String getPcOutActiveUrl() {
		return pcOutActiveUrl;
	}

	public void setPcOutActiveUrl(String pcOutActiveUrl) {
		this.pcOutActiveUrl = pcOutActiveUrl;
	}

	public String getPcOutInactiveUrl() {
		return pcOutInactiveUrl;
	}

	public void setPcOutInactiveUrl(String pcOutInactiveUrl) {
		this.pcOutInactiveUrl = pcOutInactiveUrl;
	}

	public String getPcOutReadyUrl() {
		return pcOutReadyUrl;
	}

	public String getPcOutSendAcceptUrl() {
		return pcOutSendAcceptUrl;
	}

	public void setPcOutSendAcceptUrl(String pcSendAcceptUrl) {
		this.pcOutSendAcceptUrl = pcSendAcceptUrl;
	}

	public String getPcStopscanUrl() {
		return pcStopscanUrl;
	}

	public void setPcStopscanUrl(String pcStopscanUrl) {
		this.pcStopscanUrl = pcStopscanUrl;
	}

	public String getPcStartscanUrl() {
		return pcStartscanUrl;
	}

	public void setPcStartscanUrl(String pcStartscanUrl) {
		this.pcStartscanUrl = pcStartscanUrl;
	}

	public String getPcObserveUrl() {
		return pcObserveUrl;
	}

	public void setPcObserveUrl(String pcObserveUrl) {
		this.pcObserveUrl = pcObserveUrl;
	}

	@Deprecated
	public boolean isGateControl() {
		return gateControl;
	}

	@Deprecated
	public void setGateControl(boolean gateControl) {
		this.gateControl = gateControl;
	}

	public Integer getTagTimeout() {
		return tagTimeout;
	}

	public void setTagTimeout(Integer tagTimeout) {
		this.tagTimeout = tagTimeout;
	}

	@Override
	public String toString() {
		return "Configuration [assignmentUri=" + assignmentUri + ", httpConnectionTimeOut=" + httpConnectionTimeOut + ", locationName=" + locationName
				+ ", locationId=" + locationId + ", triggerHoldTime=" + triggerHoldTime + ", scanTrigger=" + scanTrigger + ", activeSwitchAvailable="
				+ activeSwitchAvailable + ", gateControl=" + gateControl + ", tagTimeout=" + tagTimeout + ", pcOutReadyUrl=" + pcOutReadyUrl
				+ ", pcOutActiveUrl=" + pcOutActiveUrl + ", pcOutInactiveUrl=" + pcOutInactiveUrl + ", pcOutSendAcceptUrl=" + pcOutSendAcceptUrl
				+ ", pcStopscanUrl=" + pcStopscanUrl + ", pcStartscanUrl=" + pcStartscanUrl + ", pcObserveUrl=" + pcObserveUrl + "]";
	}
}