package com.hubachov.client.element;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.element.table.RoleTable;
import com.hubachov.client.model.User;
import com.hubachov.client.service.RoleServiceAsync;
import com.hubachov.client.service.UserServiceAsync;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends LayoutContainer {
    private UserServiceAsync userServiceAsync;
    private RoleServiceAsync roleServiceAsync;

    public MainPage(UserServiceAsync userServiceAsync, RoleServiceAsync roleServiceAsync) {
        this.userServiceAsync = userServiceAsync;
        this.roleServiceAsync = roleServiceAsync;
    }

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        setStyleAttribute("padding", "10px");
        ContentPanel west = new ContentPanel();
        final ContentPanel center = new ContentPanel();
        center.setHeading("Content");
        center.setScrollMode(Style.Scroll.AUTOX);
        BorderLayoutData westData = new BorderLayoutData(Style.LayoutRegion.WEST, 150);
        westData.setSplit(true);
        westData.setCollapsible(true);
        westData.setMargins(new Margins(0, 5, 0, 0));
        BorderLayoutData centerData = new BorderLayoutData(Style.LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0));

        Button users = new Button("Users", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                center.removeAll();

                final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
                configs.add(new ColumnConfig("id", "Id", 30));
                configs.add(new ColumnConfig("login", "Login", 100));
                configs.add(new ColumnConfig("email", "Email", 100));
                configs.add(new ColumnConfig("firstName", "First Name", 100));
                configs.add(new ColumnConfig("lastName", "Last Name", 100));
                configs.add(new ColumnConfig("birthday", "Birthday", 100));
                configs.add(new ColumnConfig("role", "Role", 100));

                RpcProxy<BaseListLoadResult<User>> proxy = new RpcProxy<BaseListLoadResult<User>>() {
                    @Override
                    protected void load(Object loadConfig, AsyncCallback<BaseListLoadResult<User>> callback) {
                        userServiceAsync.getUsers((BaseListLoadConfig) loadConfig, callback);
                    }
                };

                final BaseListLoader<ListLoadResult<User>> loader = new BaseListLoader<ListLoadResult<User>>(proxy);
                ListStore<User> listStore = new ListStore<User>(loader);
                ColumnModel cm = new ColumnModel(configs);
                Grid<User> grid = new Grid<User>(listStore, cm);
                grid.setStripeRows(true);
                grid.addListener(Events.Attach, new Listener<ComponentEvent>() {
                    @Override
                    public void handleEvent(ComponentEvent be) {
                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                loader.load(new BaseListLoadConfig());
                            }
                        });
                    }
                });
                grid.setHeight(300);
                grid.getView().setAutoFill(true);
                center.add(grid);
                center.layout(true);
            }
        });

        Button roles = new Button("Roles", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                center.removeAll();
                center.add(new RoleTable(roleServiceAsync));
                center.layout(true);
            }
        });

        west.add(users);
        west.add(roles);
        add(west, westData);
        add(center, centerData);
    }
}