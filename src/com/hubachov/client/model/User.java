package com.hubachov.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class User extends BaseModel implements Serializable {
    static final long serialVersionUID = 7816369586829971697L;
    private long id;
    private String login;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Date birthday;
    private Role role;
    private Set<Role> roles;

    public User() {

    }

    public User(long id, String login, String password, String email,
                String firstName, String lastName, Date birthday, Role role) {
        setId(id);
        setLogin(login);
        setPassword(password);
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setBirthday(birthday);
        setRole(role);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        set("id", id);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
        set("login", login);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        set("password", password);
    }

    public String getEmail() {
        return get("email");
    }

    public void setEmail(String email) {
        this.email = email;
        set("email", email);
    }

    public String getFirstName() {
        return get("firstName");
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        set("firstName", firstName);
    }

    public String getLastName() {
        return get("lastName");
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        set("lastName", this.lastName);
    }

    public Date getBirthday() {
        return get("birthday");
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
        set("birthday", birthday);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
        set("role", role.getName());
    }

    public Set<Role> getRoles() {
        return roles;
    }

    private String getRolesAsString() {
        StringBuffer buffer = new StringBuffer();
        for (Role role : roles) {
            buffer.append(role.getName());
            buffer.append(" ");
        }
        return buffer.toString().trim();
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
        set("roles", getRolesAsString());
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return this.id == other.getId();
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", login=" + login + ", password=" + password
                + ", email=" + email + ", firstName=" + firstName
                + ", lastName=" + lastName + ", birthday=" + birthday
                + "]";
    }

}
