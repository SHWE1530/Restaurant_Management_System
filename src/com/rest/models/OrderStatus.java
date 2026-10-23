package com.rest.models;

public enum OrderStatus {
    PLACED,
    CONFIRMED,
    PREPARING,
    READY,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus next) {
        if (next == null) return false;
        if (this == next) return true;

        switch (this) {
            case PLACED:
                return next == CONFIRMED || next == CANCELLED;
            case CONFIRMED:
                return next == PREPARING || next == CANCELLED;
            case PREPARING:
                return next == READY || next == CANCELLED;
            case READY:
                return next == COMPLETED || next == CANCELLED;
            case COMPLETED:
            case CANCELLED:
            default:
                return false;
        }
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    public boolean isCancellableByCustomer() {
        return this == PLACED;
    }
}
