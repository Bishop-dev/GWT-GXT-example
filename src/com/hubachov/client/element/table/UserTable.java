package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.hubachov.client.element.form.EditForm;
import com.hubachov.client.model.User;
import com.hubachov.client.service.RoleServiceAsync;
import com.hubachov.client.service.UserServiceAsync;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserTable extends LayoutContainer {
    private final UserServiceAsync userServiceAsync;
    private final RoleServiceAsync roleServiceAsync;
    private Grid<User> grid;
    private ContentPanel view = new ContentPanel();

    public UserTable(UserServiceAsync userServiceAsync, RoleServiceAsync roleServiceAsync) {
        this.userServiceAsync = userServiceAsync;
        this.roleServiceAsync = roleServiceAsync;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        BasePagingLoader<PagingLoadResult<User>> loader = new BasePagingLoader<PagingLoadResult<User>>(initProxy());
        ListStore<User> store = new ListStore<User>(loader);
        grid = new Grid<User>(store, new ColumnModel(configureColumns()));
        addGridOnAttachListener(loader);
        attachToolbars(loader);
        styleGrid(store);
        view.add(grid);
        add(view);
    }

    private List<ColumnConfig> configureColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        configs.add(new ColumnConfig("id", "Id", 30));
        configs.add(new ColumnConfig("login", "Login", 100));
        configs.add(new ColumnConfig("email", "Email", 100));
        configs.add(new ColumnConfig("firstName", "First Name", 100));
        configs.add(new ColumnConfig("lastName", "Last Name", 100));
        configs.add(new ColumnConfig("birthday", "Birthday", 100));
        configs.add(new ColumnConfig("role", "Role", 120));
        alignColumns(configs, Style.HorizontalAlignment.LEFT);
        return configs;
    }

    private RpcProxy<BasePagingLoadResult<User>> initProxy() {
        return new RpcProxy<BasePagingLoadResult<User>>() {
            @Override
            protected void load(Object config, AsyncCallback<BasePagingLoadResult<User>> callback) {
                userServiceAsync.getUsers((BasePagingLoadConfig) config, callback);
            }
        };
    }

    private void attachToolbars(BasePagingLoader<PagingLoadResult<User>> loader) {
        PagingToolBar pagingToolBar = new PagingToolBar(10);
        pagingToolBar.bind(loader);
        ToolBar toolBar = new ToolBar();
        view.setHeading("Editable User Grid");
        view.setFrame(true);
        view.setSize(700, 300);
        view.setLayout(new FitLayout());
        view.setBottomComponent(pagingToolBar);
        view.setTopComponent(toolBar);
        toolBar.add(makeNewUserBtn());
        toolBar.add(makeEditBtn());
        toolBar.add(makeDeleteBtn());
    }

    private void styleGrid(ListStore<User> listStore) {
        grid.setStripeRows(true);
        grid.setColumnLines(true);
        grid.getSelectionModel().bind(listStore);
        grid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
        grid.setSize(600, 300);
    }

    private void addGridOnAttachListener(final BasePagingLoader<PagingLoadResult<User>> loader) {
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
    }

    private Button makeNewUserBtn() {
        Button button = new Button("New");
        addNewUserButtonListener(button);
        return button;
    }

    private Button makeEditBtn() {
        Button button = new Button("Edit");
        addEditButtonListener(button);
        return button;
    }

    private Button makeDeleteBtn() {
        Button button = new Button("Delete");
        addDeleteButtonListener(button);
        return button;
    }

    private void addEditButtonListener(Button button) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                add(new EditForm(grid, userServiceAsync, roleServiceAsync));
                layout(true);
            }
        });
    }

    private void addNewUserButtonListener(Button button) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                add(new EditForm(grid, userServiceAsync, roleServiceAsync));
                layout(true);
            }
        });
    }

    private void addDeleteButtonListener(Button button) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                final User user = grid.getSelectionModel().getSelectedItem();
                if (user != null) {
                    MessageBox.confirm("Delete?", "Are you sure?", new Listener<MessageBoxEvent>() {
                        @Override
                        public void handleEvent(MessageBoxEvent be) {
                            Button btn = be.getButtonClicked();
                            if (Dialog.YES.equalsIgnoreCase(btn.getItemId())) {
                                userServiceAsync.remove(user, new DeleteUserAsyncCallback<Void>(grid));
                            }
                        }
                    });
                }
            }
        });
    }

    private void alignColumns(List<ColumnConfig> columns, Style.HorizontalAlignment side) {
        Iterator<ColumnConfig> iterator = columns.iterator();
        while (iterator.hasNext()) {
            iterator.next().setAlignment(side);
        }
    }

}

class DeleteUserAsyncCallback<Void> implements AsyncCallback<Void> {
    private final Grid<User> grid;

    public DeleteUserAsyncCallback(Grid<User> grid) {
        this.grid = grid;
    }

    @Override
    public void onFailure(Throwable throwable) {
        Window.alert(throwable.getMessage());
    }

    @Override
    public void onSuccess(Void aVoid) {
        Info.display("Success", "Deleted");
        grid.getStore().getLoader().load();
    }
}