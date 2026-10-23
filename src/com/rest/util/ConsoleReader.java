package com.rest.util;

import java.time.LocalTime;
import java.util.Scanner;

public class ConsoleReader {
    private final Scanner scanner;

    public ConsoleReader() {
        this.scanner = new Scanner(System.in);
    }

    public ConsoleReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Error: Input cannot be empty. Please try again.");
        }
    }

    public String readOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Error: Please enter a number between " + min + " and " + max + ".");
            } catch (Exception e) {
                System.out.println("Error: Please enter a valid whole number.");
            }
        }
    }

    public double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return Math.round(value * 100.0) / 100.0;
                }
                System.out.println("Error: Please enter a value between " + min + " and " + max + ".");
            } catch (Exception e) {
                System.out.println("Error: Please enter a valid decimal number (e.g. 12.50).");
            }
        }
    }

    public LocalTime readLocalTime(String prompt) {
        while (true) {
            System.out.print(prompt + " (HH:mm, 24-hr): ");
            String line = scanner.nextLine().trim();
            if (TimeUtil.isValidTime(line)) {
                return TimeUtil.parseTime(line);
            }
            System.out.println("Error: Invalid time format. Please enter as HH:mm (e.g. 09:30 or 22:00).");
        }
    }

    public boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes")) {
                return true;
            }
            if (line.equals("n") || line.equals("no")) {
                return false;
            }
            System.out.println("Error: Please enter 'y' for yes or 'n' for no.");
        }
    }

    public void pause(String prompt) {
        System.out.print(prompt);
        scanner.nextLine();
    }
}
