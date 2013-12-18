package com.hubachov.client.service;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.model.Role;

public interface RoleServiceAsync {
    public void getAll(PagingLoadConfig config, AsyncCallback<PagingLoadResult<Role>> callback);
}
