package com.hubachov.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;

import java.io.Serializable;

public class Role extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1328453834819427799L;
    private long id;
    private String name;

    public Role() {

    }

    public Role(long id, String name) {
        setId(id);
        setName(name);
    }

    public Role(String name) {
        this(0, name);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        set("id", id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        set("name", name);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Role other = (Role) obj;
        return this.id == other.getId() && this.name.equals(other.getName()) &&
                this.get("name").equals(other.get("name"));
    }

    @Override
    public String toString() {
        return "Role [id=" + id + ", name=" + name + "]";
    }

}
