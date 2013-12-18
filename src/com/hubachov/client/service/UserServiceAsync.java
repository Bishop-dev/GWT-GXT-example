package com.hubachov.client.service;

import com.extjs.gxt.ui.client.data.BaseListLoadConfig;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.model.User;

public interface UserServiceAsync {
   public void getUsers(BaseListLoadConfig config, AsyncCallback<BaseListLoadResult<User>> async);
}
