package com.rest.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
    private String restaurantId;
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public Cart() {
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : items.values()) {
            count += item.getQuantity();
        }
        return count;
    }

    public Collection<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public CartItem getItem(String menuItemId) {
        return items.get(menuItemId);
    }

    public void addItem(MenuItem item, int quantity) {
        if (item == null || quantity <= 0) {
            return;
        }

        if (items.isEmpty()) {
            this.restaurantId = item.getRestaurantId();
        } else if (!this.restaurantId.equals(item.getRestaurantId())) {
            throw new IllegalStateException("Cart contains items from another restaurant. Please clear cart first.");
        }

        if (items.containsKey(item.getId())) {
            CartItem existing = items.get(item.getId());
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            items.put(item.getId(), new CartItem(item.getId(), item.getName(), item.getPrice(), quantity));
        }
    }

    public void updateQuantity(String menuItemId, int quantity) {
        if (!items.containsKey(menuItemId)) {
            return;
        }
        if (quantity <= 0) {
            removeItem(menuItemId);
        } else {
            items.get(menuItemId).setQuantity(quantity);
        }
    }

    public void removeItem(String menuItemId) {
        items.remove(menuItemId);
        if (items.isEmpty()) {
            this.restaurantId = null;
        }
    }

    public void clear() {
        items.clear();
        this.restaurantId = null;
    }

    public double getSubtotal() {
        double sum = 0.0;
        for (CartItem item : items.values()) {
            sum += item.getSubtotal();
        }
        return sum;
    }
}
