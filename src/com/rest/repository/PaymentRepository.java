package com.rest.repository;

import com.rest.models.Payment;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Payment entities.
 * Extends BaseRepository (inheritance) which implements Repository (interface).
 */
public class PaymentRepository extends BaseRepository<Payment> {

    private int idCounter = 100;

    public PaymentRepository(StorageManager storageManager) {
    }

    public String nextId() {
        idCounter++;
        return "PAY-" + idCounter;
    }

    @Override
    public Payment findById(String paymentId) {
        if (paymentId == null) return null;
        for (Payment p : store) {
            if (p.getPaymentId().equalsIgnoreCase(paymentId.trim())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void save(Payment payment) {
        if (payment == null) return;
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getPaymentId().equalsIgnoreCase(payment.getPaymentId())) {
                store.set(i, payment);
                return;
            }
        }
        store.add(payment);
    }

    public Payment findByOrderId(String orderId) {
        if (orderId == null) return null;
        for (Payment p : store) {
            if (p.getOrderId().equalsIgnoreCase(orderId.trim())) {
                return p;
            }
        }
        return null;
    }

    public List<Payment> findByCustomerId(String customerId) {
        List<Payment> result = new ArrayList<>();
        if (customerId == null) return result;
        for (Payment p : store) {
            if (p.getCustomerId().equalsIgnoreCase(customerId.trim())) {
                result.add(p);
            }
        }
        return result;
    }
}
