package com.rest.repository;

import com.rest.models.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for MenuItem entities.
 * Extends BaseRepository (inheritance) which implements Repository (interface).
 */
public class MenuRepository extends BaseRepository<MenuItem> {

    private int idCounter = 100;

    public MenuRepository(StorageManager storageManager) {
        seedDefaults();
    }

    private void seedDefaults() {
        // Bella Italia (REST-101)
        store.add(new MenuItem("ITEM-101", "REST-101", "Margherita Pizza",     12.50, "Main Course", true));
        store.add(new MenuItem("ITEM-102", "REST-101", "Spaghetti Carbonara",  14.00, "Main Course", true));
        store.add(new MenuItem("ITEM-103", "REST-101", "Tiramisu",              6.50, "Dessert",     true));
        store.add(new MenuItem("ITEM-104", "REST-101", "Garlic Bread",          4.50, "Appetizer",   true));
        store.add(new MenuItem("ITEM-105", "REST-101", "White Truffle Risotto",22.00, "Main Course", false));

        // Spice Route (REST-102)
        store.add(new MenuItem("ITEM-106", "REST-102", "Butter Chicken",  15.00, "Main Course", true));
        store.add(new MenuItem("ITEM-107", "REST-102", "Garlic Naan",      3.50, "Bread",       true));
        store.add(new MenuItem("ITEM-108", "REST-102", "Samosa (2 pcs)",   5.00, "Appetizer",   true));
        store.add(new MenuItem("ITEM-109", "REST-102", "Mango Lassi",      4.00, "Beverage",    true));

        // Midnight Diner (REST-103)
        store.add(new MenuItem("ITEM-110", "REST-103", "Midnight Cheeseburger", 11.00, "Main Course", true));
        store.add(new MenuItem("ITEM-111", "REST-103", "Loaded Fries",           6.00, "Sides",       true));
        store.add(new MenuItem("ITEM-112", "REST-103", "Vanilla Milkshake",      5.00, "Beverage",    true));

        idCounter = 112;
    }

    public String nextId() {
        idCounter++;
        return "ITEM-" + idCounter;
    }

    @Override
    public MenuItem findById(String id) {
        if (id == null) return null;
        for (MenuItem item : store) {
            if (item.getId().equalsIgnoreCase(id.trim())) {
                return item;
            }
        }
        return null;
    }

    public List<MenuItem> findByRestaurantId(String restaurantId) {
        List<MenuItem> result = new ArrayList<>();
        if (restaurantId == null) return result;
        for (MenuItem item : store) {
            if (item.getRestaurantId().equalsIgnoreCase(restaurantId.trim())) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public void save(MenuItem item) {
        if (item == null) return;
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getId().equalsIgnoreCase(item.getId())) {
                store.set(i, item);
                return;
            }
        }
        store.add(item);
    }

    public boolean deleteById(String id) {
        if (id == null) return false;
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getId().equalsIgnoreCase(id.trim())) {
                store.remove(i);
                return true;
            }
        }
        return false;
    }
}
