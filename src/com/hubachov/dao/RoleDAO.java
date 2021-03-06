package com.hubachov.dao;

import com.hubachov.client.model.Role;
import com.hubachov.client.model.User;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoleDAO extends Serializable {

    public List<Role> findAll() throws Exception;

    public void create(Role role) throws Exception;

    public void update(Role role) throws Exception;

    public void remove(Role role) throws Exception;

    public Role findByName(String name) throws Exception;

    public List<Role> getStatistic() throws Exception;

    public void enrichUser(User user) throws Exception;

    public void saveUserRoles(User user) throws Exception;

    public void resetRoles(User user) throws Exception;
}
