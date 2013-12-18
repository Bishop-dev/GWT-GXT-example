package com.hubachov.server;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hubachov.client.model.Role;
import com.hubachov.client.service.RoleService;
import com.hubachov.dao.RoleDAO;
import com.hubachov.dao.impl.jdbc.RoleDAOJDBC;

import java.util.ArrayList;
import java.util.List;

public class RoleServiceImpl extends RemoteServiceServlet implements RoleService {
    private RoleDAO dao = new RoleDAOJDBC();

    @Override
    public BasePagingLoadResult<Role> getRoles(BasePagingLoadConfig config) throws Exception {
        List<Role> roles = dao.findAll();
        int start = config.getOffset();
        int limit = roles.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        List<Role> result = new ArrayList<Role>();
        for (int i = start; i < limit; i++) {
            result.add(roles.get(i));
        }
        return new BasePagingLoadResult<Role>(result);
    }
}
