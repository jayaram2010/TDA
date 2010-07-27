package com.tda.presentation.client.presenter;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.tda.model.item.Item;
import com.tda.presentation.client.service.AdminServiceGWTWrapperAsync;
import com.tda.presentation.client.service.ItemServiceGWTWrapper;
import com.tda.presentation.client.service.ItemServiceGWTWrapperAsync;
import com.tda.presentation.client.view.CrudView;

public class SocialHomePresenter implements Presenter {

	public interface Display {
		Widget asWidget();
		Panel getTabsContainer();
		Panel getMainContainer();
		DecoratedTabPanel getTab();
	}

	private Display display;
	private HandlerManager eventBus;
	private AdminServiceGWTWrapperAsync rpc;
	private List<Presenter> tabsPresenters;
	private ItemServiceGWTWrapperAsync itemRPC;

	public SocialHomePresenter(AdminServiceGWTWrapperAsync rpc, HandlerManager eventBus, Display view) {
		this.display = view;
		this.rpc = rpc;
		this.eventBus = eventBus;
		tabsPresenters = new ArrayList<Presenter>();
	}

	public void go(HasWidgets container) {
		instancePresenters();
		appendPresenters();
		container.clear();
		//select displayed tab
		display.getTab().selectTab(0);
		//go for it tiger
		container.add(display.asWidget());
	}
	
	public void appendPresenters(){
		for (Presenter presenter : tabsPresenters) {
				presenter.go(display.getTab());
		}
	}
	
	public void instancePresenters(){
		tabsPresenters.add(new ItemPresenter(eventBus, new CrudView<Item>()));
	}
	
	public ItemServiceGWTWrapperAsync getItemRPC() {
		if ( itemRPC == null ) {
			itemRPC = GWT.create(ItemServiceGWTWrapper.class);
		}

		return itemRPC;
	}

	public void onDestroy() {
		/* Do nothing */
	}

	public void go(DecoratedTabPanel panel) {
		panel.add(display.asWidget(), "Admin");
	}

}
