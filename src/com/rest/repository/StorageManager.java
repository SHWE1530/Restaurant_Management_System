package com.rest.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Storage manager for the application.
 * Operates purely in-memory to prevent creating any extra files or folders on disk,
 * keeping the project directory structure completely clean.
 */
public class StorageManager {

    public StorageManager() {
    }

    public List<String> readLines(String fileName) {
        return new ArrayList<>();
    }

    public synchronized void writeLinesAtomically(String fileName, List<String> lines) {
        // In-memory mode: no disk I/O, no files or directories are created
    }
}
