package com.rest.models;

public class User {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private UserRole role;

    public User(String id, String username, String password, String fullName, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isCustomer() {
        return this.role == UserRole.CUSTOMER;
    }

    public boolean isRestaurantOwner() {
        return this.role == UserRole.RESTAURANT_OWNER;
    }

    @Override
    public String toString() {
        return fullName + " (" + username + " - " + role + ")";
    }
}
