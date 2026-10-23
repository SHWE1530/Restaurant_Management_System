package com.rest.util;

public class SecurityUtil {
    // For student project: simple direct password comparison
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) {
            return false;
        }
        return inputPassword.equals(storedPassword);
    }
}
