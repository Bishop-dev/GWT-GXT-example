package com.hubachov.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.hubachov.client.element.MainPage;
import com.hubachov.client.service.RoleService;
import com.hubachov.client.service.RoleServiceAsync;
import com.hubachov.client.service.UserService;
import com.hubachov.client.service.UserServiceAsync;

public class TaskEntryPoint implements EntryPoint {
    private UserServiceAsync userService = (UserServiceAsync) GWT.create(UserService.class);
    private RoleServiceAsync roleService = (RoleServiceAsync) GWT.create(RoleService.class);

    @Override
    public void onModuleLoad() {
        ServiceDefTarget userServiceDefTarget = (ServiceDefTarget) userService;
        userServiceDefTarget.setServiceEntryPoint(GWT.getModuleBaseURL() + "UserService");
        ServiceDefTarget roleServiceDefTarget = (ServiceDefTarget) roleService;
        roleServiceDefTarget.setServiceEntryPoint(GWT.getModuleBaseURL() + "RoleService");
        RootPanel.get("content").add(new MainPage(userService, roleService));
    }
}