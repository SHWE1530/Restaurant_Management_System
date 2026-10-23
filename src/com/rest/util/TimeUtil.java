package com.rest.util;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class TimeUtil {
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private TimeUtil() {
    }

    public static String formatTime(LocalTime time) {
        if (time == null) return "N/A";
        return time.format(TIME_FORMATTER);
    }

    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Time string cannot be empty");
        }
        return LocalTime.parse(timeStr.trim(), TIME_FORMATTER);
    }

    public static boolean isValidTime(String timeStr) {
        try {
            parseTime(timeStr);
            return true;
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return false;
        }
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("DateTime string cannot be empty");
        }
        return LocalDateTime.parse(dateTimeStr.trim(), DATE_TIME_FORMATTER);
    }

    /**
     * Determines whether testTime falls between openingTime and closingTime.
     * Correctly handles shifts spanning across midnight (e.g., 20:00 to 02:00).
     */
    public static boolean isWithinOperatingHours(LocalTime openingTime, LocalTime closingTime, LocalTime testTime) {
        if (openingTime == null || closingTime == null || testTime == null) {
            return false;
        }
        if (openingTime.equals(closingTime)) {
            // Open 24 hours
            return true;
        }
        if (closingTime.isAfter(openingTime)) {
            // Normal same-day shift (e.g. 09:00 to 22:00)
            return !testTime.isBefore(openingTime) && testTime.isBefore(closingTime);
        } else {
            // Spans midnight (e.g. 21:00 to 03:00)
            return !testTime.isBefore(openingTime) || testTime.isBefore(closingTime);
        }
    }

    public static String formatHoursRange(LocalTime openingTime, LocalTime closingTime) {
        if (openingTime == null || closingTime == null) return "Closed";
        if (openingTime.equals(closingTime)) return "24 Hours";
        boolean crossesMidnight = closingTime.isBefore(openingTime);
        return formatTime(openingTime) + " - " + formatTime(closingTime) + (crossesMidnight ? " (+1 day)" : "");
    }
}
