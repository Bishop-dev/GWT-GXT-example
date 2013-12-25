package com.hubachov.client.service;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.hubachov.client.model.User;

@RemoteServiceRelativePath(value = "UserService")
public interface UserService extends RemoteService {
    public BasePagingLoadResult<User> getUsers(BasePagingLoadConfig config) throws Exception;

    public void update(User user) throws Exception;
}
