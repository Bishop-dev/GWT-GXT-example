package com.hubachov.dbmanager.transactional.manager;

import com.hubachov.dbmanager.transactional.manager.impl.jdbc.TransactionManagerJDBC;

public class TransactionManagerHolder {
    public static TransactionManager manager = new TransactionManagerJDBC();

}
