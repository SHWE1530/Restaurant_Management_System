package com.rest.services;

import com.rest.models.MenuItem;
import com.rest.models.Restaurant;
import com.rest.repository.MenuRepository;
import com.rest.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;

public class MenuService {
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuService(MenuRepository menuRepository, RestaurantRepository restaurantRepository) {
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
    }

    private Restaurant verifyRestaurantOwnership(String restaurantId, String ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant does not exist");
        }
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("Unauthorized: You do not own this restaurant");
        }
        return restaurant;
    }

    public MenuItem addMenuItem(String restaurantId, String ownerId, String name,
                                double price, String category, boolean available) {
        verifyRestaurantOwnership(restaurantId, ownerId);
        if (price <= 0) {
            throw new IllegalArgumentException("Item price must be greater than zero");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }

        String id = menuRepository.nextId();
        MenuItem item = new MenuItem(id, restaurantId, name.trim(), price, category, available);
        menuRepository.save(item);
        return item;
    }

    public MenuItem updateMenuItem(String itemId, String ownerId, String name,
                                   double price, String category, boolean available) {
        MenuItem item = menuRepository.findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Menu item not found");
        }
        verifyRestaurantOwnership(item.getRestaurantId(), ownerId);

        if (price <= 0) {
            throw new IllegalArgumentException("Item price must be greater than zero");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }

        item.setName(name.trim());
        item.setPrice(price);
        item.setCategory(category);
        item.setAvailable(available);
        menuRepository.save(item);
        return item;
    }

    public MenuItem toggleAvailability(String itemId, String ownerId) {
        MenuItem item = menuRepository.findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Menu item not found");
        }
        verifyRestaurantOwnership(item.getRestaurantId(), ownerId);
        item.setAvailable(!item.isAvailable());
        menuRepository.save(item);
        return item;
    }

    public boolean deleteMenuItem(String itemId, String ownerId) {
        MenuItem item = menuRepository.findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Menu item not found");
        }
        verifyRestaurantOwnership(item.getRestaurantId(), ownerId);
        return menuRepository.deleteById(itemId);
    }

    public MenuItem getMenuItemById(String id) {
        return menuRepository.findById(id);
    }

    public List<MenuItem> getMenuByRestaurant(String restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }

    public List<MenuItem> getAvailableMenuByRestaurant(String restaurantId) {
        List<MenuItem> all = menuRepository.findByRestaurantId(restaurantId);
        List<MenuItem> available = new ArrayList<>();
        for (MenuItem item : all) {
            if (item.isAvailable()) {
                available.add(item);
            }
        }
        return available;
    }
}
