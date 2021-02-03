package havis.custom.harting.assignmentcontrol.ui.resourcebundle;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

public interface AppResources extends ClientBundle {

	public static final AppResources INSTANCE = GWT.create(AppResources.class);

	@Source("resources/CssResources.css")
	@NotStrict
	CssResources css();

	@Source("resources/close.png")
	ImageResource close();
	
	@Source("resources/CONTENT_Switch_On.png")
	DataResource contentSwitchOn();
	
	@Source("resources/CONTENT_Switch_Off.png")
	DataResource contentSwitchOff();
	
	@Source("resources/App_Button.png")
	DataResource appButton();

}