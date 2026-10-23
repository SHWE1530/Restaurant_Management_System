package com.rest.services;

import com.rest.models.Payment;
import com.rest.models.PaymentMode;

/**
 * Interface defining the contract for payment operations.
 * Any class that handles payments must implement this interface.
 * Demonstrates the OOP concept of Interface / Abstraction.
 */
public interface Payable {

    /**
     * Process a payment for a given order.
     */
    Payment processPayment(String orderId, String customerId, double amount,
                           PaymentMode mode, String transactionRef);

    /**
     * Refund or cancel a payment for a given order.
     */
    Payment refundPayment(String orderId);

    /**
     * Retrieve the payment record for a given order.
     */
    Payment getPaymentByOrderId(String orderId);
}
