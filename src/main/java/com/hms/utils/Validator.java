package com.hms.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {

    public static boolean isValidName(String name) {
        // Name is valid if it does not contain numbers or special characters
        return name != null &&
                !name.trim().isEmpty() &&
                name.matches("^[a-zA-Z\\s]+$"); // Only letters and spaces allowed
    }

    public static boolean isValidUsername(String username) {
        return username != null &&
                !username.trim().isEmpty() &&
                !Character.isDigit(username.charAt(0)); // First character is not a digit
    }

    public static boolean isValidPassword(String password) {
        return password != null &&
                !password.trim().isEmpty() &&
                password.length() >= 8; // Added minimum length requirement
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Phone number is valid if it only contains digits
        return phoneNumber != null &&
                !phoneNumber.trim().isEmpty() &&
                phoneNumber.matches("^\\d+$"); // Only digits allowed
    }

    public static boolean isValidEmailAddress(String email) {
        // Basic email validation (contains @ and . with proper format)
        return email != null &&
                !email.trim().isEmpty() &&
                email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidDateOfBirth(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.trim().isEmpty()) {
            return false;
        }

        try {
            // Parse the string from DatePicker (default format is ISO_LOCAL_DATE: yyyy-MM-dd)
            LocalDate dob = LocalDate.parse(dateOfBirth);
            LocalDate today = LocalDate.now();

            // Date is valid if it's today or in the past
            return dob.isBefore(today) || dob.isEqual(today);
        } catch (DateTimeParseException e) {
            return false; // Invalid date format
        }
    }

    public static boolean isNullOrEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

}
