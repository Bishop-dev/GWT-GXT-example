package com.hubachov.server.service.impl.jdbc;

import com.hubachov.client.model.User;
import com.hubachov.dao.RoleDAO;
import com.hubachov.dao.UserDAO;
import com.hubachov.dao.impl.jdbc.RoleDAOJDBC;
import com.hubachov.dao.impl.jdbc.UserDAOJDBC;
import com.hubachov.dbmanager.transactional.manager.TransactionManager;
import com.hubachov.dbmanager.transactional.manager.TransactionManagerHolder;
import com.hubachov.dbmanager.transactional.operation.TransactionOperation;
import com.hubachov.server.service.UserServerService;

import java.sql.Connection;
import java.util.List;

public class UserServerServiceJDBC implements UserServerService {
    private UserDAO userDAO = new UserDAOJDBC();
    private RoleDAO roleDAO = new RoleDAOJDBC();
    private TransactionManager transactionManager = TransactionManagerHolder.manager;

    @Override
    public List<User> getUsers() throws Exception {
        return (List<User>) transactionManager.doTransaction(new TransactionOperation() {
            @Override
            public Object execute() throws Exception {
                List<User> list = userDAO.findAll();
                for (User user : list) {
                    roleDAO.enrichUser(user);
                }
                return list;
            }
        }, Connection.TRANSACTION_SERIALIZABLE);
    }

    @Override
    public void update(final User user) throws Exception {
        transactionManager.doTransaction(new TransactionOperation() {
            @Override
            public Object execute() throws Exception {
                roleDAO.resetRoles(user);
                userDAO.update(user);
                return null;
            }
        }, Connection.TRANSACTION_SERIALIZABLE);
    }

    @Override
    public void create(final User user) throws Exception {
        transactionManager.doTransaction(new TransactionOperation() {
            @Override
            public Object execute() throws Exception {
                userDAO.create(user);
                roleDAO.saveUserRoles(user);
                return null;
            }
        }, Connection.TRANSACTION_SERIALIZABLE);
    }

    @Override
    public void remove(final User user) throws Exception {
        transactionManager.doTransaction(new TransactionOperation() {
            @Override
            public Object execute() throws Exception {
                userDAO.remove(user);
                return null;
            }
        }, Connection.TRANSACTION_SERIALIZABLE);
    }

    @Override
    public boolean checkLogin(String login) throws Exception {
        return false;
    }

}
