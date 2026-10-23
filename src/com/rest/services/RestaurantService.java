package com.rest.services;

import com.rest.models.Restaurant;
import com.rest.repository.RestaurantRepository;

import java.time.LocalTime;
import java.util.List;

public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant addRestaurant(String ownerId, String name, String cuisineType, String city,
                                    LocalTime openingTime, LocalTime closingTime, double minOrderValue) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant name cannot be empty");
        }
        if (cuisineType == null || cuisineType.trim().isEmpty()) {
            throw new IllegalArgumentException("Cuisine type cannot be empty");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }
        if (minOrderValue < 0) {
            throw new IllegalArgumentException("Minimum order value cannot be negative");
        }

        String id = restaurantRepository.nextId();
        Restaurant restaurant = new Restaurant(id, ownerId, name.trim(), cuisineType.trim(), city.trim(),
                openingTime, closingTime, minOrderValue, true);
        restaurantRepository.save(restaurant);
        return restaurant;
    }

    public Restaurant getRestaurantById(String id) {
        return restaurantRepository.findById(id);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.getAll();
    }

    public List<Restaurant> getRestaurantsByOwner(String ownerId) {
        return restaurantRepository.findByOwnerId(ownerId);
    }

    public List<Restaurant> searchRestaurants(String query) {
        return restaurantRepository.search(query);
    }

    public Restaurant updateOperatingHours(String restaurantId, String ownerId,
                                           LocalTime openingTime, LocalTime closingTime) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant not found");
        }
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("Unauthorized: You do not own this restaurant");
        }
        restaurant.setOpeningTime(openingTime);
        restaurant.setClosingTime(closingTime);
        restaurantRepository.save(restaurant);
        return restaurant;
    }

    public Restaurant setOpenStatus(String restaurantId, String ownerId, boolean isOpen) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant not found");
        }
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("Unauthorized: You do not own this restaurant");
        }
        restaurant.setOpen(isOpen);
        restaurantRepository.save(restaurant);
        return restaurant;
    }

    public Restaurant updateMinOrderValue(String restaurantId, String ownerId, double minOrderValue) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant not found");
        }
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("Unauthorized: You do not own this restaurant");
        }
        restaurant.setMinOrderValue(minOrderValue);
        restaurantRepository.save(restaurant);
        return restaurant;
    }

    public boolean isRestaurantAcceptingOrders(Restaurant restaurant, LocalTime testTime) {
        if (restaurant == null) return false;
        return restaurant.isAcceptingOrders(testTime);
    }
}
