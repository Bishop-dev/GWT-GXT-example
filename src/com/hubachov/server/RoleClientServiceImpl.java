package com.hubachov.server;

import com.extjs.gxt.ui.client.data.BaseListLoadConfig;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hubachov.client.model.Role;
import com.hubachov.client.service.RoleClientService;
import com.hubachov.dao.RoleDAO;
import com.hubachov.dao.impl.jdbc.RoleDAOJDBC;
import com.hubachov.server.service.RoleServerService;
import com.hubachov.server.service.impl.jdbc.RoleServerServiceJDBC;

import java.util.*;

public class RoleClientServiceImpl extends RemoteServiceServlet implements RoleClientService {
    private RoleServerService roleServerService = new RoleServerServiceJDBC();

    @Override
    public BasePagingLoadResult<Role> getRoles(BasePagingLoadConfig config) throws Exception {
        List<Role> roles = loadRoles();
        if (config.getSortInfo().getSortField() != null) {
            final String sortField = config.getSortInfo().getSortField();
            Collections.sort(roles, new Comparator<Role>() {
                @Override
                public int compare(Role role1, Role role2) {
                    if (sortField.equals("name")) {
                        return role1.getName().compareTo(role2.getName());
                    }
                    return (int) (role1.getId() - role2.getId());
                }
            });
        }
        int start = config.getOffset();
        int limit = roles.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        List<Role> result = new ArrayList<Role>();
        for (int i = start; i < limit; i++) {
            result.add(roles.get(i));
        }
        return new BasePagingLoadResult<Role>(result, start, roles.size());
    }

    @Override
    public void update(Role role) throws Exception {
        roleServerService.update(role);
    }

    @Override
    public void create(Role role) throws Exception {
        roleServerService.create(role);
    }

    @Override
    public BasePagingLoadResult<Role> loadRoles(BaseListLoadConfig loadConfig) throws Exception {
        return new BasePagingLoadResult<Role>(loadRoles());
    }

    @Override
    public BasePagingLoadResult<Role> loadRoleStatistic(BaseListLoadConfig loadConfig) throws Exception {
        return new BasePagingLoadResult<Role>(roleServerService.loadStatistic());
    }

    @Override
    public void remove(Role role) throws Exception {
        roleServerService.remove(role);
    }

    @Override
    public BaseListLoadResult<Role> getRoles(BaseListLoadConfig loadConfig) throws Exception {
        return new BaseListLoadResult<Role>(loadRoles());
    }

    private List<Role> loadRoles() throws Exception {
        return roleServerService.getRoles();
    }

}
