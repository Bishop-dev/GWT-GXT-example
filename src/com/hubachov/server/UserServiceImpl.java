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
    public BasePagingLoadResult<User> getUsers(FilterPagingLoadConfig config) throws Exception {
        List<User> roles = dao.findAll();
        if (config.getSortInfo().getSortField() != null) {
            final String sortField = config.getSortInfo().getSortField();
            Collections.sort(roles, new Comparator<User>() {
                @Override
                public int compare(User first, User second) {
                    if (sortField.equals("login")) {
                        return first.getLogin().compareTo(second.getLogin());
                    }
                    if (sortField.equals("email")) {
                        return first.getEmail().compareTo(second.getEmail());
                    }
                    if (sortField.equals("firstName")) {
                        return first.getFirstName().compareTo(second.getFirstName());
                    }
                    if (sortField.equals("lastName")) {
                        return first.getLastName().compareTo(second.getLastName());
                    }
                    if (sortField.equals("birthday")) {
                        return first.getBirthday().compareTo(second.getBirthday());
                    }
                    if (sortField.equals("role")) {
                        return first.getRole().getName().compareTo(second.getRole().getName());
                    }
                    return (int) (first.getId() - second.getId());
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

    @Override
    public void remove(User user) throws Exception {
        try {
            dao.remove(user);
        } catch (Exception e) {

        }
    }

    @Override
    public void create(User user) throws Exception {
        try {
            user.setRole(roleDAO.findByName(user.get("role").toString()));
            dao.create(user);
        } catch (Exception e) {

        }
    }

    @Override
    public boolean checkLogin(String login) throws Exception {
        try {
            return dao.findByLogin(login) == null;
        } catch (Exception e) {

        }
        return false;
    }
}
