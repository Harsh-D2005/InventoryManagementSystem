package com.inventory.utils;

import java.util.regex.Pattern;
import com.inventory.utils.AlertUtil;

public class ValidationHelper {
    public static boolean validateSignUpInputs(String name, String email, String password, String confirmPassword, String ageText) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || ageText.isEmpty()) {
            AlertUtil.showError("All fields are required!");
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!Pattern.matches(emailRegex, email)) {
            AlertUtil.showError("Invalid email format!");
            return false;
        }
        if (password.length() < 6) {
            AlertUtil.showError("Password must be at least 6 characters long!");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            AlertUtil.showError("Passwords do not match!");
            return false;
        }
        try {
            int age = Integer.parseInt(ageText);
            if (age < 1 || age > 100) {
                AlertUtil.showError("Age must be between 1 and 100.");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showError("Age must be a valid number!");
            return false;
        }
        return true;
    }
}
