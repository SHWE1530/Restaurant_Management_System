package com.rest.repository;

import java.util.List;

/**
 * Generic repository interface defining the basic CRUD contract.
 * All repository classes must implement this interface.
 * Demonstrates the OOP concept of Interface.
 *
 * @param <T> the type of entity this repository manages
 */
public interface Repository<T> {

    /**
     * Save or update an entity in the store.
     */
    void save(T entity);

    /**
     * Find an entity by its string ID.
     * Returns null if not found.
     */
    T findById(String id);

    /**
     * Return all entities in the store.
     */
    List<T> getAll();
}
