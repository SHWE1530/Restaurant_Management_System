package com.rest.util;

public class CurrencyUtil {
    public static final double TAX_RATE = 0.08; // 8% tax
    public static final double STANDARD_DELIVERY_FEE = 3.99;
    public static final double FREE_DELIVERY_THRESHOLD = 30.00;

    public static String format(double amount) {
        return String.format("$%.2f", amount);
    }

    public static double calculateTax(double subtotal) {
        return Math.round(subtotal * TAX_RATE * 100.0) / 100.0;
    }

    public static double calculateDeliveryFee(double subtotal) {
        if (subtotal >= FREE_DELIVERY_THRESHOLD || subtotal <= 0) {
            return 0.0;
        }
        return STANDARD_DELIVERY_FEE;
    }

    public static double calculateTotal(double subtotal, double tax, double deliveryFee) {
        return Math.round((subtotal + tax + deliveryFee) * 100.0) / 100.0;
    }
}
