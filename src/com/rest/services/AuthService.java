package com.rest.services;

import com.rest.models.User;
import com.rest.models.UserRole;
import com.rest.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String password, String fullName, UserRole role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role must be specified");
        }

        String uname = username.trim();
        if (userRepository.findByUsername(uname) != null) {
            throw new IllegalArgumentException("Username '" + uname + "' is already taken");
        }

        String id = userRepository.nextId();
        User user = new User(id, uname, password, fullName.trim(), role);
        userRepository.save(user);
        return user;
    }

    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        User user = userRepository.findByUsername(username.trim());
        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            return user;
        }
        return null;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
