package com.hubachov.server;

import com.extjs.gxt.ui.client.data.*;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hubachov.client.model.User;
import com.hubachov.client.service.UserService;
import com.hubachov.dao.UserDAO;
import com.hubachov.dao.impl.jdbc.UserDAOJDBC;

public class UserServiceImpl extends RemoteServiceServlet implements UserService {
    private UserDAO dao = new UserDAOJDBC();

    @Override
    public BaseListLoadResult<User> getUsers(BaseListLoadConfig config) {
        try {
            BaseListLoadResult result = new BaseListLoadResult<User>(dao.findAll());
            return result;
        } catch (Exception e) {
        }
        return null;
    }
}
