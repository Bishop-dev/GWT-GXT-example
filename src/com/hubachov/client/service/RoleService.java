package com.hubachov.client.service;

import com.extjs.gxt.ui.client.data.BaseListLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.hubachov.client.model.Role;

import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath(value = "RoleService")
public interface RoleService extends RemoteService {
    public BasePagingLoadResult<Role> getRoles(BasePagingLoadConfig config) throws Exception;

    public void update(List<Role> role) throws Exception;

    public BasePagingLoadResult<Role> loadRoles(BaseListLoadConfig loadConfig) throws Exception;

    public BasePagingLoadResult<Role> loadRoleStatistic(BaseListLoadConfig loadConfig) throws Exception;
}
