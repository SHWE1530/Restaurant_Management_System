package com.rest.CLI;

import com.rest.models.Table;
import com.rest.services.TableServices;
import java.util.List;
import java.util.Scanner;

public class TableCli {
    private final TableServices service;
    private final Scanner scan;

    public TableCli() {
        this(new TableServices(), new Scanner(System.in));
    }

    public TableCli(TableServices service) {
        this(service, new Scanner(System.in));
    }

    public TableCli(TableServices service, Scanner scanner) {
        this.service = service;
        this.scan = scanner;
    }

    public void start() {
        int choice = -1;
        do {
            System.out.println("\n==================================");
            System.out.println("     TABLE MANAGEMENT SYSTEM      ");
            System.out.println("==================================");
            System.out.println("1. Show all tables");
            System.out.println("2. Book a table");
            System.out.println("3. Free a table");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            if (!scan.hasNextLine()) {
                break;
            }
            String line = scan.nextLine().trim();
            try {
                choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    showAllTables();
                    break;
                case 2:
                    bookTable();
                    break;
                case 3:
                    freeTable();
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 0);
    }

    private void showAllTables() {
        List<Table> tables = service.getAll();
        System.out.println("\n+----------+----------+------------+");
        System.out.println("| Table ID | Capacity | Status     |");
        System.out.println("+----------+----------+------------+");
        for (Table t : tables) {
            System.out.printf("| %-8d | %-8d | %-10s |%n",
                    t.getId(), t.getCapacity(), t.getstatus().name().toUpperCase());
        }
        System.out.println("+----------+----------+------------+");
    }

    private void bookTable() {
        System.out.print("Enter Table ID to book: ");
        if (!scan.hasNextLine()) return;
        try {
            int id = Integer.parseInt(scan.nextLine().trim());
            boolean success = service.bookTable(id);
            if (success) {
                System.out.println("Table #" + id + " booked successfully!");
            } else {
                System.out.println("Failed to book Table #" + id + ". It might be already occupied or does not exist.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Table ID.");
        }
    }

    private void freeTable() {
        System.out.print("Enter Table ID to free: ");
        if (!scan.hasNextLine()) return;
        try {
            int id = Integer.parseInt(scan.nextLine().trim());
            boolean success = service.freeTable(id);
            if (success) {
                System.out.println("Table #" + id + " is now free!");
            } else {
                System.out.println("Failed to free Table #" + id + ". It might be already free or does not exist.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Table ID.");
        }
    }
}