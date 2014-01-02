package com.hubachov.client.element.form;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.model.Role;
import com.hubachov.client.model.User;
import com.hubachov.client.service.RoleServiceAsync;
import com.hubachov.client.service.UserServiceAsync;

import java.util.*;

public class EditForm extends LayoutContainer {
    private User user;
    private FormPanel form;
    private static final String DATE_FORMAT = "MM/dd/y";
    //    private static final String REGEX_EMAIL = "^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*\n" +
//            "@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$";
    private static final String REGEX_EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|" +
            "}~-]+(?:\\.[a-z0-9!#$%&'*" +
            "+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21" +
            "\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7" +
            "f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?" +
            ":[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9" +
            "][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0" +
            "-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5" +
            "a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private UserServiceAsync userServiceAsync;
    private RoleServiceAsync roleServiceAsync;

    public EditForm(User user, UserServiceAsync userServiceAsync, RoleServiceAsync roleServiceAsync) {
        this.userServiceAsync = userServiceAsync;
        this.roleServiceAsync = roleServiceAsync;
        this.user = user;
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
        } else {

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
        //something wrong
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
        RpcProxy<BasePagingLoadResult<Role>> proxy = new RpcProxy<BasePagingLoadResult<Role>>() {
            @Override
            protected void load(Object loadConfig, AsyncCallback<BasePagingLoadResult<Role>> callback) {
                roleServiceAsync.getRoles((BasePagingLoadConfig) loadConfig, callback);
            }
        };
        BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy);
        ListStore<Role> roleStore = new ListStore<Role>(loader);
        ComboBox<Role> combo = new ComboBox<Role>();
        combo.setFieldLabel("Role");
        combo.setDisplayField("name");
        combo.setName("role");
        combo.setAllowBlank(false);
        if (user != null) {
            combo.setValue(user.getRole());
        }
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(roleStore);
        form.add(combo);
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
                remove(form);
                layout(true);
            }
        });
    }

    private void addPostListener(Button submitBtn) {
        submitBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if (user != null) {
                    userServiceAsync.update(constructUser(), new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            Window.alert(throwable.getMessage());
                        }

                        @Override
                        public void onSuccess(Void aVoid) {
                            Info.display("Success", "Updated");
                        }
                    });
                } else {
                    userServiceAsync.create(constructUser(), new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            Window.alert(throwable.getMessage());
                        }

                        @Override
                        public void onSuccess(Void aVoid) {
                            Info.display("Success", "Added");
                        }
                    });
                }
            }
        });
    }

    private User constructUser() {
        User user = new User();
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
            if (fieldName.equals("role")) {
                user.set("role", field.getRawValue());
            }
            if (fieldName.equals("birthday")) {
                user.setBirthday(new Date());
            }
        }
        return user;
    }

}
