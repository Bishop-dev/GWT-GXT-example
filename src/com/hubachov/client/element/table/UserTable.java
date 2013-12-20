package com.hubachov.client.element.table;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.model.Role;
import com.hubachov.client.model.User;
import com.hubachov.client.service.UserServiceAsync;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserTable extends LayoutContainer {
    private final UserServiceAsync userServiceAsync;
    private static final String REGEX_EMAIL = "^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*\n" +
            "@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$";

    public UserTable(final UserServiceAsync userServiceAsync) {
        this.userServiceAsync = userServiceAsync;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setLayout(new FlowLayout());

        //Preparing columns
        final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        configs.add(new ColumnConfig("id", "Id", 30));
        configs.add(new ColumnConfig("login", "Login", 100));
        ColumnConfig emailColumnConfig = new ColumnConfig("email", "Email", 100);
        ColumnConfig firstNameColumnConfig = new ColumnConfig("firstName", "First Name", 100);
        ColumnConfig lastNameColumnConfig = new ColumnConfig("lastName", "Last Name", 100);
        configs.add(new ColumnConfig("birthday", "Birthday", 100));
        ColumnConfig roleColumnConfig = new ColumnConfig("role", "Role", 70);

        configs.add(firstNameColumnConfig);
        configs.add(lastNameColumnConfig);
        configs.add(emailColumnConfig);
        configs.add(roleColumnConfig);
        //setting of editable properties of cells
        //for Email column
        TextField<String> emailField = new TextField<String>();
        emailField.setAllowBlank(false);
        emailField.setRegex(REGEX_EMAIL);
        emailColumnConfig.setEditor(new CellEditor(emailField));
        //for First Name column
        TextField<String> firstNameField = new TextField<String>();
        firstNameField.setAllowBlank(false);
        firstNameField.setMinLength(3);
        firstNameField.setMaxLength(50);
        firstNameColumnConfig.setEditor(new CellEditor(firstNameField));
        // for Last Name column
        TextField<String> lastNameField = new TextField<String>();
        lastNameField.setAllowBlank(false);
        lastNameField.setMinLength(3);
        lastNameField.setMaxLength(50);
        lastNameColumnConfig.setEditor(new CellEditor(lastNameField));
        //for Role column
        final SimpleComboBox<String> roleComboBox = new SimpleComboBox<String>();
        roleComboBox.setForceSelection(true);
        roleComboBox.setTriggerAction(ComboBox.TriggerAction.ALL);
        roleComboBox.add("User");
        roleComboBox.add("Admin");
        CellEditor roleEditor = new CellEditor(roleComboBox) {
            @Override
            public Object preProcessValue(Object value) {
                return value;
            }

            @Override
            public Object postProcessValue(Object value) {
                if (value == null) {
                    return value;
                }
                return ((User) value).get("role");
            }
        };
        roleColumnConfig.setEditor(roleEditor);
        //align text in grid to left side
        Iterator<ColumnConfig> iterator = configs.iterator();
        while (iterator.hasNext()) {
            iterator.next().setAlignment(Style.HorizontalAlignment.LEFT);
        }
        RpcProxy<BasePagingLoadResult<User>> proxy = new RpcProxy<BasePagingLoadResult<User>>() {
            @Override
            protected void load(Object config, AsyncCallback<BasePagingLoadResult<User>> callback) {
                userServiceAsync.getUsers((BasePagingLoadConfig) config, callback);
            }
        };
        final PagingLoader<PagingLoadResult<Role>> loader = new BasePagingLoader<PagingLoadResult<Role>>(proxy);
        ListStore<User> listStore = new ListStore<User>(loader);
        ColumnModel cm = new ColumnModel(configs);
        final EditorGrid<User> grid = new EditorGrid<User>(listStore, cm);
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
        grid.setStripeRows(true);
        ToolBar toolBar = new ToolBar();
        toolBar.add(new Button("Test"));
        PagingToolBar pagingToolBar = new PagingToolBar(10);
        pagingToolBar.bind(loader);
        ContentPanel panel = new ContentPanel();
        panel.setHeading("Editable User Grid");
        panel.setFrame(true);
        panel.setSize(700, 300);
        panel.setLayout(new FitLayout());
        panel.setTopComponent(toolBar);
        panel.setBottomComponent(pagingToolBar);
        panel.add(grid);
        grid.setSize(600, 300);
        add(panel);
    }
}
