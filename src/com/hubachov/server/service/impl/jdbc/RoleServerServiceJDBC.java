package com.hubachov.server.service.impl.jdbc;

import com.hubachov.client.model.Role;
import com.hubachov.dao.RoleDAO;
import com.hubachov.dao.impl.jdbc.RoleDAOJDBC;
import com.hubachov.dbmanager.transactional.manager.TransactionManager;
import com.hubachov.dbmanager.transactional.manager.TransactionManagerHolder;
import com.hubachov.dbmanager.transactional.operation.TransactionOperation;
import com.hubachov.server.service.RoleServerService;

import java.sql.Connection;
import java.util.List;

public class RoleServerServiceJDBC implements RoleServerService {
    private RoleDAO roleDAO = new RoleDAOJDBC();
    private TransactionManager transactionManager = TransactionManagerHolder.manager;

    @Override
    public List<Role> getRoles() throws Exception {
        return (List<Role>) transactionManager.doTransaction(new TransactionOperation() {
            @Override
            public Object execute() throws Exception {
                return roleDAO.findAll();
            }
        }, Connection.TRANSACTION_SERIALIZABLE);
    }

    @Override
    public List<Role> loadStatistic() throws Exception {
        return (List<Role>) transactionManager.doTransaction(new TransactionOperation() {
            @Override
            public Object execute() throws Exception {
                return roleDAO.getStatistic();
            }
        }, Connection.TRANSACTION_SERIALIZABLE);
    }

    @Override
    public void create(final Role role) throws Exception {
        transactionManager.doTransaction(new TransactionOperation() {
            @Override
            public Object execute() throws Exception {
                roleDAO.create(role);
                return null;
            }
        }, Connection.TRANSACTION_SERIALIZABLE);
    }

    @Override
    public void remove(final Role role) throws Exception {
        transactionManager.doTransaction(new TransactionOperation() {
            @Override
            public Object execute() throws Exception {
                roleDAO.remove(role);
                return null;
            }
        }, Connection.TRANSACTION_SERIALIZABLE);
    }

    @Override
    public void update(final Role role) throws Exception {
        transactionManager.doTransaction(new TransactionOperation() {
            @Override
            public Object execute() throws Exception {
                roleDAO.update(role);
                return null;
            }
        }, Connection.TRANSACTION_SERIALIZABLE);
    }

}
