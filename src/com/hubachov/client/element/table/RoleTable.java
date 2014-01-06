package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.model.Role;
import com.hubachov.client.service.RoleServiceAsync;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoleTable extends LayoutContainer {
    private static final int ROLES_ON_PAGE = 10;
    private RoleServiceAsync roleServiceAsync;
    private RpcProxy<BasePagingLoadResult<Role>> proxy;
    private PagingLoader<PagingLoadResult<Role>> loader;
    private List<ColumnConfig> columnConfigList;
    private Grid<Role> grid;
    private ListStore<Role> store;
    private ContentPanel view = new ContentPanel();

    public RoleTable(RoleServiceAsync roleServiceAsync) {
        this.roleServiceAsync = roleServiceAsync;
    }

    @Override
    public void onRender(Element parent, int index) {
        super.onRender(parent, index);
        initProxy();
        initLoader();
        attachToolbar();
        configureColumns();
        grid = new EditorGrid<Role>(store, new ColumnModel(columnConfigList));
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

    private void addSaveRoleListener(Button button) {
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                List<Role> roles = new ArrayList<Role>();
                for (Record record : store.getModifiedRecords()) {
                    Role role = (Role) record.getModel();
                    String newName = role.get("name");
                    role.setName(newName);
                    roles.add(role);
                }
                //to prevent excess service calling
                if (!roles.isEmpty()) {
                    roleServiceAsync.update(roles, getUpdateAsyncCallback());
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
                roleServiceAsync.getRoles((BasePagingLoadConfig) config, callback);
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
        columnConfigList = new ArrayList<ColumnConfig>();
        columnConfigList.add(new ColumnConfig("id", "Id", 30));
        ColumnConfig nameColumnConfig = new ColumnConfig("name", "Name", 100);
        TextField<String> field = new TextField<String>();
        field.setAllowBlank(false);
        nameColumnConfig.setEditor(new CellEditor(field));
        columnConfigList.add(nameColumnConfig);
    }

    private void styleGrid() {
        grid.setStateId("roleGrid");
        grid.setStateful(true);
        grid.setLoadMask(true);
        grid.setBorders(true);
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
