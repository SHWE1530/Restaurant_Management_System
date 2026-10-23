package com.rest.repository;

import com.rest.models.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Table entities.
 * Extends BaseRepository (inheritance) which implements Repository (interface).
 * Tables use integer IDs; findById(String) parses the string to int for compatibility.
 */
public class TableRepository extends BaseRepository<Table> {

    public TableRepository() {
        this(new StorageManager());
    }

    public TableRepository(StorageManager storageManager) {
        seedDefaults();
    }

    private void seedDefaults() {
        store.add(new Table(1, 5));
        store.add(new Table(2, 8));
        store.add(new Table(3, 5));
    }

    /**
     * Find a table by its integer ID.
     */
    public Table findById(int id) {
        for (Table t : store) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    /**
     * Implements the Repository contract — parses String to int for table lookup.
     */
    @Override
    public Table findById(String id) {
        if (id == null) return null;
        try {
            return findById(Integer.parseInt(id.trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void save(Table table) {
        if (table == null) return;
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getId() == table.getId()) {
                store.set(i, table);
                return;
            }
        }
        store.add(table);
    }

    public boolean bookTable(int id) {
        Table table = findById(id);
        if (table != null && table.getStatus() == Table.Availability.free) {
            table.booking();
            return true;
        }
        return false;
    }

    public boolean freeTable(int id) {
        Table table = findById(id);
        if (table != null && table.getStatus() == Table.Availability.occupy) {
            table.freeNow();
            return true;
        }
        return false;
    }
}