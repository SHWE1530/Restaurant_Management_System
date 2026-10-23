package com.rest.models;

public class MenuItem {
    private String id;
    private String restaurantId;
    private String name;
    private double price;
    private String category;
    private boolean available;

    public MenuItem(String id, String restaurantId, String name, double price, String category, boolean available) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
        this.category = (category == null || category.trim().isEmpty()) ? "General" : category;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return name + " - $" + String.format("%.2f", price) + " (" + category + ")";
    }
}
