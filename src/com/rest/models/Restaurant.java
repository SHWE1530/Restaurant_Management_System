package com.rest.models;

import java.time.LocalTime;

public class Restaurant {
    private String id;
    private String ownerId;
    private String name;
    private String cuisineType;
    private String city;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private double minOrderValue;
    private boolean open;

    public Restaurant(String id, String ownerId, String name, String cuisineType, String city,
                      LocalTime openingTime, LocalTime closingTime, double minOrderValue, boolean open) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.cuisineType = cuisineType;
        this.city = city;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.minOrderValue = minOrderValue;
        this.open = open;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public double getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(double minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isWithinOperatingHours(LocalTime time) {
        if (time == null || openingTime == null || closingTime == null) {
            return false;
        }
        if (openingTime.equals(closingTime)) {
            return true;
        }
        if (closingTime.isAfter(openingTime)) {
            return !time.isBefore(openingTime) && time.isBefore(closingTime);
        } else {
            return !time.isBefore(openingTime) || time.isBefore(closingTime);
        }
    }

    public boolean isAcceptingOrders(LocalTime currentTime) {
        return this.open && isWithinOperatingHours(currentTime);
    }

    public boolean isAcceptingOrdersNow() {
        return isAcceptingOrders(LocalTime.now());
    }

    @Override
    public String toString() {
        return name + " (" + cuisineType + ", " + city + ") - Min Order: $" + String.format("%.2f", minOrderValue);
    }
}
