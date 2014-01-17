package com.hubachov.server.service;

import com.hubachov.client.model.Role;

import java.util.List;

public interface RoleServerService {
    public List<Role> getRoles() throws Exception;

    public List<Role> loadStatistic() throws Exception;

    public void create(Role role) throws Exception;

    public void remove(Role role) throws Exception;

    public void update(Role role) throws Exception;

}
