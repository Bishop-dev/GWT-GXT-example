package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.element.form.EditForm;
import com.hubachov.client.model.Role;
import com.hubachov.client.model.User;
import com.hubachov.client.service.UserServiceAsync;

import java.util.*;

public class UserTable extends LayoutContainer {
    private final UserServiceAsync userServiceAsync;

    public UserTable(final UserServiceAsync userServiceAsync) {
        this.userServiceAsync = userServiceAsync;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        super.onRender(parent, index);
        setLayout(new FlowLayout());

        //Preparing columns
        final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        configs.add(new ColumnConfig("id", "Id", 30));
        configs.add(new ColumnConfig("login", "Login", 100));
        configs.add(new ColumnConfig("email", "Email", 100));
        configs.add(new ColumnConfig("firstName", "First Name", 100));
        configs.add(new ColumnConfig("lastName", "Last Name", 100));
        configs.add(new ColumnConfig("birthday", "Birthday", 100));
        configs.add(new ColumnConfig("role", "Role", 120));

        //align text in grid to left side
        Iterator<ColumnConfig> iterator = configs.iterator();
        while (iterator.hasNext()) {
            iterator.next().setAlignment(Style.HorizontalAlignment.LEFT);
        }
        RpcProxy<BasePagingLoadResult<User>> proxy = new RpcProxy<BasePagingLoadResult<User>>() {
            @Override
            protected void load(Object config, AsyncCallback<BasePagingLoadResult<User>> callback) {
                userServiceAsync.getUsers((BasePagingLoadConfig) config, callback);
            }
        };
        final PagingLoader<PagingLoadResult<Role>> loader = new BasePagingLoader<PagingLoadResult<Role>>(proxy);
        final ListStore<User> listStore = new ListStore<User>(loader);
        ColumnModel cm = new ColumnModel(configs);
        final Grid<User> grid = new Grid<User>(listStore, cm);
        grid.addListener(Events.Attach, new Listener<GridEvent<User>>() {
            @Override
            public void handleEvent(GridEvent<User> baseEvent) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        PagingLoadConfig config = new BasePagingLoadConfig();
                        config.setOffset(0);
                        config.setLimit(10);
                        Map<String, Object> state = grid.getState();
                        if (state.containsKey("offset")) {
                            int offset = (Integer) state.get("offset");
                            int limit = (Integer) state.get("limit");
                            config.setLimit(limit);
                            config.setOffset(offset);
                        }
                        if (state.containsKey("sortField")) {
                            config.setSortField((String) state.get("sortField"));
                            config.setSortDir(Style.SortDir.valueOf((String) state.get("sortDir")));
                        }
                        loader.load(config);
                    }
                });
            }
        });
        grid.setStripeRows(true);
        grid.setColumnLines(true);
        ToolBar toolBar = new ToolBar();
        grid.getSelectionModel().bind(listStore);
        grid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
        PagingToolBar pagingToolBar = new PagingToolBar(10);
        pagingToolBar.bind(loader);
        final ContentPanel panel = new ContentPanel();
        panel.setHeading("Editable User Grid");
        panel.setFrame(true);
        panel.setSize(700, 300);
        panel.setLayout(new FitLayout());
        panel.setTopComponent(toolBar);
        panel.setBottomComponent(pagingToolBar);
        panel.add(grid);
        toolBar.add(new Button("Edit", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                //add(new EditForm(new User(1L, "admin", "admin", "admin@mail.com", "Admin", "Adminovich", new Date(), new Role(1L, "admin"))));
                add(new EditForm(grid.getSelectionModel().getSelectedItem(), userServiceAsync));
                layout(true);
            }
        }));
        grid.setSize(600, 300);
        add(panel);
    }
}
