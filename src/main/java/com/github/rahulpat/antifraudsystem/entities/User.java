package com.github.rahulpat.antifraudsystem.entities;

import javax.persistence.*;

@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;
    @Column
    private String name;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private Role role;
    @Column
    private boolean isUserNotLocked;
    @Transient
    private Operation operation;

    public User() {};

    public User(long id, String name, String username, String password, Role role, boolean isUserNotLocked) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isUserNotLocked = isUserNotLocked;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isUserNotLocked() {
        return isUserNotLocked;
    }

    public void setUserNotLocked(boolean userNotLocked) {
        isUserNotLocked = userNotLocked;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
