package com.rest.services;

import com.rest.models.Payment;
import com.rest.models.PaymentMode;
import com.rest.models.PaymentStatus;
import com.rest.repository.PaymentRepository;

import java.time.LocalDateTime;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(String orderId, String customerId, double amount,
                                  PaymentMode mode, String transactionRef) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        if (mode == null) {
            throw new IllegalArgumentException("Payment mode must be specified");
        }

        String paymentId = paymentRepository.nextId();
        PaymentStatus status;

        if (mode == PaymentMode.CASH_ON_DELIVERY) {
            status = PaymentStatus.PENDING;
            if (transactionRef == null || transactionRef.trim().isEmpty()) {
                transactionRef = "Cash on Delivery";
            }
        } else {
            status = PaymentStatus.PAID;
        }

        Payment payment = new Payment(
                paymentId,
                orderId,
                customerId,
                amount,
                mode,
                status,
                transactionRef,
                LocalDateTime.now()
        );

        paymentRepository.save(payment);
        return payment;
    }

    public Payment markPaymentCompleted(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment != null) {
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
        }
        return payment;
    }

    public Payment refundPayment(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment != null) {
            if (payment.getStatus() == PaymentStatus.PAID) {
                payment.setStatus(PaymentStatus.REFUNDED);
            } else if (payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.FAILED);
            }
            paymentRepository.save(payment);
        }
        return payment;
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId);
    }
}
