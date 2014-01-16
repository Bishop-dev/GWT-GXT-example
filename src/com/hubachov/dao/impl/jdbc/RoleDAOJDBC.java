package com.hubachov.dao.impl.jdbc;

import com.hubachov.client.model.Role;
import com.hubachov.client.model.User;
import com.hubachov.dao.RoleDAO;
import com.hubachov.dbmanager.DBUtil;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoleDAOJDBC implements RoleDAO {
    private static Logger log = Logger.getLogger(RoleDAOJDBC.class);
    private static final String SQL__GET_ALL = "SELECT * FROM ROLE";
    private static final String SQL__CREATE_ROLE = "INSERT INTO Role (role_name) VALUES(?)";
    private static final String SQL__UPDATE_ROLE = "UPDATE Role SET role_name=? WHERE role_id=?";
    private static final String SQL__REMOVE_ROLE = "DELETE FROM Role WHERE role_id=?";
    private static final String SQL__FIND_BY_NAME = "SELECT * FROM Role WHERE role_name=?";
    private static final String SQL__GAIN_STATISTIC = "SELECT ROLE.ROLE_NAME, COUNT(*) " +
            "FROM ROLE INNER JOIN USER_ROLE ON ROLE.ROLE_ID=USER_ROLE.ROLE_ID GROUP BY ROLE.ROLE_NAME";
    private static final String CALLABLE_GAIN_STATISTIC = "call statistic()";
    private static final String CALLABLE__ENRICH_USER = "call enrichuser(?)";
    private static final String CALLABLE__SAVE_USER_ROLE = "call saveUserRoles(?,?)";
    private static final String SQL__REMOVE_USER_ROLES = "DELETE FROM user_role ur WHERE ur.user_id=?";

    @Override
    public List<Role> findAll() throws Exception {
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL__GET_ALL);
        ResultSet set = preparedStatement.executeQuery();
        List<Role> result = new ArrayList<Role>();
        try {
            while (set.next()) {
                result.add(extractRole(set));
            }
        } catch (SQLException e) {
            log.error("Can't get roles", e);
            throw e;
        } finally {
            DBUtil.closeAll(set, null, preparedStatement, connection);
        }
        return result;
    }

    @Override
    public synchronized void create(Role role) throws Exception {
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL__CREATE_ROLE,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, role.getName());
            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
            if (resultSet.first()) {
                role.setId(resultSet.getLong(1));
            } else {
                throw new SQLException("Role id was not generated");
            }
        } catch (SQLException e) {
            log.error("Can't save role: " + role.toString(), e);
        } finally {
            DBUtil.closeAll(resultSet, null, statement, connection);
        }
    }

    @Override
    public synchronized void update(Role role) throws Exception {
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL__UPDATE_ROLE);
            statement.setString(1, role.getName());
            statement.setLong(2, role.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Can't update role#" + role.getId(), e);
        } finally {
            DBUtil.closeAll(null, null, statement, connection);
        }
    }

    @Override
    public synchronized void remove(Role role) throws Exception {
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL__REMOVE_ROLE);
            statement.setLong(1, role.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Can't remove role#" + role.getId()
                    + ". Most likely User table has reference to this role.", e);
        } finally {
            DBUtil.closeAll(null, null, statement, connection);
        }
    }

    @Override
    public synchronized Role findByName(String name) throws Exception {
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL__FIND_BY_NAME);
            statement.setString(1, name);
            resultSet = statement.executeQuery();
            if (resultSet.first()) {
                return extractRole(resultSet);
            }
        } catch (SQLException e) {
            log.error("Can't find role with name \"" + name + "\"", e);
        } finally {
            DBUtil.closeAll(resultSet, null, statement, connection);
        }
        log.warn("Role with name \"" + name + "\" doesn't exist");
        return null;
    }

    @Override
    public List<Role> getStatistic() throws Exception {
        Connection connection = DBUtil.getInstance().getConnection();
        CallableStatement callableStatement = connection.prepareCall(CALLABLE_GAIN_STATISTIC);
        List<Role> result = new ArrayList<Role>();
        ResultSet resultSet = null;
        try {
            callableStatement.execute();
            resultSet = callableStatement.getResultSet();
            while (resultSet.next()) {
                Role role = new Role();
                role.setName(resultSet.getString(1));
                role.set("number", resultSet.getInt(2));
                result.add(role);
            }
        } catch (Exception e) {
            log.error("Can't get roles", e);
            throw e;
        } finally {
            DBUtil.closeAll(resultSet, callableStatement, null, connection);
        }
        return result;
    }

    @Override
    public void enrichUser(User user) throws Exception {
        Set<Role> roles = new HashSet<Role>();
        Connection connection = DBUtil.getInstance().getConnection();
        ResultSet resultSet = null;
        CallableStatement statement = connection.prepareCall(CALLABLE__ENRICH_USER);
        statement.setLong(1, user.getId());
        try {
            statement.execute();
            resultSet = statement.getResultSet();
            while (resultSet.next()) {
                roles.add(extractRole(resultSet));
            }
            user.setRoles(roles);
        } catch (Exception e) {
            log.error("Can't enrich user#" + user.getId(), e);
            throw e;
        } finally {
            DBUtil.closeAll(resultSet, statement, null, connection);
        }
    }

    @Override
    public void saveUserRoles(User user) throws Exception {
        Connection connection = DBUtil.getInstance().getConnection();
        saveUserRoles(user, connection);
    }

    private void saveUserRoles(User user, Connection connection) throws Exception {
        CallableStatement statement = connection.prepareCall(CALLABLE__SAVE_USER_ROLE);
        try {
            for (Role role : user.getRoles()) {
                statement.setLong(1, user.getId());
                statement.setLong(2, role.getId());
                statement.addBatch();
            }
            int[] result = statement.executeBatch();
            for (Integer code : result) {
                if (code.equals(Statement.EXECUTE_FAILED)) {
                    throw new SQLException("Failed to insert row#" + code);
                }
            }
        } catch (Exception e) {
            log.error("Can't save roles of user#" + user.getId(), e);
            throw e;
        } finally {
            DBUtil.closeAll(null, statement, null, connection);
        }
    }

    @Override
    public void resetRoles(User user) throws Exception {
        Connection connection = DBUtil.getInstance().getConnection();
        removeUserRoles(user, connection);
        saveUserRoles(user, connection);
    }

    private void removeUserRoles(User user, Connection connection) throws Exception {
        PreparedStatement statement = null;
        statement = connection.prepareStatement(SQL__REMOVE_USER_ROLES);
        statement.setLong(1, user.getId());
        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Can't remove roles of user#" + user.getId(), e);
        } finally {
            DBUtil.closeAll(null, null, statement, null);
        }
    }

    private List<Role> gainStatisticFromPreparedStatement() throws Exception {
        List<Role> result = new ArrayList<Role>();
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL__GAIN_STATISTIC);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Role role = new Role();
                role.setName(resultSet.getString(1));
                role.set("number", resultSet.getInt(2));
                result.add(role);
            }
        } catch (Exception e) {
            log.error("Can't calculate statistic", e);
            throw e;
        } finally {
            DBUtil.closeAll(resultSet, null, statement, connection);
        }
        return result;
    }

    private synchronized Role extractRole(ResultSet resultSet)
            throws SQLException {
        Role role = new Role();
        try {
            role.setId(resultSet.getLong(1));
            role.setName(resultSet.getString(2));
        } catch (SQLException e) {
            log.error("Can't extract role", e);
        }
        return role;
    }

}
