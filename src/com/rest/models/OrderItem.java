package com.rest.models;

public class OrderItem {
    private String menuItemId;
    private String itemName;
    private double unitPrice;
    private int quantity;
    private double subtotal;

    public OrderItem(String menuItemId, String itemName, double unitPrice, int quantity) {
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = unitPrice * quantity;
    }

    public OrderItem(String menuItemId, String itemName, double unitPrice, int quantity, double subtotal) {
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return itemName + " x " + quantity + " @ $" + String.format("%.2f", unitPrice) + " = $" + String.format("%.2f", subtotal);
    }
}
