package com.hubachov.dbmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

public class DBUtil {
    private static Logger log = Logger.getLogger(DBUtil.class);
    private static DataSource dataSource;
    private static volatile DBUtil instance;
    private static String propertyFile = "/server_resources/db.properties";

    private DBUtil() {

    }

    public static String getPropertyFile() {
        return propertyFile;
    }

    public static void setPropertyFile(String path) {
        propertyFile = path;
    }

    public static DBUtil getInstance() {
        if (instance == null) {
            synchronized (DBUtil.class) {
                if (instance == null) {
                    instance = new DBUtil();
                }
            }
        }
        return instance;
    }

    public synchronized Connection getConnection() {
        Connection connection = null;
        if (dataSource == null) {
            initPool();
        }
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            log.error("Get connection error", e);
        }
        return connection;
    }

    public synchronized void initPool() {
        Properties properties = new Properties();
        try {
//            properties
//                    .load(DBUtil.class.getResource(propertyFile).openStream());
        } catch (Exception e) {
            log.error("Properties file read error", e);
        }
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            log.error("Driver not found", e);
        }
        GenericObjectPool pool = new GenericObjectPool();
        pool.setMaxActive(10);
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory("jdbc:h2:tcp://10.11.60.15/~/userdb",
                "user", "pswd");
        PoolableConnectionFactory poolableFactory = new PoolableConnectionFactory(
                connectionFactory, pool, null, null, false, true);
        dataSource = new PoolingDataSource(poolableFactory.getPool());
    }

    public static synchronized void closeAll(ResultSet resultSet,
                                             PreparedStatement statement, Connection connection) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error("Can't close DB objects", e);
        }
    }
}
