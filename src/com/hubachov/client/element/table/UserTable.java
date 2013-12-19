package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.model.User;
import com.hubachov.client.service.UserServiceAsync;

import java.util.ArrayList;
import java.util.List;

public class UserTable extends LayoutContainer {
    private UserServiceAsync userServiceAsync;

    public UserTable(UserServiceAsync userServiceAsync) {
        this.userServiceAsync = userServiceAsync;
    }

    @Override
    protected void onAttach() {
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
        add(grid);
    }
}
