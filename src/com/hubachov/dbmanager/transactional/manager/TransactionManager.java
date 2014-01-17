package com.hubachov.dbmanager.transactional.manager;

import com.hubachov.dbmanager.transactional.operation.TransactionOperation;

public interface TransactionManager {
    public Object doTransaction(TransactionOperation op, int isolation) throws Exception;
}
