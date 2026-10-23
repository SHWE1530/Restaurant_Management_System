package com.rest.services;

import com.rest.models.Cart;
import com.rest.models.MenuItem;
import com.rest.util.CurrencyUtil;

import java.util.HashMap;
import java.util.Map;

public class CartService {
    private final Map<String, Cart> userCarts = new HashMap<>();

    public CartService() {
    }

    public Cart getCart(String customerId) {
        if (!userCarts.containsKey(customerId)) {
            userCarts.put(customerId, new Cart());
        }
        return userCarts.get(customerId);
    }

    public void addItem(String customerId, MenuItem item, int quantity) {
        Cart cart = getCart(customerId);
        cart.addItem(item, quantity);
    }

    public void updateQuantity(String customerId, String menuItemId, int quantity) {
        Cart cart = getCart(customerId);
        cart.updateQuantity(menuItemId, quantity);
    }

    public void removeItem(String customerId, String menuItemId) {
        Cart cart = getCart(customerId);
        cart.removeItem(menuItemId);
    }

    public void clearCart(String customerId) {
        Cart cart = getCart(customerId);
        cart.clear();
    }

    public static class OrderTotals {
        private final double subtotal;
        private final double tax;
        private final double deliveryFee;
        private final double totalAmount;

        public OrderTotals(double subtotal, double tax, double deliveryFee, double totalAmount) {
            this.subtotal = subtotal;
            this.tax = tax;
            this.deliveryFee = deliveryFee;
            this.totalAmount = totalAmount;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public double getTax() {
            return tax;
        }

        public double getDeliveryFee() {
            return deliveryFee;
        }

        public double getTotalAmount() {
            return totalAmount;
        }
    }

    public OrderTotals calculateTotals(Cart cart) {
        if (cart == null || cart.isEmpty()) {
            return new OrderTotals(0.0, 0.0, 0.0, 0.0);
        }
        double subtotal = cart.getSubtotal();
        double tax = CurrencyUtil.calculateTax(subtotal);
        double deliveryFee = CurrencyUtil.calculateDeliveryFee(subtotal);
        double total = CurrencyUtil.calculateTotal(subtotal, tax, deliveryFee);
        return new OrderTotals(subtotal, tax, deliveryFee, total);
    }
}
