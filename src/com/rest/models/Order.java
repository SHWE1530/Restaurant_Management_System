package com.rest.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private String customerId;
    private String customerName;
    private String restaurantId;
    private String restaurantName;
    private List<OrderItem> items;
    private double subtotal;
    private double tax;
    private double deliveryFee;
    private double totalAmount;
    private LocalDateTime orderTimestamp;
    private OrderStatus status;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private String paymentId;
    private String paymentReference;

    public Order(String orderId, String customerId, String customerName,
                 String restaurantId, String restaurantName,
                 List<OrderItem> items, double subtotal, double tax,
                 double deliveryFee, double totalAmount,
                 LocalDateTime orderTimestamp, OrderStatus status) {
        this(orderId, customerId, customerName, restaurantId, restaurantName, items,
                subtotal, tax, deliveryFee, totalAmount, orderTimestamp, status,
                PaymentMode.CASH_ON_DELIVERY, PaymentStatus.PENDING, null, "Cash on Delivery");
    }

    public Order(String orderId, String customerId, String customerName,
                 String restaurantId, String restaurantName,
                 List<OrderItem> items, double subtotal, double tax,
                 double deliveryFee, double totalAmount,
                 LocalDateTime orderTimestamp, OrderStatus status,
                 PaymentMode paymentMode, PaymentStatus paymentStatus,
                 String paymentId, String paymentReference) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.items = items != null ? items : new ArrayList<>();
        this.subtotal = subtotal;
        this.tax = tax;
        this.deliveryFee = deliveryFee;
        this.totalAmount = totalAmount;
        this.orderTimestamp = orderTimestamp != null ? orderTimestamp : LocalDateTime.now();
        this.status = status != null ? status : OrderStatus.PLACED;
        this.paymentMode = paymentMode != null ? paymentMode : PaymentMode.CASH_ON_DELIVERY;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;
        this.paymentId = paymentId;
        this.paymentReference = paymentReference;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public List<OrderItem> getItems() {
        return items;
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

    public LocalDateTime getOrderTimestamp() {
        return orderTimestamp;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public void transitionTo(OrderStatus newStatus) {
        if (newStatus != null && this.status.canTransitionTo(newStatus)) {
            this.status = newStatus;
        } else {
            throw new IllegalStateException("Cannot change status from " + this.status + " to " + newStatus);
        }
    }

    @Override
    public String toString() {
        return "Order #" + orderId + " [" + restaurantName + "] - Total: $" + String.format("%.2f", totalAmount)
                + " (" + status + ") [Payment: " + paymentMode + " - " + paymentStatus + "]";
    }
}
