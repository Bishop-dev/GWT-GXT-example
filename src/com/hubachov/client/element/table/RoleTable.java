package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.hubachov.client.model.Role;

import java.util.ArrayList;
import java.util.List;

public class RoleTable extends LayoutContainer {
    private List<Role> roles;

    public RoleTable(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    protected void onAttach() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        configs.add(new ColumnConfig("id", "Id", 30));
        configs.add(new ColumnConfig("name", "Name", 100));
        ListStore<Role> roleListStore = new ListStore<Role>();
        roleListStore.add(roles);
        ColumnModel cm = new ColumnModel(configs);
        Grid<Role> grid = new Grid<Role>(roleListStore, cm);
        grid.setStripeRows(true);
        add(grid);
    }
}
