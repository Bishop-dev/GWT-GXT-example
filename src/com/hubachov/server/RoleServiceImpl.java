package com.hubachov.server;

import com.extjs.gxt.ui.client.data.BaseListLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hubachov.client.model.Role;
import com.hubachov.client.service.RoleService;
import com.hubachov.dao.RoleDAO;
import com.hubachov.dao.impl.jdbc.RoleDAOJDBC;

import java.util.*;

public class RoleServiceImpl extends RemoteServiceServlet implements RoleService {
    private RoleDAO dao = new RoleDAOJDBC();

    @Override
    public BasePagingLoadResult<Role> getRoles(BasePagingLoadConfig config) throws Exception {
        List<Role> roles = dao.findAll();
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
    public void update(List<Role> roles) throws Exception {
        for (Role role : roles) {
            dao.update(role);
        }
    }

    @Override
    public BasePagingLoadResult<Role> loadRoles(BaseListLoadConfig loadConfig) throws Exception {
        return new BasePagingLoadResult<Role>(dao.findAll());
    }

    @Override
    public BasePagingLoadResult<Role> loadRoleStatistic(BaseListLoadConfig loadConfig) throws Exception {
        return new BasePagingLoadResult<Role>(dao.getStatistic());
    }
}
