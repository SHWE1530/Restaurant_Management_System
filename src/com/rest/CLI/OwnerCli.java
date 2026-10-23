package com.rest.CLI;

import com.rest.models.*;
import com.rest.services.MenuService;
import com.rest.services.OrderService;
import com.rest.services.RestaurantService;
import com.rest.util.ConsoleReader;
import com.rest.util.CurrencyUtil;
import com.rest.util.TimeUtil;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class OwnerCli {
    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final OrderService orderService;
    private final ConsoleReader reader;

    public OwnerCli(RestaurantService restaurantService, MenuService menuService,
                    OrderService orderService, ConsoleReader reader) {
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.orderService = orderService;
        this.reader = reader;
    }

    public void start(User owner) {
        int choice;
        do {
            System.out.println("\n==========================================");
            System.out.println("   RESTAURANT OWNER: " + owner.getFullName());
            System.out.println("==========================================");
            System.out.println("1. Add a New Restaurant");
            System.out.println("2. View My Restaurants");
            System.out.println("3. Update Restaurant Details (Hours/Status/Min Order)");
            System.out.println("4. Manage Restaurant Menu Items");
            System.out.println("5. Manage Orders & Update Status");
            System.out.println("6. View Sales Summary");
            System.out.println("7. Logout");
            System.out.println("------------------------------------------");

            choice = reader.readInt("Enter choice [1-7]: ", 1, 7);

            switch (choice) {
                case 1:
                    addRestaurant(owner);
                    break;
                case 2:
                    viewMyRestaurants(owner);
                    break;
                case 3:
                    updateRestaurantOperationalDetails(owner);
                    break;
                case 4:
                    manageMenu(owner);
                    break;
                case 5:
                    manageOrders(owner);
                    break;
                case 6:
                    viewSummary(owner);
                    break;
                case 7:
                    System.out.println("Logged out successfully.");
                    break;
            }
        } while (choice != 7);
    }

    private void addRestaurant(User owner) {
        System.out.println("\n--- Register New Restaurant ---");
        String name = reader.readString("Enter Restaurant Name: ");
        String cuisine = reader.readString("Enter Cuisine Type: ");
        String city = reader.readString("Enter City: ");
        LocalTime opening = reader.readLocalTime("Enter Opening Time");
        LocalTime closing = reader.readLocalTime("Enter Closing Time");
        double minOrder = reader.readDouble("Enter Minimum Order ($): ", 0, 1000);

        try {
            Restaurant r = restaurantService.addRestaurant(owner.getId(), name, cuisine, city, opening, closing, minOrder);
            System.out.println("\nRestaurant added successfully with ID: " + r.getId());
        } catch (Exception e) {
            System.out.println("Error adding restaurant: " + e.getMessage());
        }
    }

    private void viewMyRestaurants(User owner) {
        List<Restaurant> myRestaurants = restaurantService.getRestaurantsByOwner(owner.getId());
        if (myRestaurants.isEmpty()) {
            System.out.println("\nYou do not have any registered restaurants.");
            return;
        }

        System.out.println("\n--- My Restaurants ---");
        for (Restaurant r : myRestaurants) {
            System.out.println("ID: " + r.getId() + " | " + r.getName() + " (" + r.getCuisineType() + ", " + r.getCity() + ")"
                    + " | Hours: " + TimeUtil.formatHoursRange(r.getOpeningTime(), r.getClosingTime())
                    + " | Min Order: " + CurrencyUtil.format(r.getMinOrderValue())
                    + " | Status: " + (r.isOpen() ? "OPEN" : "CLOSED"));
        }
    }

    private void updateRestaurantOperationalDetails(User owner) {
        viewMyRestaurants(owner);
        String restId = reader.readString("Enter Restaurant ID to update (or '0' to cancel): ");
        if ("0".equals(restId)) return;

        Restaurant restaurant = restaurantService.getRestaurantById(restId);
        if (restaurant == null || !restaurant.getOwnerId().equals(owner.getId())) {
            System.out.println("Error: Restaurant not found or not owned by you.");
            return;
        }

        System.out.println("\nManaging: " + restaurant.getName());
        System.out.println("1. Update Operating Hours");
        System.out.println("2. Toggle Open/Closed Status (Currently: " + (restaurant.isOpen() ? "OPEN" : "CLOSED") + ")");
        System.out.println("3. Update Minimum Order Value (Currently: " + CurrencyUtil.format(restaurant.getMinOrderValue()) + ")");
        System.out.println("4. Cancel");

        int choice = reader.readInt("Enter choice [1-4]: ", 1, 4);
        try {
            switch (choice) {
                case 1:
                    LocalTime open = reader.readLocalTime("Enter new Opening Time");
                    LocalTime close = reader.readLocalTime("Enter new Closing Time");
                    restaurantService.updateOperatingHours(restaurant.getId(), owner.getId(), open, close);
                    System.out.println("Operating hours updated.");
                    break;
                case 2:
                    boolean newStatus = !restaurant.isOpen();
                    restaurantService.setOpenStatus(restaurant.getId(), owner.getId(), newStatus);
                    System.out.println("Status changed to: " + (newStatus ? "OPEN" : "CLOSED"));
                    break;
                case 3:
                    double newMin = reader.readDouble("Enter new Minimum Order ($): ", 0, 1000);
                    restaurantService.updateMinOrderValue(restaurant.getId(), owner.getId(), newMin);
                    System.out.println("Minimum order value updated.");
                    break;
                case 4:
                    break;
            }
        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    private void manageMenu(User owner) {
        viewMyRestaurants(owner);
        String restId = reader.readString("Enter Restaurant ID to manage menu (or '0' to cancel): ");
        if ("0".equals(restId)) return;

        Restaurant restaurant = restaurantService.getRestaurantById(restId);
        if (restaurant == null || !restaurant.getOwnerId().equals(owner.getId())) {
            System.out.println("Error: Restaurant not found.");
            return;
        }

        int choice;
        do {
            System.out.println("\n--- Menu for " + restaurant.getName() + " ---");
            List<MenuItem> menu = menuService.getMenuByRestaurant(restaurant.getId());
            if (menu.isEmpty()) {
                System.out.println("No menu items yet.");
            } else {
                for (MenuItem item : menu) {
                    System.out.println("ID: " + item.getId() + " | " + item.getName() + " | Price: "
                            + CurrencyUtil.format(item.getPrice()) + " | Category: " + item.getCategory()
                            + " | Available: " + (item.isAvailable() ? "YES" : "NO"));
                }
            }

            System.out.println("\n1. Add Menu Item");
            System.out.println("2. Edit Menu Item");
            System.out.println("3. Toggle Item Availability");
            System.out.println("4. Delete Menu Item");
            System.out.println("5. Back");

            choice = reader.readInt("Enter choice [1-5]: ", 1, 5);
            switch (choice) {
                case 1:
                    addMenuItem(owner, restaurant.getId());
                    break;
                case 2:
                    editMenuItem(owner, restaurant.getId());
                    break;
                case 3:
                    toggleAvailability(owner, restaurant.getId());
                    break;
                case 4:
                    deleteMenuItem(owner, restaurant.getId());
                    break;
                case 5:
                    break;
            }
        } while (choice != 5);
    }

    private void addMenuItem(User owner, String restaurantId) {
        System.out.println("\n-- Add Item --");
        String name = reader.readString("Enter Item Name: ");
        double price = reader.readDouble("Enter Price ($): ", 0.01, 1000);
        String category = reader.readString("Enter Category: ");
        boolean available = reader.readBoolean("Is item available?");

        try {
            MenuItem item = menuService.addMenuItem(restaurantId, owner.getId(), name, price, category, available);
            System.out.println("Added: " + item.getName() + " (ID: " + item.getId() + ")");
        } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    private void editMenuItem(User owner, String restaurantId) {
        String itemId = reader.readString("Enter Item ID to edit: ");
        MenuItem item = menuService.getMenuItemById(itemId);
        if (item == null || !item.getRestaurantId().equals(restaurantId)) {
            System.out.println("Item not found.");
            return;
        }

        String name = reader.readString("Enter new name: ");
        double price = reader.readDouble("Enter new price ($): ", 0.01, 1000);
        String category = reader.readString("Enter new category: ");
        boolean available = reader.readBoolean("Is item available?");

        try {
            menuService.updateMenuItem(item.getId(), owner.getId(), name, price, category, available);
            System.out.println("Menu item updated.");
        } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    private void toggleAvailability(User owner, String restaurantId) {
        String itemId = reader.readString("Enter Item ID to toggle: ");
        try {
            MenuItem item = menuService.toggleAvailability(itemId, owner.getId());
            System.out.println("Item '" + item.getName() + "' is now " + (item.isAvailable() ? "AVAILABLE" : "UNAVAILABLE"));
        } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    private void deleteMenuItem(User owner, String restaurantId) {
        String itemId = reader.readString("Enter Item ID to delete: ");
        if (reader.readBoolean("Are you sure you want to delete this item?")) {
            try {
                boolean deleted = menuService.deleteMenuItem(itemId, owner.getId());
                if (deleted) {
                    System.out.println("Item deleted.");
                } else {
                    System.out.println("Item not found.");
                }
            } catch (Exception e) {
                System.out.println("Delete failed: " + e.getMessage());
            }
        }
    }

    private void manageOrders(User owner) {
        List<Order> orders = orderService.getOrdersForOwner(owner.getId());
        if (orders.isEmpty()) {
            System.out.println("\nNo orders received yet.");
            return;
        }

        System.out.println("\n--- Orders Received ---");
        for (Order o : orders) {
            System.out.println("Order #" + o.getOrderId() + " | Customer: " + o.getCustomerName()
                    + " | Total: " + CurrencyUtil.format(o.getTotalAmount())
                    + " | Status: " + o.getStatus()
                    + " | Time: " + TimeUtil.formatDateTime(o.getOrderTimestamp()));
        }

        String orderId = reader.readString("Enter Order ID to update (or '0' to return): ");
        if ("0".equals(orderId)) return;

        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        System.out.println("\nCurrent Status: " + order.getStatus());
        System.out.println("Available Statuses: PLACED, CONFIRMED, PREPARING, READY, COMPLETED, CANCELLED");
        String nextStatusStr = reader.readString("Enter new status (or 'CANCEL' to abort): ").toUpperCase();
        if ("CANCEL".equalsIgnoreCase(nextStatusStr)) return;

        try {
            OrderStatus targetStatus = OrderStatus.valueOf(nextStatusStr);
            orderService.updateOrderStatus(order.getOrderId(), owner.getId(), targetStatus);
            System.out.println("Order " + order.getOrderId() + " updated to " + targetStatus);
        } catch (Exception e) {
            System.out.println("Failed to update status: " + e.getMessage());
        }
    }

    private void viewSummary(User owner) {
        viewMyRestaurants(owner);
        String restId = reader.readString("Enter Restaurant ID for summary (or '0' to cancel): ");
        if ("0".equals(restId)) return;

        try {
            OrderService.RestaurantSummary summary = orderService.getRestaurantSummary(restId, owner.getId());
            System.out.println("\n========================================");
            System.out.println("  SUMMARY: " + summary.getRestaurantName());
            System.out.println("========================================");
            System.out.println("Total Orders:    " + summary.getTotalOrders());
            System.out.println("Total Revenue:   " + CurrencyUtil.format(summary.getTotalRevenue()));
            System.out.println("\nOrders by Status:");
            for (Map.Entry<OrderStatus, Integer> entry : summary.getOrdersByStatus().entrySet()) {
                System.out.println("  - " + entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("\nPopular Items Sold:");
            if (summary.getPopularItems().isEmpty()) {
                System.out.println("  (None sold yet)");
            } else {
                for (Map.Entry<String, Integer> entry : summary.getPopularItems().entrySet()) {
                    System.out.println("  - " + entry.getKey() + ": " + entry.getValue() + " sold");
                }
            }
            System.out.println("========================================");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
