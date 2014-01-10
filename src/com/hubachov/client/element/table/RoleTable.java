package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.TaskEntryPoint;
import com.hubachov.client.model.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoleTable extends LayoutContainer {
    private static final int ROLES_ON_PAGE = 10;
    private RpcProxy<BasePagingLoadResult<Role>> proxy;
    private PagingLoader<PagingLoadResult<Role>> loader;
    private List<ColumnConfig> columns;
    private Grid<Role> grid;
    private ListStore<Role> store;
    private final RowEditor<Role> rowEditor = new RowEditor<Role>();
    private CheckBoxSelectionModel selectionRowPlugin = new CheckBoxSelectionModel<Role>();
    private ContentPanel view = new ContentPanel();

    @Override
    public void onRender(Element parent, int index) {
        super.onRender(parent, index);
        initProxy();
        initLoader();
        attachToolbar();
        configureColumns();
        grid = new EditorGrid<Role>(store, new ColumnModel(columns));
        addOnAttachGridListener();
        styleGrid();
        styleView();
        add(view);
    }

    private Button makeSaveBtn() {
        Button button = new Button("Save");
        addSaveRoleListener(button);
        return button;
    }

    private Button makeResetBtn() {
        Button button = new Button("Reset");
        addResetChangesListener(button);
        return button;
    }

    private Button makeNewBtn() {
        Button button = new Button("Add");
        addSaveNewRoleListener(button);
        return button;
    }

    private Button makeDeleteBtn() {
        Button button = new Button("Delete");
        addDeleteRoleListener(button);
        return button;
    }

    private void addSaveRoleListener(Button button) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                boolean reload = false;
                for (Record record : store.getModifiedRecords()) {
                    Role role = (Role) record.getModel();
                    String newName = role.get("name");
                    role.setName(newName);
                    if (role.getId() == 0) {
                        TaskEntryPoint.roleService.create(role, getUpdateAsyncCallback());
                        reload = true;
                    } else {
                        TaskEntryPoint.roleService.update(role, getUpdateAsyncCallback());
                    }
                }
                if (reload) {
                    loader.load();
                }
            }
        });
    }

    private void addResetChangesListener(Button button) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                store.rejectChanges();
            }
        });
    }

    private void addSaveNewRoleListener(Button button) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                Role role = new Role(0L, "");
                rowEditor.stopEditing(false);
                store.insert(role, store.getCount());
                rowEditor.startEditing(store.indexOf(role), true);
            }
        });
    }

    private void addDeleteRoleListener(Button button) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                final List<Role> selectedRoles = selectionRowPlugin.getSelectedItems();
                if (!selectedRoles.isEmpty()) {
                    MessageBox.confirm("Delete", "Are you sure?", makeConfirmDeleteListener());
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
                    for (Role role : (List<Role>) selectionRowPlugin.getSelectedItems()) {
                        TaskEntryPoint.roleService.remove(role, makeDeleteRoleAsyncCallback());
                    }
                }
            }
        };
    }

    private AsyncCallback<Void> makeDeleteRoleAsyncCallback() {
        return new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                com.google.gwt.user.client.Window.alert(throwable.getMessage());
            }

            @Override
            public void onSuccess(Void aVoid) {
                Info.display("Success", "Deleted");
                loader.load();
            }
        };
    }

    private AsyncCallback<Void> getUpdateAsyncCallback() {
        return new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Info.display("Error", "Can't update");
            }

            @Override
            public void onSuccess(Void result) {
                store.commitChanges();
            }
        };
    }

    private void initProxy() {
        proxy = new RpcProxy<BasePagingLoadResult<Role>>() {
            @Override
            protected void load(Object config, AsyncCallback<BasePagingLoadResult<Role>> callback) {
                TaskEntryPoint.roleService.getRoles((BasePagingLoadConfig) config, callback);
            }
        };
    }

    private void initLoader() {
        loader = new BasePagingLoader<PagingLoadResult<Role>>(proxy);
        loader.setRemoteSort(true);
        loader.setSortField("id");
        loader.setSortDir(Style.SortDir.ASC);
    }

    private void configureColumns() {
        columns = new ArrayList<ColumnConfig>();
        columns.add(selectionRowPlugin.getColumn());
        columns.add(new ColumnConfig("id", "Id", 30));
        ColumnConfig nameColumnConfig = new ColumnConfig("name", "Name", 100);
        TextField<String> field = new TextField<String>();
        field.setAllowBlank(false);
        nameColumnConfig.setEditor(new CellEditor(field));
        columns.add(nameColumnConfig);
    }

    private void styleGrid() {
        grid.setStateId("roleGrid");
        grid.setStateful(true);
        grid.setLoadMask(true);
        grid.setBorders(true);
        grid.addPlugin(rowEditor);
        grid.addPlugin(selectionRowPlugin);
        grid.setSelectionModel(selectionRowPlugin);
        grid.setSize(600, 350);
        grid.getAriaSupport().setLabelledBy(view.getId());
    }

    private void addOnAttachGridListener() {
        grid.addListener(Events.Attach, new Listener<GridEvent<Role>>() {
            @Override
            public void handleEvent(GridEvent<Role> baseEvent) {
                Scheduler.get().scheduleDeferred(makeOnAttachScheduler());
            }
        });
    }

    private Scheduler.ScheduledCommand makeOnAttachScheduler() {
        return new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                config.setLimit(ROLES_ON_PAGE);
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

    private void attachToolbar() {
        store = new ListStore<Role>(loader);
        PagingToolBar pagingToolBar = new PagingToolBar(ROLES_ON_PAGE);
        pagingToolBar.bind(loader);
        view.setBottomComponent(pagingToolBar);
        ToolBar toolBar = new ToolBar();
        toolBar.add(makeSaveBtn());
        toolBar.add(makeResetBtn());
        toolBar.add(makeNewBtn());
        toolBar.add(makeDeleteBtn());
        view.setHeading("Editable Role Grid");
        view.setFrame(true);
        view.setSize(800, 350);
        view.setLayout(new FitLayout());
        view.setBottomComponent(pagingToolBar);
        view.setTopComponent(toolBar);
    }

    private void styleView() {
        view.setFrame(true);
        view.setCollapsible(true);
        view.setAnimCollapse(false);
        view.setLayout(new FitLayout());
        view.add(grid);
        view.setSize(600, 350);
    }

}
