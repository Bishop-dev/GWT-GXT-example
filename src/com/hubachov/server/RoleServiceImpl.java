package com.hubachov.server;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hubachov.client.model.Role;
import com.hubachov.client.service.RoleService;
import com.hubachov.dao.RoleDAO;
import com.hubachov.dao.impl.jdbc.RoleDAOJDBC;

import java.util.List;

public class RoleServiceImpl extends RemoteServiceServlet implements RoleService {
    private RoleDAO dao = new RoleDAOJDBC();

    @Override
    public PagingLoadResult<Role> getAll(PagingLoadConfig config) throws Exception {
        List<Role> roles = dao.findAll();
        int start = config.getOffset();
        int limit = roles.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        return new BasePagingLoadResult<Role>(roles.subList(start, start + limit), start, roles.size());
    }
}
