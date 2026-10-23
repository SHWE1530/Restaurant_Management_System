package com.rest.services;

import com.rest.models.Table;
import com.rest.repository.TableRepository;
import java.util.List;

public class TableServices {
    private final TableRepository repository;

    public TableServices() {
        this.repository = new TableRepository();
    }

    public TableServices(TableRepository repository) {
        this.repository = repository;
    }

    public List<Table> getAll() {
        return repository.getAll();
    }

    public boolean bookTable(int id) {
        return repository.bookTable(id);
    }

    public boolean freeTable(int id) {
        return repository.freeTable(id);
    }
}
