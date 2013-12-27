package com.hubachov.dao.impl.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hubachov.client.model.Role;
import org.apache.log4j.Logger;

import com.hubachov.dao.UserDAO;
import com.hubachov.dbmanager.DBUtil;
import com.hubachov.client.model.User;

public class UserDAOJDBC implements UserDAO {
    private static Logger log = Logger.getLogger(UserDAOJDBC.class);
    private static final String SQL__CREATE_USER = "INSERT INTO User (role_id, user_login, user_password, user_email, " +
            "user_firstName, user_lastName, user_birthday) values(?,?,?,?,?,?,?);";
    private static final String SQL__SELECT_ALL = "SELECT * FROM User INNER JOIN Role ON User.role_id=Role.role_id;";
    private static final String SQL__FIND_BY_LOGIN = "SELECT * FROM User WHERE user_login=?;";
    private static final String SQL__FIND_BY_EMAIL = "SELECT * FROM User WHERE user_email=?;";
    private static final String SQL__REMOVE_USER = "DELETE FROM User WHERE user_id=?;";
    private static final String SQL__UPDATE_USER = "UPDATE User SET role_id=?, user_password=?, user_email=?, " +
            "user_firstName=?, user_lastName=?, user_birthday=? WHERE user_id=?;";

    @Override
    public synchronized void create(User user) throws SQLException {
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL__CREATE_USER,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setLong(1, user.getRole().getId());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getFirstName());
            statement.setString(6, user.getLastName());
            statement.setDate(7,
                    new java.sql.Date(user.getBirthday().getTime()));
            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
            if (resultSet.first()) {
                user.setId(resultSet.getLong(1));
            } else {
                log.error("User id was not generated");
            }
        } catch (SQLException e) {
            log.error("Can't save user: " + user.toString(), e);
            throw e;
        } finally {
            DBUtil.closeAll(resultSet, statement, connection);
        }
    }

    @Override
    public synchronized void update(User user) throws SQLException {
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL__UPDATE_USER);
            statement.setLong(1, user.getRole().getId());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getFirstName());
            statement.setString(5, user.getLastName());
            statement.setDate(6, new java.sql.Date(user.getBirthday().getTime()));
            statement.setLong(7, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Can't update user#" + user.getId(), e);
            throw e;
        } finally {
            DBUtil.closeAll(null, statement, connection);
        }
    }

    @Override
    public synchronized void remove(User user) throws SQLException {
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL__REMOVE_USER);
            statement.setLong(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Can't remove user#" + user.getId(), e);
            throw e;
        } finally {
            DBUtil.closeAll(null, statement, connection);
        }
    }

    @Override
    public synchronized List<User> findAll() throws SQLException {
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<User> list = new ArrayList<User>();
        try {
            statement = connection.prepareStatement(SQL__SELECT_ALL);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(extractUser(resultSet));
            }
        } catch (SQLException e) {
            log.error("Error in findAll()", e);
            throw e;
        } finally {
            DBUtil.closeAll(resultSet, statement, connection);
        }
        if (list.isEmpty()) {
            log.trace("There is no users");
        }
        return list;
    }

    @Override
    public synchronized User findByLogin(String login) throws SQLException {
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL__FIND_BY_LOGIN);
            statement.setString(1, login);
            resultSet = statement.executeQuery();
            if (resultSet.first()) {
                return extractUser(resultSet);
            }
        } catch (SQLException e) {
            log.error("Can't find user with login \"" + login + "\"", e);
            throw e;
        } finally {
            DBUtil.closeAll(resultSet, statement, connection);
        }
        log.trace("User \"" + login + "\" doesn't exist");
        return null;
    }

    @Override
    public synchronized User findByEmail(String email) throws SQLException {
        Connection connection = DBUtil.getInstance().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL__FIND_BY_EMAIL);
            statement.setString(1, email);
            resultSet = statement.executeQuery();
            if (resultSet.first()) {
                return extractUser(resultSet);
            }
        } catch (SQLException e) {
            log.error("Can't find user with email \"" + email + "\"", e);
            throw e;
        } finally {
            DBUtil.closeAll(resultSet, statement, connection);
        }
        log.trace("User with email \"" + email + "\" doesn't exist");
        return null;
    }

    private synchronized User extractUser(ResultSet result) throws SQLException {
        User user = new User();
        try {
            user.setId(result.getLong("user_id"));
            Role role = new Role();
            role.setId(result.getLong("role_id"));
            role.setName(result.getString("role_name"));
            user.setRole(role);
            user.setLogin(result.getString("user_login"));
            user.setPassword(result.getString("user_password"));
            user.setEmail(result.getString("user_email"));
            user.setFirstName(result.getString("user_firstname"));
            user.setLastName(result.getString("user_lastname"));
            user.setBirthday(result.getDate("user_birthday"));
        } catch (SQLException e) {
            log.error("Error extracting user", e);
            throw e;
        }
        return user;
    }

}
