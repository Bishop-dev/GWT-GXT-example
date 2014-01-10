package com.hubachov.client.element;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.Element;
import com.hubachov.client.element.chart.UserByRolesChart;
import com.hubachov.client.element.table.RoleTable;
import com.hubachov.client.element.table.UserTable;

public class MainPage extends LayoutContainer {
    private ContentPanel center;

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        setStyleAttribute("padding", "10px");
        ContentPanel west = new ContentPanel();
        center = new ContentPanel();
        center.setHeading("Content");
        center.setScrollMode(Style.Scroll.AUTOX);
        BorderLayoutData westData = new BorderLayoutData(Style.LayoutRegion.WEST, 150);
        westData.setSplit(true);
        westData.setCollapsible(true);
        westData.setMargins(new Margins(0, 5, 0, 0));
        BorderLayoutData centerData = new BorderLayoutData(Style.LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0));

        west.add(makeUsersButton());
        west.add(makeRolesButton());
        west.add(makeChartsButton());

        add(west, westData);
        add(center, centerData);
    }

    private Button makeUsersButton(){
       return new Button("Users", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                center.removeAll();
                center.add(new UserTable());
                center.layout(true);
            }
        });
    }

    private Button makeRolesButton() {
        return new Button("Roles", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                center.removeAll();
                center.add(new RoleTable());
                center.layout(true);
            }
        });
    }

    private Button makeChartsButton() {
        return new Button("Charts", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                center.removeAll();
                center.add(new UserByRolesChart());
                center.layout(true);
            }
        });
    }
}