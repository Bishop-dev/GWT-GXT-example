package com.hubachov.server.service;

import com.hubachov.client.model.User;

import java.util.List;

public interface UserServerService {
    public List<User> getUsers() throws Exception;

    public void update(User user) throws Exception;

    public void remove(User user) throws Exception;

    public void create(User user) throws Exception;

    public boolean checkLogin(String login) throws Exception;

}
