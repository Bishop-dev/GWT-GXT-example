package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.filters.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.element.form.UserForm;
import com.hubachov.client.model.Role;
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
    private List<ColumnConfig> columns;
    private ListStore<User> store;
    private PagingLoader<PagingLoadResult<User>> loader;
    private RpcProxy<BasePagingLoadResult<User>> proxy;
    private CheckBoxSelectionModel selectionRowPlugin = new CheckBoxSelectionModel<User>();
    private ContentPanel view = new ContentPanel();
    private static final int USERS_ON_PAGE = 10;

    public UserTable(UserServiceAsync userServiceAsync, RoleServiceAsync roleServiceAsync) {
        this.userServiceAsync = userServiceAsync;
        this.roleServiceAsync = roleServiceAsync;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        initProxy();
        initLoader();
        configureColumns();
        createGrid();
        addGridOnAttachListener();
        attachToolbars();
        styleGrid();
        add(view);
    }

    private void initLoader() {
        loader = new BasePagingLoader<PagingLoadResult<User>>(proxy) {
            @Override
            protected Object newLoadConfig() {
                return new BaseFilterPagingLoadConfig();
            }
        };
        loader.setRemoteSort(true);
    }

    private void createGrid() {
        store = new ListStore<User>(loader);
        grid = new Grid<User>(store, new ColumnModel(columns));
        grid.addListener(Events.RowDoubleClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                showUserFormWindow(((GridEvent<User>) be).getModel());
            }
        });
    }

    private void configureColumns() {
        columns = new ArrayList<ColumnConfig>();
        columns.add(selectionRowPlugin.getColumn());
        columns.add(new ColumnConfig("id", "Id", 30));
        columns.add(new ColumnConfig("login", "Login", 100));
        columns.add(new ColumnConfig("email", "Email", 100));
        columns.add(new ColumnConfig("firstName", "First Name", 100));
        columns.add(new ColumnConfig("lastName", "Last Name", 100));
        columns.add(new ColumnConfig("birthday", "Birthday", 100));
        columns.add(new ColumnConfig("role", "Role", 120));
        alignColumns(Style.HorizontalAlignment.LEFT);
    }

    private void initProxy() {
        proxy = new RpcProxy<BasePagingLoadResult<User>>() {
            @Override
            protected void load(Object config, AsyncCallback<BasePagingLoadResult<User>> callback) {
                userServiceAsync.getUsers((FilterPagingLoadConfig) config, callback);
            }
        };
    }

    private void attachToolbars() {
        PagingToolBar pagingToolBar = new PagingToolBar(USERS_ON_PAGE);
        pagingToolBar.bind(loader);
        ToolBar toolBar = new ToolBar();
        view.setHeading("Editable User Grid");
        view.setFrame(true);
        view.setSize(800, 350);
        view.setLayout(new FitLayout());
        view.setBottomComponent(pagingToolBar);
        view.setTopComponent(toolBar);
        toolBar.add(makeNewEditUserBtn("New", "add-btn"));
        toolBar.add(makeNewEditUserBtn("Edit", "edit-btn"));
        toolBar.add(makeDeleteBtn());
    }

    private GridFilters attachFilters() {
        GridFilters filters = new GridFilters();
        filters.setLocal(true);
        filters.addFilter(new NumericFilter("id"));
        filters.addFilter(new StringFilter("login"));
        filters.addFilter(new StringFilter("email"));
        filters.addFilter(new StringFilter("firstName"));
        filters.addFilter(new StringFilter("lastName"));
        filters.addFilter(new DateFilter("birthday"));

//        RpcProxy<BasePagingLoadResult<Role>> proxy = new RpcProxy<BasePagingLoadResult<Role>>() {
//            @Override
//            protected void load(Object loadConfig, AsyncCallback<BasePagingLoadResult<Role>> callback) {
//                roleServiceAsync.getRoles((BasePagingLoadConfig) loadConfig, callback);
//            }
//        };
//        BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy);
//        ListStore<Role> roleStore = new ListStore<Role>(loader);
//        ListFilter roleFilter = new ListFilter("role", roleStore);
//        loader.load();

        filters.addFilter(attachRoleFilter());
        return filters;
    }

    private ListFilter attachRoleFilter() {
        ListStore<Role> roleStore = new ListStore<Role>();
        roleStore.add(new Role("admin"));
        roleStore.add(new Role("user"));
        ListFilter roleFilter = new ListFilter("role", roleStore);
        roleFilter.setDisplayProperty("name");
        return roleFilter;
    }

    private void styleGrid() {
        grid.setStripeRows(true);
        grid.addPlugin(selectionRowPlugin);
        grid.addPlugin(attachFilters());
        grid.setSelectionModel(selectionRowPlugin);
        grid.getSelectionModel().bind(store);
        grid.setSize(700, 350);
        view.add(grid);
    }

    private void addGridOnAttachListener() {
        grid.addListener(Events.Attach, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent baseEvent) {
                // Scheduler.get().scheduleDeferred(makeGridOnAttachScheduler());
                loader.load(0, USERS_ON_PAGE);
            }
        });
    }

    private Scheduler.ScheduledCommand makeGridOnAttachScheduler() {
        return new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                config.setLimit(USERS_ON_PAGE);
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
        };
    }

    private Button makeNewEditUserBtn(String name, String id) {
        Button button = new Button(name);
        button.setId(id);
        addAddEditButtonListener(button);
        return button;
    }

    private Button makeDeleteBtn() {
        Button button = new Button("Delete");
        addDeleteButtonListener(button);
        return button;
    }

    private void addAddEditButtonListener(final Button button) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if (button.getId().equals("edit-btn")) {
                    showUserFormWindow(grid.getSelectionModel().getSelectedItem());
                } else {
                    showUserFormWindow(null);
                }
            }
        });
    }

    private void addDeleteButtonListener(Button button) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                final List<User> selectedUsers = selectionRowPlugin.getSelectedItems();
                if (!selectedUsers.isEmpty()) {
                    MessageBox.confirm("Delete?", "Are you sure?", makeConfirmDeleteListener());
                }
            }
        });
    }

    private Listener<MessageBoxEvent> makeConfirmDeleteListener() {
        return new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(MessageBoxEvent be) {
                Button btn = be.getButtonClicked();
                if (Dialog.YES.equalsIgnoreCase(btn.getItemId())) {
                    for (User user : (List<User>) selectionRowPlugin.getSelectedItems()) {
                        userServiceAsync.remove(user, makeDeleteUserAsyncCallback());
                    }
                }
            }
        };
    }

    private AsyncCallback<Void> makeDeleteUserAsyncCallback() {
        return new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }

            @Override
            public void onSuccess(Void aVoid) {
                Info.display("Success", "Deleted");
                grid.getStore().getLoader().load();
            }
        };
    }

    private void alignColumns(Style.HorizontalAlignment side) {
        Iterator<ColumnConfig> iterator = columns.iterator();
        while (iterator.hasNext()) {
            iterator.next().setAlignment(side);
        }
    }

    private void showUserFormWindow(User user) {
        view.setEnabled(false);
        com.extjs.gxt.ui.client.widget.Window window =
                new com.extjs.gxt.ui.client.widget.Window();
        window.setClosable(false);
        window.add(new UserForm(grid, user, userServiceAsync, roleServiceAsync, window, view));
        window.setWidth(350);
        window.show();
    }

}