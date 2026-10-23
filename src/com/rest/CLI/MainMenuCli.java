package com.rest.CLI;

import com.rest.models.User;
import com.rest.models.UserRole;
import com.rest.services.*;
import com.rest.util.ConsoleReader;

public class MainMenuCli {
    private final AuthService authService;
    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final CartService cartService;
    private final OrderService orderService;
    private final TableServices tableServices;
    private final ConsoleReader reader;

    public MainMenuCli(AuthService authService,
                       RestaurantService restaurantService,
                       MenuService menuService,
                       CartService cartService,
                       OrderService orderService,
                       TableServices tableServices,
                       ConsoleReader reader) {
        this.authService = authService;
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.tableServices = tableServices;
        this.reader = reader;
    }

    public void run() {
        int choice;
        do {
            System.out.println("\n==============================================");
            System.out.println("        RESTAURANT MANAGEMENT SYSTEM          ");
            System.out.println("==============================================");
            System.out.println("1. Customer Portal");
            System.out.println("2. Restaurant Owner Portal");
            System.out.println("3. Table Management");
            System.out.println("4. Exit");
            System.out.println("==============================================");

            choice = reader.readInt("Select an option [1-4]: ", 1, 4);

            switch (choice) {
                case 1:
                    handleCustomerPortal();
                    break;
                case 2:
                    handleOwnerPortal();
                    break;
                case 3:
                    new TableCli(tableServices).start();
                    break;
                case 4:
                    System.out.println("\nThank you for using the system. Goodbye!");
                    break;
            }
        } while (choice != 4);
    }

    private void handleCustomerPortal() {
        while (true) {
            System.out.println("\n--- CUSTOMER PORTAL ---");
            System.out.println("1. Login (demo: customer1 / password123)");
            System.out.println("2. Register New Customer");
            System.out.println("3. Back to Main Menu");

            int option = reader.readInt("Enter option [1-3]: ", 1, 3);
            if (option == 1) {
                String username = reader.readString("Enter Username: ");
                String password = reader.readString("Enter Password: ");
                User user = authService.login(username, password);
                if (user != null) {
                    CustomerCli customerCli = new CustomerCli(restaurantService, menuService, cartService, orderService, reader);
                    customerCli.start(user);
                    break;
                } else {
                    System.out.println("Error: Invalid username or password.");
                }
            } else if (option == 2) {
                String username = reader.readString("Choose Username: ");
                String password = reader.readString("Choose Password (min 4 chars): ");
                String fullName = reader.readString("Enter Your Full Name: ");
                try {
                    User created = authService.register(username, password, fullName, UserRole.CUSTOMER);
                    System.out.println("Registration successful! Welcome, " + created.getFullName() + ". You may now log in.");
                } catch (Exception e) {
                    System.out.println("Registration failed: " + e.getMessage());
                }
            } else {
                break;
            }
        }
    }

    private void handleOwnerPortal() {
        while (true) {
            System.out.println("\n--- RESTAURANT OWNER PORTAL ---");
            System.out.println("1. Login (demo: owner1 / password123)");
            System.out.println("2. Register New Owner");
            System.out.println("3. Back to Main Menu");

            int option = reader.readInt("Enter option [1-3]: ", 1, 3);
            if (option == 1) {
                String username = reader.readString("Enter Username: ");
                String password = reader.readString("Enter Password: ");
                User user = authService.login(username, password);
                if (user != null) {
                    if (!user.isRestaurantOwner()) {
                        System.out.println("Access Denied: This account is not a Restaurant Owner.");
                        continue;
                    }
                    OwnerCli ownerCli = new OwnerCli(restaurantService, menuService, orderService, reader);
                    ownerCli.start(user);
                    break;
                } else {
                    System.out.println("Error: Invalid username or password.");
                }
            } else if (option == 2) {
                String username = reader.readString("Choose Username: ");
                String password = reader.readString("Choose Password (min 4 chars): ");
                String fullName = reader.readString("Enter Your Full Name: ");
                try {
                    User created = authService.register(username, password, fullName, UserRole.RESTAURANT_OWNER);
                    System.out.println("Owner registration successful! Welcome, " + created.getFullName() + ". You may now log in.");
                } catch (Exception e) {
                    System.out.println("Registration failed: " + e.getMessage());
                }
            } else {
                break;
            }
        }
    }
}
