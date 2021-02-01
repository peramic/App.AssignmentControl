package havis.custom.harting.assignmentcontrol.ui.client;

import havis.custom.harting.assignmentcontrol.rest.async.AssignmentControlServiceAsync;
import havis.custom.harting.assignmentcontrol.ui.resourcebundle.ConstantsResource;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;

import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class StaticElementsUI extends Composite {

	private static StaticElementsUiBinder uiBinder = GWT.create(StaticElementsUiBinder.class);

	private AssignmentControlServiceAsync serviceAsync = GWT
			.create(AssignmentControlServiceAsync.class);

	private final static Logger LOGGER = Logger.getLogger(StaticElementsUI.class.getName());

	interface StaticElementsUiBinder extends UiBinder<Widget, StaticElementsUI> {
	}

	public StaticElementsUI() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("sendAcceptButton")
	public void onClickButton(ClickEvent clickEvent) {
		serviceAsync.sendAccept(callback());
	}

	private MethodCallback<Void> callback() {
		return new MethodCallback<Void>() {

			@Override
			public void onSuccess(Method method, Void response) {
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				LOGGER.log(java.util.logging.Level.SEVERE, "Class:" + exception.getClass().getName() + ";" + exception.getMessage()); // Util.getReason(exception));
				CustomMessageWidget.show(ConstantsResource.INSTANCE.sendAcceptException(), MessageType.ERROR);

			}
		};
	}

}
