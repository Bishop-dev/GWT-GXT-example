package com.hubachov.client.element.form;

import com.extjs.gxt.ui.client.Style;
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
import com.hubachov.client.service.UserServiceAsync;

import java.util.*;

public class EditForm extends LayoutContainer {
    private User user;
    private FormPanel form;
    private static final String DATE_FORMAT = "MM/dd/y";
    private UserServiceAsync userServiceAsync;

    public EditForm(User user, UserServiceAsync userServiceAsync) {
        this.user = user;
        this.userServiceAsync = userServiceAsync;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        initForm();
        attachIdField();
        attachLoginField();
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
        id.setEnabled(false);
        id.setValue(Long.toString(user.getId()));
        form.add(id);
    }

    private void attachLoginField() {
        TextField<String> login = new TextField<String>();
        login.setFieldLabel("Login");
        login.setName("login");
        login.setEnabled(false);
        login.setValue(user.getLogin());
        form.add(login);
    }

    private void attachEmailField() {
        TextField<String> email = new TextField<String>();
        email.setFieldLabel("Email");
        email.setName("email");
        email.setAllowBlank(false);
        email.setValue(user.getEmail());
        form.add(email);
    }

    private void attachFirstNameField() {
        TextField<String> firstName = new TextField<String>();
        firstName.setFieldLabel("First Name");
        firstName.setName("firstName");
        firstName.setAllowBlank(false);
        firstName.setValue(user.getFirstName());
        form.add(firstName);
    }

    private void attachLastNameField() {
        TextField<String> lastName = new TextField<String>();
        lastName.setFieldLabel("Last Name");
        lastName.setName("lastName");
        lastName.setAllowBlank(false);
        lastName.setValue(user.getLastName());
        form.add(lastName);
    }

    private void attachBirthdayField() {
        DateField dateField = new DateField();
        dateField.getPropertyEditor().setFormat(DateTimeFormat.getFormat(DATE_FORMAT));
        dateField.setFieldLabel("Birthday");
        dateField.setName("birthday");
        dateField.setValue(user.getBirthday());
        form.add(dateField);
    }

    private void attachRolesField() {
        ListStore<Role> store = new ListStore<Role>();
        store.add(Arrays.asList(new Role(1L, "admin"), new Role(2L, "user")));
        ComboBox<Role> combo = new ComboBox<Role>();
        combo.setFieldLabel("Role");
        combo.setDisplayField("name");
        combo.setName("role");
        combo.setValue(user.getRole());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);
        form.add(combo);
    }

    private void attachButtons() {
        Button submitBtn = new Button("Submit");
        form.addButton(submitBtn);
        form.addButton(new Button("Cancel"));
        form.setButtonAlign(Style.HorizontalAlignment.CENTER);
        FormButtonBinding binding = new FormButtonBinding(form);
        binding.addButton(submitBtn);
        addPostListener(submitBtn);
    }

    private void addPostListener(Button submitBtn) {
        submitBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                userServiceAsync.update(constructUser(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Window.alert(throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(Void aVoid) {
                        Info.display("Success", "Ololo");
                    }
                });
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
