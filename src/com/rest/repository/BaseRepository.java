package com.rest.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all repositories.
 * Implements the Repository interface and provides the shared in-memory
 * data store and the concrete getAll() method, eliminating code duplication
 * across all repository subclasses.
 *
 * Demonstrates:
 *   - Abstract Class (OOP concept)
 *   - Inheritance (subclasses extend this)
 *   - Interface implementation (implements Repository<T>)
 *   - Generics
 *
 * @param <T> the type of entity this repository manages
 */
public abstract class BaseRepository<T> implements Repository<T> {

    /**
     * The shared in-memory data store.
     * Protected so subclasses can access it directly for seeding and queries.
     */
    protected final List<T> store = new ArrayList<>();

    /**
     * Save or update an entity — must be implemented by each subclass
     * because only the subclass knows how to compare entity IDs.
     */
    @Override
    public abstract void save(T entity);

    /**
     * Find entity by String ID — must be implemented by each subclass
     * because each entity type uses a different ID field.
     */
    @Override
    public abstract T findById(String id);

    /**
     * Returns a defensive copy of all entities.
     * Shared implementation — no need to duplicate in every subclass.
     */
    @Override
    public List<T> getAll() {
        return new ArrayList<>(store);
    }

    /**
     * Returns the number of stored entities.
     */
    protected int count() {
        return store.size();
    }

    /**
     * Removes all entities matching the given predicate.
     */
    protected boolean removeIf(java.util.function.Predicate<T> predicate) {
        return store.removeIf(predicate);
    }
}
