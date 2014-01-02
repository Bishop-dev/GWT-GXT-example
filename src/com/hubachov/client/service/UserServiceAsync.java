package com.hubachov.client.service;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.model.User;

public interface UserServiceAsync {
    public void getUsers(BasePagingLoadConfig config, AsyncCallback<BasePagingLoadResult<User>> async);

    public void update(User user, AsyncCallback<Void> async);

    public void remove(User user, AsyncCallback<Void> async);

    public void create(User user, AsyncCallback<Void> async);

    public void checkLogin(String login, AsyncCallback<Boolean> async);
}
