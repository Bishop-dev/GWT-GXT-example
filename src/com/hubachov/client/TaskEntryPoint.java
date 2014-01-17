package com.hubachov.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.hubachov.client.element.MainPage;
import com.hubachov.client.service.*;

public class TaskEntryPoint implements EntryPoint {
    public static UserClientServiceAsync userService = (UserClientServiceAsync) GWT.create(UserClientService.class);
    public static RoleClientServiceAsync roleService = (RoleClientServiceAsync) GWT.create(RoleClientService.class);

    @Override
    public void onModuleLoad() {
        ServiceDefTarget userServiceDefTarget = (ServiceDefTarget) userService;
        userServiceDefTarget.setServiceEntryPoint(GWT.getModuleBaseURL() + "UserService");
        ServiceDefTarget roleServiceDefTarget = (ServiceDefTarget) roleService;
        roleServiceDefTarget.setServiceEntryPoint(GWT.getModuleBaseURL() + "RoleService");
        RootPanel.get("content").add(new MainPage());
    }
}