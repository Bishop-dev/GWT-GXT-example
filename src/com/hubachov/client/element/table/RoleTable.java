package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
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
    private ListStore<Role> listStore;
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
        grid = new EditorGrid<Role>(listStore, new ColumnModel(columnConfigList));
        addOnAttachGridListener();
        styleGrid();
        styleView();
        add(view);
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

    private void attachToolbar() {
        listStore = new ListStore<Role>(loader);
        PagingToolBar toolBar = new PagingToolBar(ROLES_ON_PAGE);
        toolBar.bind(loader);
        view.setBottomComponent(toolBar);
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
