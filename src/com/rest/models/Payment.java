package com.rest.models;

import java.time.LocalDateTime;

public class Payment {
    private String paymentId;
    private String orderId;
    private String customerId;
    private double amount;
    private PaymentMode mode;
    private PaymentStatus status;
    private String transactionRef;
    private LocalDateTime timestamp;

    public Payment(String paymentId, String orderId, String customerId, double amount,
                   PaymentMode mode, PaymentStatus status, String transactionRef, LocalDateTime timestamp) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.mode = mode;
        this.status = status;
        this.transactionRef = transactionRef;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMode getMode() {
        return mode;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Payment #" + paymentId + " [Order: " + orderId + ", Mode: " + mode
                + ", Amount: $" + String.format("%.2f", amount) + ", Status: " + status + "]";
    }
}
