package com.rest.repository;

import com.rest.models.Restaurant;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Restaurant entities.
 * Extends BaseRepository (inheritance) which implements Repository (interface).
 */
public class RestaurantRepository extends BaseRepository<Restaurant> {

    private int idCounter = 100;

    public RestaurantRepository(StorageManager storageManager) {
        seedDefaults();
    }

    private void seedDefaults() {
        store.add(new Restaurant("REST-101", "USR-102", "Bella Italia", "Italian", "New York",
                LocalTime.of(11, 0), LocalTime.of(22, 0), 15.00, true));
        store.add(new Restaurant("REST-102", "USR-102", "Spice Route", "Indian", "Chicago",
                LocalTime.of(12, 0), LocalTime.of(23, 0), 20.00, true));
        store.add(new Restaurant("REST-103", "USR-102", "Midnight Diner", "American", "Los Angeles",
                LocalTime.of(20, 0), LocalTime.of(4, 0), 10.00, true));
        idCounter = 103;
    }

    public String nextId() {
        idCounter++;
        return "REST-" + idCounter;
    }

    @Override
    public Restaurant findById(String id) {
        if (id == null) return null;
        for (Restaurant r : store) {
            if (r.getId().equalsIgnoreCase(id.trim())) {
                return r;
            }
        }
        return null;
    }

    public List<Restaurant> findByOwnerId(String ownerId) {
        List<Restaurant> result = new ArrayList<>();
        if (ownerId == null) return result;
        for (Restaurant r : store) {
            if (r.getOwnerId().equalsIgnoreCase(ownerId.trim())) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Restaurant> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAll();
        }
        String q = query.trim().toLowerCase();
        List<Restaurant> result = new ArrayList<>();
        for (Restaurant r : store) {
            if (r.getName().toLowerCase().contains(q) ||
                r.getCuisineType().toLowerCase().contains(q) ||
                r.getCity().toLowerCase().contains(q)) {
                result.add(r);
            }
        }
        return result;
    }

    @Override
    public void save(Restaurant restaurant) {
        if (restaurant == null) return;
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getId().equalsIgnoreCase(restaurant.getId())) {
                store.set(i, restaurant);
                return;
            }
        }
        store.add(restaurant);
    }
}
