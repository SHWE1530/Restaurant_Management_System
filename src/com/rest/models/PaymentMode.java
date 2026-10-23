package com.rest.models;

public enum PaymentMode {
    CASH_ON_DELIVERY("Cash on Delivery (COD)"),
    CREDIT_CARD("Credit / Debit Card"),
    UPI("UPI");

    private final String displayName;

    PaymentMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
