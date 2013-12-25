package com.hubachov.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;

import java.io.Serializable;
import java.util.Date;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((birthday == null) ? 0 : birthday.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result
                + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result
                + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        result = prime * result
                + ((password == null) ? 0 : password.hashCode());
        result = (int) (prime * result + role.getId());
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
                + ", role=" + role + "]";
    }

}
