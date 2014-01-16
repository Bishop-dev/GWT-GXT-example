package com.hubachov.client.element.form;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.CheckBoxListView;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.TaskEntryPoint;
import com.hubachov.client.model.Role;
import com.hubachov.client.model.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class UserForm extends LayoutContainer {
    private User user;
    private FormPanel form;
    private final Grid<User> grid;
    private final ContentPanel view;
    private CheckBoxListView<Role> roleList;
    private static final String DATE_FORMAT = "MM/dd/y";
    private static final String REGEX_EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|" +
            "}~-]+(?:\\.[a-z0-9!#$%&'*" +
            "+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21" +
            "\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7" +
            "f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?" +
            ":[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9" +
            "][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0" +
            "-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5" +
            "a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private com.extjs.gxt.ui.client.widget.Window window;

    public UserForm(Grid<User> grid, User user, com.extjs.gxt.ui.client.widget.Window window, ContentPanel view) {
        this.user = user;
        this.grid = grid;
        this.window = window;
        this.view = view;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        initForm();
        if (user != null) {
            attachIdField();
        }
        attachLoginField();
        attachPasswordField();
        attachEmailField();
        attachFirstNameField();
        attachLastNameField();
        attachBirthdayField();
        attachRolesField();
        attachButtons();
        add(form);
    }

    private void initForm() {
        form = new FormPanel();
        form.setHeading(user == null ? "Adding User" : "Editing User");
        form.setFrame(true);
        form.setWidth(350);
        form.setHeight(600);
    }

    private void attachIdField() {
        TextField<String> id = new TextField<String>();
        id.setFieldLabel("Id");
        id.setName("id");
        id.setValue(Long.toString(user.getId()));
        id.setEnabled(false);
        form.add(id);
    }

    private void attachLoginField() {
        final TextField<String> login = new TextField<String>();
        login.setFieldLabel("Login");
        login.setName("login");
        login.setAllowBlank(false);
        if (user != null) {
            login.setValue(user.getLogin());
            login.setEnabled(false);
        }
        form.add(login);
    }

    private void attachPasswordField() {
        final TextField<String> password = new TextField<String>();
        password.setFieldLabel("Password");
        password.setName("password");
        password.setPassword(true);
        password.setAllowBlank(false);
        if (user != null) {
            password.setValue(user.getPassword());
        }
        form.add(password);
        final TextField<String> confirmPassword = new TextField<String>();
        confirmPassword.setFieldLabel("Confirm");
        confirmPassword.setName("confirmPassword");
        confirmPassword.setPassword(true);
        confirmPassword.setAllowBlank(false);
        if (user != null) {
            confirmPassword.setValue(user.getPassword());
        }
        confirmPassword.setValidator(new Validator() {
            @Override
            public String validate(Field<?> field, String value) {
                if (password.getValue().equals(confirmPassword.getValue())) {
                    return null;
                }
                return "Passwords do not match";
            }
        });
        form.add(confirmPassword);
    }

    private void attachEmailField() {
        TextField<String> email = new TextField<String>();
        email.setFieldLabel("Email");
        email.setName("email");
        email.setRegex(REGEX_EMAIL);
        email.setAllowBlank(false);
        if (user != null) {
            email.setValue(user.getEmail());
        }
        form.add(email);
    }

    private void attachFirstNameField() {
        TextField<String> firstName = new TextField<String>();
        firstName.setFieldLabel("First Name");
        firstName.setName("firstName");
        firstName.setAllowBlank(false);
        if (user != null) {
            firstName.setValue(user.getFirstName());
        }
        form.add(firstName);
    }

    private void attachLastNameField() {
        TextField<String> lastName = new TextField<String>();
        lastName.setFieldLabel("Last Name");
        lastName.setName("lastName");
        lastName.setAllowBlank(false);
        if (user != null) {
            lastName.setValue(user.getLastName());
        }
        form.add(lastName);
    }

    private void attachBirthdayField() {
        DateField dateField = new DateField();
        dateField.getPropertyEditor().setFormat(DateTimeFormat.getFormat(DATE_FORMAT));
        dateField.setFieldLabel("Birthday");
        dateField.setName("birthday");
        dateField.setAllowBlank(false);
        if (user != null) {
            dateField.setValue(user.getBirthday());
        }
        form.add(dateField);
    }

    private void attachRolesField() {
        roleList = new CheckBoxListView<Role>();
        roleList.setStore(makeStore());
        roleList.setDisplayProperty("name");
        form.add(createRolePanel());
    }

    private ListStore<Role> makeStore() {
        RpcProxy<BaseListLoadResult<Role>> proxy = new RpcProxy<BaseListLoadResult<Role>>() {
            @Override
            protected void load(Object loadConfig, AsyncCallback<BaseListLoadResult<Role>> callback) {
                TaskEntryPoint.roleService.getRoles((BaseListLoadConfig) loadConfig, callback);
            }
        };
        BaseListLoader<ListLoadResult<Role>> loader = new BaseListLoader<ListLoadResult<Role>>(proxy);
        ListStore<Role> roleStore = new ListStore<Role>(loader);
        loader.load();
        addLoaderListener(loader);
        return roleStore;
    }

    private void addLoaderListener(BaseListLoader<ListLoadResult<Role>> loader) {
        loader.addLoadListener(new LoadListener() {
            @Override
            public void loaderLoad(LoadEvent le) {
                for (Role role : user.getRoles()) {
                    roleList.setChecked(role, true);
                }
            }
        });
    }

    private ContentPanel createRolePanel() {
        ContentPanel panel = new ContentPanel();
        panel.setCollapsible(true);
        panel.setAnimCollapse(false);
        panel.setFrame(true);
        panel.setHeading("Roles");
        panel.setWidth(300);
        panel.setAutoHeight(true);
        panel.setBodyBorder(false);
        panel.add(roleList);
        return panel;
    }

    private void attachButtons() {
        Button submitBtn = new Button("Submit");
        Button cancelBtn = new Button("Cancel");
        addPostListener(submitBtn);
        addCancelListener(cancelBtn);
        form.addButton(submitBtn);
        form.addButton(cancelBtn);
        form.setButtonAlign(Style.HorizontalAlignment.CENTER);
        FormButtonBinding binding = new FormButtonBinding(form);
        binding.addButton(submitBtn);
    }

    private void addCancelListener(Button cancelBtn) {
        cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                removeForm();
            }
        });
    }

    private void addPostListener(Button submitBtn) {
        submitBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if (roleList.getChecked().isEmpty()) {
                    Window.alert("Choose at least one role");
                    return;
                }
                if (user != null) {
                    TaskEntryPoint.userService.update(constructUser(), getAsyncCallbackCreateEdit());
                } else {
                    TaskEntryPoint.userService.create(constructUser(), getAsyncCallbackCreateEdit());
                }
            }
        });
    }

    private AsyncCallback<Void> getAsyncCallbackCreateEdit() {
        return new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }

            @Override
            public void onSuccess(Void aVoid) {
                Info.display("Success", "Added");
                removeForm();
            }
        };
    }

    private User constructUser() {
        User user = new User();
        Set<Role> roles = new HashSet<Role>(roleList.getChecked());
        user.setRoles(roles);
        for (Field field : form.getFields()) {
            String fieldName = field.getName();
            if (fieldName.equals("id")) {
                user.setId(Long.parseLong(field.getRawValue()));
            }
            if (fieldName.equals("login")) {
                user.setLogin(field.getRawValue());
            }
            if (fieldName.equals("password")) {
                user.setPassword(field.getRawValue());
            }
            if (fieldName.equals("email")) {
                user.setEmail(field.getRawValue());
            }
            if (fieldName.equals("firstName")) {
                user.setFirstName(field.getRawValue());
            }
            if (fieldName.equals("lastName")) {
                user.setLastName(field.getRawValue());
            }
            if (fieldName.equals("birthday")) {
                user.setBirthday(new Date());
            }
        }
        return user;
    }

    private void removeForm() {
        view.setEnabled(true);
        window.hide();
        layout(true);
        grid.getStore().getLoader().load();
    }

}
