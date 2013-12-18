package com.hubachov.dao;

import java.io.Serializable;
import java.util.List;

import com.hubachov.client.model.Role;

public interface RoleDAO extends Serializable {

    public List<Role> findAll() throws Exception;

    public void create(Role role) throws Exception;

    public void update(Role role) throws Exception;

    public void remove(Role role) throws Exception;

    public Role findByName(String name) throws Exception;
}
