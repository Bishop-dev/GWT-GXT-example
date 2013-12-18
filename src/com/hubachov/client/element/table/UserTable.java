package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
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
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
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

        BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy);
        ListStore<User> listStore = new ListStore<User>(loader);
        ColumnModel cm = new ColumnModel(configs);
        Grid<User> grid = new Grid<User>(listStore, cm);
        grid.setStripeRows(true);
        add(grid);
    }
}
