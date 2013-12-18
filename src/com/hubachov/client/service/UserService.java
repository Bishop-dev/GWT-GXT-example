package com.hubachov.client.service;

import com.extjs.gxt.ui.client.data.*;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.hubachov.client.model.User;

@RemoteServiceRelativePath(value = "UserService")
public interface UserService extends RemoteService {
   public BaseListLoadResult<User> getUsers(BaseListLoadConfig config);
}
