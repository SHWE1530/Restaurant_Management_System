package com.rest.repository;

import com.rest.models.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Order entities.
 * Extends BaseRepository (inheritance) which implements Repository (interface).
 */
public class OrderRepository extends BaseRepository<Order> {

    private int idCounter = 100;

    public OrderRepository(StorageManager storageManager) {
    }

    public String nextId() {
        idCounter++;
        return "ORD-" + idCounter;
    }

    @Override
    public Order findById(String orderId) {
        if (orderId == null) return null;
        for (Order o : store) {
            if (o.getOrderId().equalsIgnoreCase(orderId.trim())) {
                return o;
            }
        }
        return null;
    }

    @Override
    public void save(Order order) {
        if (order == null) return;
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getOrderId().equalsIgnoreCase(order.getOrderId())) {
                store.set(i, order);
                return;
            }
        }
        store.add(order);
    }

    public List<Order> findByCustomerId(String customerId) {
        List<Order> result = new ArrayList<>();
        if (customerId == null) return result;
        for (Order o : store) {
            if (o.getCustomerId().equalsIgnoreCase(customerId.trim())) {
                result.add(o);
            }
        }
        return result;
    }

    public List<Order> findByRestaurantId(String restaurantId) {
        List<Order> result = new ArrayList<>();
        if (restaurantId == null) return result;
        for (Order o : store) {
            if (o.getRestaurantId().equalsIgnoreCase(restaurantId.trim())) {
                result.add(o);
            }
        }
        return result;
    }
}
