package havis.custom.harting.assignmentcontrol.ui.client;

import havis.custom.harting.assignmentcontrol.model.Configuration;
import havis.custom.harting.assignmentcontrol.rest.async.AssignmentControlServiceAsync;
import havis.custom.harting.assignmentcontrol.ui.resourcebundle.ConstantsResource;
import havis.net.ui.shared.client.ConfigurationSection;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationUI extends ConfigurationSection {

	private static ConfigurationUIUiBinder uiBinder = GWT.create(ConfigurationUIUiBinder.class);

	private final static String URI_REG_EXP = "^([A-Za-z][A-Za-z0-9+.-]*):(?:\\/\\/((?:(?=((?:[A-Za-z0-9-._~!$&'()*+,;=:]|%[0-9A-F]{2})*))(\\3)@)?(?=(\\[[0-9A-F:.]{2,}\\]|(?:[A-Za-z0-9-._~!$&'()*+,;=]|%[0-9A-F]{2})*))\\5(?::(?=(\\d*))\\6)?)(\\/(?=((?:[A-Za-z0-9-._~!$&'()*+,;=:@\\/]|%[0-9A-F]{2})*))\\8)?|(\\/?(?!\\/)(?=((?:[A-Za-z0-9-._~!$&'()*+,;=:@\\/]|%[0-9A-F]{2})*))\\10)?)(?:\\?(?=((?:[A-Za-z0-9-._~!$&'()*+,;=:@\\/?]|%[0-9A-F]{2})*))\\11)?(?:#(?=((?:[A-Za-z0-9-._~!$&'()*+,;=:@\\/?]|%[0-9A-F]{2})*))\12)?$";
	private final static Logger LOGGER = Logger.getLogger(ConfigurationUI.class.getName());

	private AssignmentControlServiceAsync serviceAsync = GWT
			.create(AssignmentControlServiceAsync.class);
	private Configuration configuration;

	private RegExp patternUri = RegExp.compile(URI_REG_EXP);

	interface ConfigurationUIUiBinder extends UiBinder<Widget, ConfigurationUI> {
	}

	@UiField
	protected Label labelTriggerHoldTime;

	@UiField
	protected ToggleButton toggleButtonScanTrigger;

	@UiField
	protected ToggleButton toggleButtonActivateSwitch;

	@UiField
	protected TextBox assignmentUriTextBox;

	@UiField
	protected TextBox triggerHoldTime;

	@UiField
	protected TextBox tagTimeout;

	@UiField
	protected TextBox locationNameTextBox;

	@UiField
	protected TextBox locationIdTextBox;

	@UiConstructor
	public ConfigurationUI(String name) {
		super(name);
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		serviceAsync.getConfig(new MethodCallback<Configuration>() {

			@Override
			public void onSuccess(Method method, Configuration response) {
				ConfigurationUI.this.configuration = response;

				if (ConfigurationUI.this.configuration != null) {
					assignmentUriTextBox.setText(response.getAssignmentUri());
					locationNameTextBox.setText(response.getLocationName());
					locationIdTextBox.setText((response.getLocationId() != null) ? response
							.getLocationId().toString() : "");
					triggerHoldTime.setText(response.getTriggerHoldTime().toString());
					tagTimeout.setText(response.getTagTimeout().toString());
					toggleButtonActivateSwitch.setDown(response.isActiveSwitchAvailable());
					toggleButtonScanTrigger.setDown(response.isScanTrigger());
				} else {
					ConfigurationUI.this.configuration = new Configuration();
				}
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(ConstantsResource.INSTANCE.loadConfigException(),
						MessageType.ERROR);
				LOGGER.log(java.util.logging.Level.WARNING,
						ConstantsResource.INSTANCE.loadConfigException());
			}
		});
	}

	@UiHandler("triggerHoldTime")
	public void onTriggerHoldTimeValueChange(ValueChangeEvent<String> e) {
		try {
			Long longValue = null;

			if ((triggerHoldTime.getText() != null)
					&& (triggerHoldTime.getText().trim().length() > 0)) {
				longValue = Long.parseLong(triggerHoldTime.getText());
			}

			this.configuration.setTriggerHoldTime(longValue);
			serviceAsync.setConfig(this.configuration, callback());
		} catch (NumberFormatException nfe) {
			CustomMessageWidget.show(ConstantsResource.INSTANCE.triggerHoldTimeFormatException(),
					MessageType.ERROR);
			LOGGER.log(java.util.logging.Level.SEVERE, "Class:" + nfe.getClass().getName() + ";"
					+ nfe.getMessage());
		}
	}

	@UiHandler("tagTimeout")
	public void onTagTimeoutValueChange(ValueChangeEvent<String> e) {
		try {
			int iValue = 0;

			if ((tagTimeout.getText() != null) && (tagTimeout.getText().trim().length() > 0))
				iValue = Integer.parseInt(tagTimeout.getText());

			this.configuration.setTagTimeout(iValue);
			serviceAsync.setConfig(this.configuration, callback());
		} catch (NumberFormatException exc) {
			CustomMessageWidget.show(ConstantsResource.INSTANCE.tagTimeoutFormatException(),
					MessageType.ERROR);
			LOGGER.log(Level.SEVERE, "Class: " + exc.getClass().getName() + ";" + exc.getMessage());
		}
	}

	@UiHandler("assignmentUriTextBox")
	public void onAssignmentUriValueChange(ValueChangeEvent<String> e) {
		MatchResult result = patternUri.exec(assignmentUriTextBox.getText());

		if (result != null) {
			try {
				this.configuration.setAssignmentUri(assignmentUriTextBox.getText());
				serviceAsync.setConfig(this.configuration, callback());
			} catch (NumberFormatException nfe) {
			}
		} else {
			CustomMessageWidget.show(ConstantsResource.INSTANCE.assignmentUriFormatException(),
					MessageType.ERROR);
			LOGGER.log(java.util.logging.Level.SEVERE,
					ConstantsResource.INSTANCE.assignmentUriFormatException());
		}
	}

	@UiHandler("locationIdTextBox")
	public void onLocationIdValueChange(ValueChangeEvent<String> e) {
		try {
			int value = Integer.parseInt(locationIdTextBox.getText());
			this.configuration.setLocationId(value);
			serviceAsync.setConfig(this.configuration, callback());
		} catch (NumberFormatException nfe) {
			CustomMessageWidget.show(ConstantsResource.INSTANCE.locationIdFormatException(),
					MessageType.ERROR);
			LOGGER.log(java.util.logging.Level.SEVERE, "Class:" + nfe.getClass().getName() + ";"
					+ nfe.getMessage());
		}
	}

	@UiHandler("locationNameTextBox")
	public void onLocationNameValueChange(ValueChangeEvent<String> e) {
		if ((locationNameTextBox.getText() != null)
				&& (locationNameTextBox.getText().trim().length() > 0)) {
			this.configuration.setLocationName(locationNameTextBox.getText().trim());
			serviceAsync.setConfig(this.configuration, callback());
		} else {
			CustomMessageWidget.show(ConstantsResource.INSTANCE.locationNameFormatException(),
					MessageType.ERROR);
			LOGGER.log(java.util.logging.Level.SEVERE,
					ConstantsResource.INSTANCE.locationNameFormatException());
		}
	}

	@UiHandler("toggleButtonActivateSwitch")
	public void onActivateSwitchValueChange(ValueChangeEvent<Boolean> e) {
		this.configuration.setActiveSwitchAvailable(toggleButtonActivateSwitch.isDown());
		serviceAsync.setConfig(this.configuration, callback());
	}

	@UiHandler("toggleButtonScanTrigger")
	public void onScanTriggerValueChange(ValueChangeEvent<Boolean> e) {
		this.configuration.setScanTrigger(toggleButtonScanTrigger.isDown());
		serviceAsync.setConfig(this.configuration, callback());
	}

	private MethodCallback<Void> callback() {
		return new MethodCallback<Void>() {

			@Override
			public void onSuccess(Method method, Void response) {
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				LOGGER.log(java.util.logging.Level.SEVERE, "Class:"
						+ exception.getClass().getName() + ";" + exception.getMessage()); // Util.getReason(exception));
				CustomMessageWidget.show(ConstantsResource.INSTANCE.saveConfigException(),
						MessageType.ERROR);
			}
		};
	}
}
