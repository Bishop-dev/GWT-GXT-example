package com.hubachov.client.service;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.hubachov.client.model.Role;

@RemoteServiceRelativePath(value = "roleService")
public interface RoleService extends RemoteService {
    public PagingLoadResult<Role> getAll(PagingLoadConfig config) throws Exception;
}
