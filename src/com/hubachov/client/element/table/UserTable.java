package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
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
    private ContentPanel view = new ContentPanel();

    public UserTable(final UserServiceAsync userServiceAsync, RoleServiceAsync roleServiceAsync) {
        this.userServiceAsync = userServiceAsync;
        this.roleServiceAsync = roleServiceAsync;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        BasePagingLoader<PagingLoadResult<User>> loader = new BasePagingLoader<PagingLoadResult<User>>(initProxy());
        ListStore<User> store = new ListStore<User>(loader);
        Grid<User> grid = new Grid<User>(store, new ColumnModel(configureColumns()));
        addGridOnAttachListener(grid, loader);
        attachToolbars(loader, grid);
        styleGrid(grid, store);
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

    private void attachToolbars(BasePagingLoader<PagingLoadResult<User>> loader, Grid<User> grid) {
        PagingToolBar pagingToolBar = new PagingToolBar(10);
        pagingToolBar.bind(loader);
        ToolBar toolBar = new ToolBar();
        view.setHeading("Editable User Grid");
        view.setFrame(true);
        view.setSize(700, 300);
        view.setLayout(new FitLayout());
        view.setBottomComponent(pagingToolBar);
        view.setTopComponent(toolBar);
        toolBar.add(makeEditBtn(grid));
        toolBar.add(makeDeleteBtn(grid));
    }

    private void styleGrid(Grid<User> grid, ListStore<User> listStore) {
        grid.setStripeRows(true);
        grid.setColumnLines(true);
        grid.getSelectionModel().bind(listStore);
        grid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
        grid.setSize(600, 300);
    }

    private void addGridOnAttachListener(final Grid<User> grid, final BasePagingLoader<PagingLoadResult<User>> loader) {
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

    private Button makeEditBtn(Grid<User> grid) {
        Button button = new Button("Edit");
        addEditButtonListener(button, grid);
        return button;
    }

    private Button makeDeleteBtn(Grid grid) {
        Button button = new Button("Delete");
        addDeleteButtonListener(button, grid);
        return button;
    }

    private void addEditButtonListener(Button button, final Grid<User> grid) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                add(new EditForm(grid.getSelectionModel().getSelectedItem(), userServiceAsync, roleServiceAsync));
                layout(true);
            }
        });
    }

    private void addDeleteButtonListener(Button button, final Grid<User> grid) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                User user = grid.getSelectionModel().getSelectedItem();
                if (user != null) {
                    //for dialog version
//                    attachDeleteConfirmDialog(grid);
//                    layout(true);
                    //without confirmation
                    userServiceAsync.remove(user, new DeleteUserAsyncCallback<Void>(grid));
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

    private void attachDeleteConfirmDialog(final Grid<User> grid) {
        final DialogBox box = initDeleteDialog();
        attachYESButton(box, grid);
        attachNOButton(box);
        add(box);
    }

    private DialogBox initDeleteDialog() {
        DialogBox dialog = new DialogBox(false, true);
        dialog.setText("Delete this user?");
        dialog.setVisible(true);
        return dialog;
    }

    private void attachYESButton(final DialogBox dialog, final Grid<User> grid) {
        Button button = new Button("YES");
        addYESListener(button, grid, dialog);
        dialog.add(button);
    }

    private void attachNOButton(final DialogBox dialog) {
        Button button = new Button("NO");
        addNOListener(button, dialog);
        //something wrong with next line :-(
        //dialog.add(button);
    }

    private void addYESListener(Button button, final Grid<User> grid, final DialogBox dialog) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                User user = grid.getSelectionModel().getSelectedItem();
                userServiceAsync.remove(user, new DeleteUserAsyncCallback<Void>(grid));
                removeDialog(dialog);
            }
        });
    }

    private void addNOListener(final Button button, final DialogBox dialogBox) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                removeDialog(dialogBox);
            }
        });
    }

    private void removeDialog(DialogBox dialogBox) {
        remove(dialogBox);
        layout(true);
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
