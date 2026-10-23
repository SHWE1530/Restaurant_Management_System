package com.rest.CLI;

import com.rest.models.*;
import com.rest.services.CartService;
import com.rest.services.MenuService;
import com.rest.services.OrderService;
import com.rest.services.RestaurantService;
import com.rest.util.ConsoleReader;
import com.rest.util.CurrencyUtil;
import com.rest.util.TimeUtil;

import java.time.LocalTime;
import java.util.List;

public class CustomerCli {
    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final CartService cartService;
    private final OrderService orderService;
    private final ConsoleReader reader;

    public CustomerCli(RestaurantService restaurantService, MenuService menuService,
                       CartService cartService, OrderService orderService, ConsoleReader reader) {
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.reader = reader;
    }

    public void start(User customer) {
        int choice;
        do {
            System.out.println("\n==========================================");
            System.out.println("   CUSTOMER PORTAL: Welcome, " + customer.getFullName() + "!");
            System.out.println("==========================================");
            System.out.println("1. View Available Restaurants");
            System.out.println("2. Search Restaurants (Name, Cuisine, City)");
            System.out.println("3. View Restaurant Menu & Add to Cart");
            System.out.println("4. View & Manage Cart");
            System.out.println("5. Checkout & Place Order");
            System.out.println("6. View My Orders");
            System.out.println("7. Cancel an Order");
            System.out.println("8. Logout to Main Menu");
            System.out.println("------------------------------------------");

            choice = reader.readInt("Enter choice [1-8]: ", 1, 8);

            switch (choice) {
                case 1:
                    viewRestaurants(restaurantService.getAllRestaurants());
                    break;
                case 2:
                    searchRestaurants();
                    break;
                case 3:
                    browseMenuAndAddToCart(customer);
                    break;
                case 4:
                    manageCart(customer);
                    break;
                case 5:
                    checkoutOrder(customer);
                    break;
                case 6:
                    viewOrderHistory(customer);
                    break;
                case 7:
                    cancelOrder(customer);
                    break;
                case 8:
                    System.out.println("Logged out successfully.");
                    break;
            }
        } while (choice != 8);
    }

    private void viewRestaurants(List<Restaurant> restaurants) {
        if (restaurants.isEmpty()) {
            System.out.println("\nNo restaurants found.");
            return;
        }
        System.out.println("\n--- Available Restaurants ---");
        LocalTime now = LocalTime.now();
        for (Restaurant r : restaurants) {
            boolean openNow = r.isAcceptingOrders(now);
            String status = openNow ? "OPEN" : "CLOSED";
            System.out.println("ID: " + r.getId() + " | " + r.getName() + " (" + r.getCuisineType() + ", " + r.getCity() + ")"
                    + " | Hours: " + TimeUtil.formatHoursRange(r.getOpeningTime(), r.getClosingTime())
                    + " | Min Order: " + CurrencyUtil.format(r.getMinOrderValue())
                    + " | Status: " + status);
        }
    }

    private void searchRestaurants() {
        String query = reader.readString("Enter keyword (name, cuisine, or city): ");
        List<Restaurant> results = restaurantService.searchRestaurants(query);
        System.out.println("Found " + results.size() + " restaurant(s) for '" + query + "':");
        viewRestaurants(results);
    }

    private void browseMenuAndAddToCart(User customer) {
        viewRestaurants(restaurantService.getAllRestaurants());
        String restId = reader.readString("Enter Restaurant ID to view menu (or '0' to cancel): ");
        if ("0".equals(restId)) return;

        Restaurant restaurant = restaurantService.getRestaurantById(restId);
        if (restaurant == null) {
            System.out.println("Error: Restaurant not found.");
            return;
        }

        System.out.println("\n=== MENU: " + restaurant.getName().toUpperCase() + " ===");
        List<MenuItem> menu = menuService.getMenuByRestaurant(restaurant.getId());
        if (menu.isEmpty()) {
            System.out.println("No menu items listed.");
            return;
        }

        for (MenuItem item : menu) {
            System.out.println("ID: " + item.getId() + " | " + item.getName() + " | $" + String.format("%.2f", item.getPrice())
                    + " (" + item.getCategory() + ") - " + (item.isAvailable() ? "AVAILABLE" : "OUT OF STOCK"));
        }

        boolean addMore = true;
        while (addMore) {
            String itemId = reader.readString("\nEnter Item ID to add (or '0' to finish): ");
            if ("0".equals(itemId)) break;

            MenuItem item = menuService.getMenuItemById(itemId);
            if (item == null || !item.getRestaurantId().equals(restaurant.getId())) {
                System.out.println("Invalid Item ID.");
                continue;
            }
            if (!item.isAvailable()) {
                System.out.println("Sorry, '" + item.getName() + "' is out of stock.");
                continue;
            }

            int qty = reader.readInt("Enter quantity: ", 1, 99);
            try {
                cartService.addItem(customer.getId(), item, qty);
                System.out.println("Added " + qty + "x " + item.getName() + " to cart.");
            } catch (Exception e) {
                System.out.println("Warning: " + e.getMessage());
                if (reader.readBoolean("Clear cart and start new order from this restaurant?")) {
                    cartService.clearCart(customer.getId());
                    cartService.addItem(customer.getId(), item, qty);
                    System.out.println("Cart cleared and item added.");
                }
            }
            addMore = reader.readBoolean("Add another item?");
        }
    }

    private void manageCart(User customer) {
        Cart cart = cartService.getCart(customer.getId());
        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }

        displayCart(cart);

        System.out.println("\nCart Options:");
        System.out.println("1. Update item quantity");
        System.out.println("2. Remove an item");
        System.out.println("3. Clear cart");
        System.out.println("4. Back to Customer Menu");

        int choice = reader.readInt("Enter choice [1-4]: ", 1, 4);
        switch (choice) {
            case 1:
                String updateId = reader.readString("Enter Item ID to update: ");
                if (cart.getItem(updateId) == null) {
                    System.out.println("Item not in cart.");
                    return;
                }
                int newQty = reader.readInt("Enter new quantity (0 to remove): ", 0, 99);
                cartService.updateQuantity(customer.getId(), updateId, newQty);
                System.out.println("Cart updated.");
                break;
            case 2:
                String removeId = reader.readString("Enter Item ID to remove: ");
                cartService.removeItem(customer.getId(), removeId);
                System.out.println("Item removed from cart.");
                break;
            case 3:
                if (reader.readBoolean("Are you sure you want to clear your cart?")) {
                    cartService.clearCart(customer.getId());
                    System.out.println("Cart cleared.");
                }
                break;
            case 4:
                break;
        }
    }

    private void displayCart(Cart cart) {
        Restaurant restaurant = restaurantService.getRestaurantById(cart.getRestaurantId());
        String restName = restaurant != null ? restaurant.getName() : "Unknown";

        System.out.println("\n--- YOUR SHOPPING CART ---");
        System.out.println("Restaurant: " + restName);
        for (CartItem item : cart.getItems()) {
            System.out.println(item.getItemName() + " x " + item.getQuantity() + " = " + CurrencyUtil.format(item.getSubtotal()));
        }

        CartService.OrderTotals totals = cartService.calculateTotals(cart);
        System.out.println("Subtotal:     " + CurrencyUtil.format(totals.getSubtotal()));
        System.out.println("Tax (8%):     " + CurrencyUtil.format(totals.getTax()));
        System.out.println("Delivery Fee: " + CurrencyUtil.format(totals.getDeliveryFee()));
        System.out.println("TOTAL:        " + CurrencyUtil.format(totals.getTotalAmount()));
        System.out.println("--------------------------");
    }

    private void checkoutOrder(User customer) {
        Cart cart = cartService.getCart(customer.getId());
        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }

        displayCart(cart);

        Restaurant restaurant = restaurantService.getRestaurantById(cart.getRestaurantId());
        if (restaurant == null) {
            System.out.println("Error: Restaurant no longer exists.");
            return;
        }

        LocalTime now = LocalTime.now();
        if (!restaurant.isOpen()) {
            System.out.println("Error: Restaurant is currently closed.");
            return;
        }
        if (!restaurant.isWithinOperatingHours(now)) {
            System.out.println("Error: Restaurant is outside operating hours ("
                    + TimeUtil.formatHoursRange(restaurant.getOpeningTime(), restaurant.getClosingTime()) + ").");
            return;
        }
        if (cart.getSubtotal() < restaurant.getMinOrderValue()) {
            System.out.println("Error: Minimum order value is " + CurrencyUtil.format(restaurant.getMinOrderValue())
                    + ". Current subtotal is " + CurrencyUtil.format(cart.getSubtotal()) + ".");
            return;
        }

        CartService.OrderTotals totals = cartService.calculateTotals(cart);

        System.out.println("\n--- PAYMENT METHOD REQUIRED ---");
        System.out.println("1. Cash on Delivery (COD)");
        System.out.println("2. Credit / Debit Card");
        System.out.println("3. UPI (Google Pay, PhonePe, Paytm, etc.)");
        System.out.println("4. Cancel Checkout");

        int payChoice = reader.readInt("Select payment option [1-4]: ", 1, 4);
        if (payChoice == 4) {
            System.out.println("Checkout cancelled.");
            return;
        }

        PaymentMode paymentMode;
        String paymentDetails;

        if (payChoice == 1) {
            paymentMode = PaymentMode.CASH_ON_DELIVERY;
            paymentDetails = "Cash on Delivery";
            System.out.println("\nNotice: You will pay " + CurrencyUtil.format(totals.getTotalAmount()) + " in cash upon delivery.");
            if (!reader.readBoolean("Confirm and place COD order?")) {
                System.out.println("Order cancelled.");
                return;
            }
        } else if (payChoice == 2) {
            paymentMode = PaymentMode.CREDIT_CARD;
            paymentDetails = collectCardPayment(totals.getTotalAmount());
            if (paymentDetails == null) {
                System.out.println("Card payment cancelled.");
                return;
            }
        } else {
            paymentMode = PaymentMode.UPI;
            paymentDetails = collectUpiPayment(totals.getTotalAmount());
            if (paymentDetails == null) {
                System.out.println("UPI payment cancelled.");
                return;
            }
        }

        try {
            Order order = orderService.placeOrder(customer, cart, now, paymentMode, paymentDetails);
            System.out.println("\n==========================================");
            System.out.println("       ORDER PLACED SUCCESSFULLY!         ");
            System.out.println("==========================================");
            System.out.println("Order ID:       " + order.getOrderId());
            System.out.println("Restaurant:     " + order.getRestaurantName());
            System.out.println("Total Amount:   " + CurrencyUtil.format(order.getTotalAmount()));
            System.out.println("Payment Method: " + order.getPaymentMode());
            System.out.println("Payment Status: " + order.getPaymentStatus());
            if (order.getPaymentReference() != null) {
                System.out.println("Payment Ref:    " + order.getPaymentReference());
            }
            System.out.println("Order Status:   " + order.getStatus());
            System.out.println("Placed At:      " + TimeUtil.formatDateTime(order.getOrderTimestamp()));
            System.out.println("==========================================");
        } catch (Exception e) {
            System.out.println("Failed to place order: " + e.getMessage());
        }
    }

    private String collectCardPayment(double totalAmount) {
        System.out.println("\n--- Credit / Debit Card Payment (" + CurrencyUtil.format(totalAmount) + ") ---");
        String cardNumber = "";
        while (true) {
            String rawCard = reader.readString("Enter 16-digit Card Number (or '0' to cancel): ");
            if ("0".equals(rawCard)) return null;
            String cleanCard = rawCard.replaceAll("[\\s-]", "");
            if (cleanCard.matches("\\d{13,19}")) {
                cardNumber = cleanCard;
                break;
            }
            System.out.println("Invalid card number. Please enter a 13-19 digit card number.");
        }

        String cardHolder = reader.readString("Enter Cardholder Name: ");

        String expiry = "";
        while (true) {
            String rawExp = reader.readString("Enter Expiry Date (MM/YY): ");
            if (rawExp.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
                expiry = rawExp;
                break;
            }
            System.out.println("Invalid format. Please enter as MM/YY (e.g. 12/28).");
        }

        String cvv = "";
        while (true) {
            String rawCvv = reader.readString("Enter CVV (3-4 digits): ");
            if (rawCvv.matches("\\d{3,4}")) {
                cvv = rawCvv;
                break;
            }
            System.out.println("Invalid CVV. Please enter 3 or 4 digits.");
        }

        System.out.println("\nAuthorizing card payment of " + CurrencyUtil.format(totalAmount) + "...");
        System.out.println("Payment Approved! (Card: **** **** **** " + cardNumber.substring(cardNumber.length() - 4) + ")");
        return "Card ending in " + cardNumber.substring(cardNumber.length() - 4) + " (" + cardHolder + ")";
    }

    private String collectUpiPayment(double totalAmount) {
        System.out.println("\n--- UPI Payment (" + CurrencyUtil.format(totalAmount) + ") ---");
        String upiId = "";
        while (true) {
            String rawUpi = reader.readString("Enter UPI ID (e.g. name@okhdfc, or '0' to cancel): ");
            if ("0".equals(rawUpi)) return null;
            if (rawUpi.contains("@") && rawUpi.length() >= 5) {
                upiId = rawUpi.trim();
                break;
            }
            System.out.println("Invalid UPI ID. UPI ID must be in format username@bank (e.g., alex@okaxis).");
        }

        System.out.println("\nSending payment prompt of " + CurrencyUtil.format(totalAmount) + " to " + upiId + "...");
        System.out.println("UPI Payment Received & Verified!");
        return "UPI (" + upiId + ")";
    }

    private void viewOrderHistory(User customer) {
        List<Order> orders = orderService.getOrdersByCustomer(customer.getId());
        if (orders.isEmpty()) {
            System.out.println("\nYou have no past orders.");
            return;
        }

        System.out.println("\n--- Order History ---");
        for (Order o : orders) {
            System.out.println("Order #" + o.getOrderId() + " | " + o.getRestaurantName()
                    + " | Total: " + CurrencyUtil.format(o.getTotalAmount())
                    + " | Payment: " + o.getPaymentMode() + " [" + o.getPaymentStatus() + "]"
                    + " | Status: " + o.getStatus()
                    + " | Time: " + TimeUtil.formatDateTime(o.getOrderTimestamp()));
        }

        String orderId = reader.readOptionalString("Enter Order ID for invoice (or press Enter to return): ");
        if (!orderId.isEmpty()) {
            Order order = orderService.getOrderById(orderId);
            if (order != null) {
                displayInvoice(order);
            } else {
                System.out.println("Order not found.");
            }
        }
    }

    private void displayInvoice(Order order) {
        System.out.println("\n=================== INVOICE ===================");
        System.out.println("Order ID:       " + order.getOrderId());
        System.out.println("Restaurant:     " + order.getRestaurantName());
        System.out.println("Date/Time:      " + TimeUtil.formatDateTime(order.getOrderTimestamp()));
        System.out.println("Order Status:   " + order.getStatus());
        System.out.println("Payment Method: " + order.getPaymentMode());
        System.out.println("Payment Status: " + order.getPaymentStatus());
        if (order.getPaymentReference() != null) {
            System.out.println("Payment Ref:    " + order.getPaymentReference());
        }
        System.out.println("-----------------------------------------------");
        for (OrderItem item : order.getItems()) {
            System.out.println(" - " + item.getItemName() + " x " + item.getQuantity() + " = " + CurrencyUtil.format(item.getSubtotal()));
        }
        System.out.println("-----------------------------------------------");
        System.out.println("Subtotal:       " + CurrencyUtil.format(order.getSubtotal()));
        System.out.println("Tax:            " + CurrencyUtil.format(order.getTax()));
        System.out.println("Delivery Fee:   " + CurrencyUtil.format(order.getDeliveryFee()));
        System.out.println("TOTAL:          " + CurrencyUtil.format(order.getTotalAmount()));
        System.out.println("===============================================");
    }

    private void cancelOrder(User customer) {
        String orderId = reader.readString("Enter Order ID to cancel: ");
        try {
            Order order = orderService.cancelOrderByCustomer(orderId, customer.getId());
            System.out.println("Order #" + order.getOrderId() + " has been cancelled.");
        } catch (Exception e) {
            System.out.println("Cannot cancel order: " + e.getMessage());
        }
    }
}
