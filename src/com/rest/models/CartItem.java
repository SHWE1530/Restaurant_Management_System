package com.rest.models;

public class CartItem {
    private String menuItemId;
    private String itemName;
    private double unitPrice;
    private int quantity;

    public CartItem(String menuItemId, String itemName, double unitPrice, int quantity) {
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return itemName + " x " + quantity + " = $" + String.format("%.2f", getSubtotal());
    }
}
