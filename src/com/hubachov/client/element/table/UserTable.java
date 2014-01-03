package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
    private List<ColumnConfig> columns;
    private ListStore<User> store;
    private ContentPanel view = new ContentPanel();

    public UserTable(UserServiceAsync userServiceAsync, RoleServiceAsync roleServiceAsync) {
        this.userServiceAsync = userServiceAsync;
        this.roleServiceAsync = roleServiceAsync;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        BasePagingLoader<PagingLoadResult<User>> loader = new BasePagingLoader<PagingLoadResult<User>>(initProxy());
        configureColumns();
        store = new ListStore<User>(loader);
        grid = new EditorGrid<User>(store, new ColumnModel(columns));
        addGridOnAttachListener(loader);
        attachToolbars(loader);
        styleGrid(store);
        view.add(grid);
        add(view);
    }

    private void configureColumns() {
        columns = new ArrayList<ColumnConfig>();
        columns.add(new ColumnConfig("id", "Id", 30));
        columns.add(new ColumnConfig("login", "Login", 100));
        columns.add(new ColumnConfig("email", "Email", 100));
        columns.add(new ColumnConfig("firstName", "First Name", 100));
        columns.add(new ColumnConfig("lastName", "Last Name", 100));
        columns.add(new ColumnConfig("birthday", "Birthday", 100));
        columns.add(new ColumnConfig("role", "Role", 120));
        CheckColumnConfig checkColumnConfig = new CheckColumnConfig("remove", "Delete?", 100);
        CellEditor editor = new CellEditor(new CheckBox());
        checkColumnConfig.setEditor(editor);
        columns.add(checkColumnConfig);
        alignColumns(Style.HorizontalAlignment.LEFT);
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
        view.setSize(800, 300);
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
        grid.setSize(700, 300);
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
                final List<Record> records = store.getModifiedRecords();
                if (!records.isEmpty()) {
                    MessageBox.confirm("Delete?", "Are you sure?", new Listener<MessageBoxEvent>() {
                        @Override
                        public void handleEvent(MessageBoxEvent be) {
                            Button btn = be.getButtonClicked();
                            if (Dialog.YES.equalsIgnoreCase(btn.getItemId())) {
                                for (Record record : records) {
                                    Boolean isChecked = (Boolean) record.get("remove");
                                    if (isChecked) {
                                        userServiceAsync.remove(extractUser(record), new DeleteUserAsyncCallback<Void>(grid));
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void alignColumns(Style.HorizontalAlignment side) {
        Iterator<ColumnConfig> iterator = columns.iterator();
        while (iterator.hasNext()) {
            iterator.next().setAlignment(side);
        }
    }

    private User extractUser(Record record) {
        User user = new User();
        user.setId((Long) record.get("id"));
        return user;
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