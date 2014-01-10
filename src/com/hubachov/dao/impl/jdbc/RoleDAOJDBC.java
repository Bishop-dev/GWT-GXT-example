package com.hubachov.dao.impl.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hubachov.dao.RoleDAO;
import com.hubachov.dbmanager.DBUtil;
import com.hubachov.client.model.Role;

public class RoleDAOJDBC implements RoleDAO {
    private static Logger log = Logger.getLogger(RoleDAOJDBC.class);
    private static final String SQL__GET_ALL = "SELECT * FROM ROLE";
    private static final String SQL__CREATE_ROLE = "INSERT INTO Role (role_name) VALUES(?);";
    private static final String SQL__UPDATE_ROLE = "UPDATE Role SET role_name=? WHERE role_id=?;";
    private static final String SQL__REMOVE_ROLE = "DELETE FROM Role WHERE role_id=?;";
    private static final String SQL__FIND_BY_NAME = "SELECT * FROM Role WHERE role_name=?;";
    private static final String SQL__GAIN_STATISTIC = "SELECT Role.role_id, Role.role_name, COUNT(*) FROM Role " +
            "INNER JOIN User ON Role.role_id=User.role_id GROUP BY Role.role_name;";

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
            DBUtil.closeAll(set, preparedStatement, connection);
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
            DBUtil.closeAll(resultSet, statement, connection);
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
            DBUtil.closeAll(null, statement, connection);
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
            DBUtil.closeAll(null, statement, connection);
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
            DBUtil.closeAll(resultSet, statement, connection);
        }
        log.warn("Role with name \"" + name + "\" doesn't exist");
        return null;
    }

    @Override
    public List<Role> getStatistic() throws Exception {
        List<Role> result = new ArrayList<Role>();
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL__GAIN_STATISTIC);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Role role = extractRole(resultSet);
                role.set("number", resultSet.getInt("COUNT(*)"));
                result.add(role);
            }
        } catch (Exception e) {
            log.error("Can't calculate statistic", e);
            throw e;
        } finally {
            DBUtil.closeAll(resultSet, statement, connection);
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
