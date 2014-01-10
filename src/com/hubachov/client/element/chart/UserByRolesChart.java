package com.hubachov.client.element.chart;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.PieDataProvider;
import com.extjs.gxt.charts.client.model.charts.ChartConfig;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.TaskEntryPoint;
import com.hubachov.client.model.Role;
import com.hubachov.client.model.User;

import java.util.*;

public class UserByRolesChart extends LayoutContainer {

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        ContentPanel cp = new ContentPanel();
        cp.setHeading("Chart example");
        cp.setFrame(true);
        cp.setSize(550, 400);
        cp.setLayout(new FitLayout());
        String url = "gxt/chart/open-flash-chart.swf";
        Chart chart = new Chart(url);
        chart.setBorders(true);
        chart.setChartModel(getPieChart());
        cp.add(chart);
        add(cp);
    }

    public ChartModel getPieChart() {
        ChartModel cm = new ChartModel("Users by Roles", "font-size: 14px; font-family: Verdana; text-align: center;");
        cm.setBackgroundColour("#fffff0");
        PieChart pie = new PieChart();
        stylePieChart(pie);
        //attachOverriddenVariant(pie);
        attachData(pie);
        cm.addChartConfig(pie);
        return cm;
    }

    private void stylePieChart(PieChart pie) {
        pie.setAlpha(0.5f);
        pie.setTooltip("#label# #val#<br>#percent#");
        pie.setAnimate(false);
        pie.setAlphaHighlight(true);
        pie.setGradientFill(true);
        pie.setColours("#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff");
    }

    private void attachData(PieChart pie) {
        RpcProxy<BasePagingLoadResult<Role>> proxy = new RpcProxy<BasePagingLoadResult<Role>>() {
            @Override
            protected void load(Object loadConfig, AsyncCallback<BasePagingLoadResult<Role>> callback) {
                TaskEntryPoint.roleService.loadRoleStatistic((BaseListLoadConfig) loadConfig, callback);
            }
        };
        BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy);
        ListStore<Role> store = new ListStore<Role>(loader);
        PieDataProvider provider = new PieDataProvider("number", "name");
        provider.bind(store);
        pie.setDataProvider(provider);
        loader.load();
    }

    private void attachOverriddenVariant(PieChart pie) {
        RpcProxy<BasePagingLoadResult<User>> proxy = new RpcProxy<BasePagingLoadResult<User>>() {
            @Override
            protected void load(Object loadConfig, AsyncCallback<BasePagingLoadResult<User>> callback) {
                TaskEntryPoint.userService.loadUsers((BaseListLoadConfig) loadConfig, callback);
            }
        };
        BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy);
        ListStore<User> store = new ListStore<User>(loader);
        PieDataProvider provider = new PieDataProvider("role") {
            @Override
            public void populateData(ChartConfig config) {
                PieChart chart = (PieChart) config;
                chart.getValues().clear();
                Map<Role, Integer> statistics = new HashMap<Role, Integer>();
                for (ModelData model : store.getModels()) {
                    User user = (User) model;
                    Role role = user.getRole();
                    Integer oldValue = statistics.get(role);
                    if (oldValue == null) {
                        oldValue = 0;
                    }
                    statistics.put(role, ++oldValue);
                }
                for (Role role : statistics.keySet()) {
                    chart.addSlices(new PieChart.Slice(statistics.get(role), role.getName()));
                }
            }
        };
        provider.bind(store);
        pie.setDataProvider(provider);
        loader.load();
    }

}