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
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.model.Role;
import com.hubachov.client.model.User;
import com.hubachov.client.service.RoleServiceAsync;
import com.hubachov.client.service.UserServiceAsync;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            public void componentSelected(ButtonEvent ce) {
                RpcProxy<PagingLoadResult<Role>> proxy = new RpcProxy<PagingLoadResult<Role>>() {
                    @Override
                    protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<Role>> callback) {
                        roleServiceAsync.getAll((PagingLoadConfig) loadConfig, callback);
                    }
                };
                final PagingLoader<PagingLoadResult<Role>> loader = new BasePagingLoader<PagingLoadResult<Role>>(proxy);
                loader.setRemoteSort(true);
                ListStore<Role> list = new ListStore<Role>(loader);
                final PagingToolBar toolBar = new PagingToolBar(3);
                toolBar.bind(loader);
                List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
                configs.add(new ColumnConfig("id", "Id", 30));
                configs.add(new ColumnConfig("name", "Name", 100));
                ColumnModel cm = new ColumnModel(configs);
                final Grid<Role> grid = new Grid<Role>(list, cm);
                grid.setStateId("pagingGridExample");
                grid.setStateful(true);
                grid.addListener(Events.Attach, new Listener<GridEvent<Role>>() {
                    public void handleEvent(GridEvent<Role> be) {
                        PagingLoadConfig config = new BasePagingLoadConfig();
                        config.setOffset(0);
                        config.setLimit(3);
                        Map<String, Object> state = grid.getState();
                        if (state.containsKey("offset")) {
                            int offset = (Integer) state.get("offset");
                            int limit = (Integer) state.get("limit");
                            config.setOffset(offset);
                            config.setLimit(limit);
                        }
                        if (state.containsKey("sortField")) {
                            config.setSortField((String) state.get("sortField"));
                            config.setSortDir(Style.SortDir.valueOf((String) state
                                    .get("sortDir")));
                        }
                        loader.load(config);
                    }
                });
                grid.setLoadMask(true);
                grid.setBorders(true);
                grid.setAutoExpandColumn("comments");
                grid.setStyleAttribute("borderTop", "none");
                grid.setStripeRows(true);
                ContentPanel cp = new ContentPanel();
                cp.setBodyBorder(false);
                cp.setHeading("Grid with Pagination");
                cp.setButtonAlign(Style.HorizontalAlignment.CENTER);
                cp.setLayout(new FitLayout());
                cp.setSize(700, 300);
                cp.add(grid);
                cp.setBottomComponent(toolBar);
                center.removeAll();
                center.add(cp);
                center.layout(true);
            }
        });

        west.add(users);
        west.add(roles);
        add(west, westData);
        add(center, centerData);
    }
}