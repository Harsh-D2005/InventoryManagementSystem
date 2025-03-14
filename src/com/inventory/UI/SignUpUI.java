package com.inventory.UI;

import com.inventory.DatabaseConnection;
import com.inventory.utils.AlertUtil;
import com.inventory.utils.ValidationHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpUI {
    public static void showSignUpPage(Stage stage) {
        // Create a main container with gradient background.
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f2f2f2, #d9d9d9);");

        // Create a card-like container for the sign-up form.
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);");

        Label titleLabel = new Label("Create Your Account");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1a73e8;");

        Label welcomeLabel = new Label("Join our Inventory Management System");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333;");

        // Full Name
        Label nameLabel = new Label("Full Name:");
        nameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        // Email
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email address");
        emailField.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        // Password
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter a password");
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        // Confirm Password
        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter your password");
        confirmPasswordField.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        // Age
        Label ageLabel = new Label("Age:");
        ageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        TextField ageField = new TextField();
        ageField.setPromptText("Enter your age");
        ageField.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        // Gender
        Label genderLabel = new Label("Gender:");
        genderLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        genderComboBox.setValue("Male");
        genderComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        // Error message label
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        // Sign up button
        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 16px; " +
                "-fx-background-radius: 5; -fx-padding: 8 16;");
        signUpButton.setMaxWidth(Double.MAX_VALUE);

        // Back to login hyperlink
        Hyperlink backToLoginLink = new Hyperlink("Already have an account? Sign In");
        backToLoginLink.setStyle("-fx-font-size: 14px;");
        backToLoginLink.setOnAction(e -> LoginUI.showLoginPage(stage));

        // Assemble the form
        VBox form = new VBox(10,
                titleLabel,
                nameLabel, nameField,
                emailLabel, emailField,
                passwordLabel, passwordField,
                confirmPasswordLabel, confirmPasswordField,
                ageLabel, ageField,
                genderLabel, genderComboBox,
                signUpButton,
                backToLoginLink,
                messageLabel
        );
        form.setAlignment(Pos.CENTER_LEFT);
        form.setFillWidth(true);

        card.getChildren().addAll(welcomeLabel, form);
        root.setCenter(card);
        BorderPane.setMargin(card, new Insets(50));

        Scene scene = new Scene(root, 500, 700);
        stage.setScene(scene);
        stage.setTitle("Sign Up");

        // Set initial window size and allow resizing
        stage.setWidth(550);
        stage.setHeight(800);
        stage.setResizable(true);

        stage.show();

        signUpButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();
            String ageText = ageField.getText().trim();
            String gender = genderComboBox.getValue();
            String role = "customer"; // Only customer signups allowed

            if (ValidationHelper.validateSignUpInputs(name, email, password, confirmPassword, ageText)) {
                int age = Integer.parseInt(ageText);
                if (registerUser(name, email, password, age, gender, role)) {
                    AlertUtil.showInfo("Account created successfully!");
                    LoginUI.showLoginPage(stage);
                } else {
                    messageLabel.setText("Sign-up failed! Email may already exist.");
                }
            }
        });
    }

    private static boolean registerUser(String name, String email, String password, int age, String gender, String role) {
        try (Connection conn = com.inventory.DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?")) {
            checkStmt.setString(1, email);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        try (Connection conn = com.inventory.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (name, email, password, age, gender, role, status) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setInt(4, age);
            stmt.setString(5, gender);
            stmt.setString(6, role);
            stmt.setString(7, "Enabled");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
