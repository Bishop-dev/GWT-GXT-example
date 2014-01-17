package com.hubachov.dbmanager.transactional;

import java.sql.Connection;

public class JdbcConnectionHolder {
    private static ThreadLocal<Connection> holder = new ThreadLocal<Connection>();

    public static void set(Connection connection) {
        holder.set(connection);
    }

    public static void unset() {
        holder.remove();
    }

    public static Connection get() {
        return holder.get();
    }
}
