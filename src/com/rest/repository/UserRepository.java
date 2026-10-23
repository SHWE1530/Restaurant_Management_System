package com.rest.repository;

import com.rest.models.User;
import com.rest.models.UserRole;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for User entities.
 * Extends BaseRepository (inheritance) which implements Repository (interface).
 */
public class UserRepository extends BaseRepository<User> {

    private int idCounter = 100;

    public UserRepository(StorageManager storageManager) {
        seedDefaults();
    }

    private void seedDefaults() {
        store.add(new User("USR-101", "customer1", "password123", "John Customer", UserRole.CUSTOMER));
        store.add(new User("USR-102", "owner1", "password123", "Chef Mario", UserRole.RESTAURANT_OWNER));
        idCounter = 102;
    }

    public String nextId() {
        idCounter++;
        return "USR-" + idCounter;
    }

    @Override
    public User findById(String id) {
        if (id == null) return null;
        for (User u : store) {
            if (u.getId().equalsIgnoreCase(id.trim())) {
                return u;
            }
        }
        return null;
    }

    public User findByUsername(String username) {
        if (username == null) return null;
        for (User u : store) {
            if (u.getUsername().equalsIgnoreCase(username.trim())) {
                return u;
            }
        }
        return null;
    }

    @Override
    public void save(User user) {
        if (user == null) return;
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getId().equalsIgnoreCase(user.getId())) {
                store.set(i, user);
                return;
            }
        }
        store.add(user);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(store);
    }
}
