package com.hubachov.dbmanager.transactional.manager.impl.jdbc;

import com.hubachov.dbmanager.DBUtil;
import com.hubachov.dbmanager.transactional.JdbcConnectionHolder;
import com.hubachov.dbmanager.transactional.manager.TransactionManager;
import com.hubachov.dbmanager.transactional.operation.TransactionOperation;

import java.sql.Connection;

public class TransactionManagerJDBC implements TransactionManager {
    private DBUtil dataSource = DBUtil.getInstance();

    @Override
    public Object doTransaction(TransactionOperation operation, int isolation) throws Exception {
        Object result = null;
        Connection connection = dataSource.getConnection();
        JdbcConnectionHolder.set(connection);
        try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(isolation);
            result = operation.execute();
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
        } finally {
            connection.close();
            JdbcConnectionHolder.unset();
        }
        return result;
    }

}
