package com.hubachov.server;

import com.extjs.gxt.ui.client.data.*;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hubachov.client.model.Role;
import com.hubachov.client.model.User;
import com.hubachov.client.service.UserService;
import com.hubachov.dao.RoleDAO;
import com.hubachov.dao.UserDAO;
import com.hubachov.dao.impl.jdbc.RoleDAOJDBC;
import com.hubachov.dao.impl.jdbc.UserDAOJDBC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserServiceImpl extends RemoteServiceServlet implements UserService {
    private UserDAO dao = new UserDAOJDBC();
    private RoleDAO roleDAO = new RoleDAOJDBC();

    @Override
    public BasePagingLoadResult<User> getUsers(BasePagingLoadConfig config) throws Exception {
        List<User> roles = dao.findAll();
        if (config.getSortInfo().getSortField() != null) {
            final String sortField = config.getSortInfo().getSortField();
            Collections.sort(roles, new Comparator<User>() {
                @Override
                public int compare(User role1, User role2) {
                    if (sortField.equals("login")) {
                        return role1.getLogin().compareTo(role2.getLogin());
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
        List<User> result = new ArrayList<User>();
        for (int i = start; i < limit; i++) {
            result.add(roles.get(i));
        }
        return new BasePagingLoadResult<User>(result, start, roles.size());
    }

    @Override
    public void update(User user) throws Exception {
        try {
            user.setRole(roleDAO.findByName(user.get("role").toString()));
            dao.update(user);
        } catch (Exception e) {
            throw e;
        }
    }
}
