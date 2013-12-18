package com.hubachov.dao;

import java.io.Serializable;
import java.util.List;

import com.hubachov.client.model.User;

public interface UserDAO extends Serializable {
    public void create(User user) throws Exception;

    public void update(User user) throws Exception;

    public void remove(User user) throws Exception;

    public List<User> findAll() throws Exception;

    public User findByLogin(String login) throws Exception;

    public User findByEmail(String email) throws Exception;
}
