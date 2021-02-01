package havis.custom.harting.assignmentcontrol.ui.resourcebundle;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.Constants;

public interface ConstantsResource extends Constants {

	public static final ConstantsResource INSTANCE = GWT.create(ConstantsResource.class);

	String header();

	String triggerHoldTime();

	String tagTimeout();

	String assignmentURI();

	String locationName();

	String locationID();

	String activateSwitch();

	String sendAccept();

	String appName();

	String appConfiguration();

	String scanTrigger();

	String triggerHoldTimeFormatException();

	String tagTimeoutFormatException();

	String assignmentUriFormatException();

	String locationIdFormatException();

	String locationNameFormatException();

	String loadConfigException();

	String saveConfigException();

	String sendAcceptException();
}