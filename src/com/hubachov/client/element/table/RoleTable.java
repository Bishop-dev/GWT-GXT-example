package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.model.Role;
import com.hubachov.client.service.RoleServiceAsync;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoleTable extends LayoutContainer {
    private RoleServiceAsync roleServiceAsync;

    public RoleTable(RoleServiceAsync roleServiceAsync) {
        this.roleServiceAsync = roleServiceAsync;
    }

    @Override
    protected void onAttach() {
        FlowLayout flowLayout = new FlowLayout(10);
        setLayout(flowLayout);
        RpcProxy<BasePagingLoadResult<Role>> proxy = new RpcProxy<BasePagingLoadResult<Role>>() {
            @Override
            protected void load(Object config, AsyncCallback<BasePagingLoadResult<Role>> callback) {
                roleServiceAsync.getRoles((BasePagingLoadConfig) config, callback);
            }
        };
        final PagingLoader<PagingLoadResult<Role>> loader = new BasePagingLoader<PagingLoadResult<Role>>(proxy);
        loader.setRemoteSort(true);
        ListStore<Role> listStore = new ListStore<Role>(loader);
        PagingToolBar toolBar = new PagingToolBar(50);
        toolBar.bind(loader);
        List<ColumnConfig> columnConfigList = new ArrayList<ColumnConfig>();
        columnConfigList.add(new ColumnConfig("id", "Id", 30));
        columnConfigList.add(new ColumnConfig("name", "Name", 100));
        ColumnModel columnModel = new ColumnModel(columnConfigList);
        final Grid<Role> grid = new Grid<Role>(listStore, columnModel);
        grid.setStateId("roleGrid");
        grid.setStateful(true);
        grid.addListener(Events.Attach, new Listener<ComponentEvent>() {
            @Override
            public void handleEvent(ComponentEvent baseEvent) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        PagingLoadConfig config = new BasePagingLoadConfig();
                        config.setOffset(0);
                        config.setLimit(2);
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
                        loader.load(new BaseListLoadConfig());
                    }
                });
            }
        });
        grid.setLoadMask(true);
        grid.setBorders(true);
        ContentPanel panel = new ContentPanel();
        panel.setFrame(true);
        panel.setCollapsible(true);
        panel.setAnimCollapse(false);
        panel.setLayout(new FitLayout());
        panel.add(grid);
        panel.setSize(600, 350);
        panel.setBottomComponent(toolBar);
        grid.getAriaSupport().setLabelledBy(panel.getId());
        loader.load();
        add(panel);
    }
}
