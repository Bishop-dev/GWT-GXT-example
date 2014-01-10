package com.hubachov.client.service;

import com.extjs.gxt.ui.client.data.BaseListLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hubachov.client.model.Role;

import java.util.List;
import java.util.Map;

public interface RoleServiceAsync {
    public void getRoles(BasePagingLoadConfig config, AsyncCallback<BasePagingLoadResult<Role>> async);

    public void update(List<Role> roles, AsyncCallback<Void> asyncCallback);

    public void loadRoles(BaseListLoadConfig loadConfig, AsyncCallback<BasePagingLoadResult<Role>> callback);

    public void loadRoleStatistic(BaseListLoadConfig loadConfig, AsyncCallback<BasePagingLoadResult<Role>> callback);
}
