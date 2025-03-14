package com.inventory.UI;

import com.inventory.Session;
import com.inventory.admin.AdminDashboardUI;
import com.inventory.customer.CustomerProductUI;
import com.inventory.models.User;
import com.inventory.models.UserDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginUI {
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String ADMIN_PASSWORD = "admin123";

    public static void showLoginPage(Stage stage) {
        // Create a main BorderPane with a gradient background.
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f2f2f2, #d9d9d9);");

        // Create a card-like container for the login form.
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);");

        // Welcome message
        Label welcomeLabel = new Label("Welcome to Inventory Management");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Title of the login form
        Label titleLabel = new Label("Sign In");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1a73e8;");

        // Email field with label
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        // Password field with label
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        // Error message label
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 16px; " +
                "-fx-background-radius: 5; -fx-padding: 8 16;");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        // Sign-up hyperlink
        Hyperlink signUpLink = new Hyperlink("Don't have an account? Sign Up");
        signUpLink.setStyle("-fx-font-size: 14px;");
        signUpLink.setOnAction(e -> SignUpUI.showSignUpPage(stage));

        // Assemble the form
        VBox form = new VBox(10, titleLabel, emailLabel, emailField, passwordLabel, passwordField, loginButton, signUpLink, messageLabel);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setFillWidth(true);

        card.getChildren().addAll(welcomeLabel, form);
        root.setCenter(card);
        BorderPane.setMargin(card, new Insets(50));

        Scene scene = new Scene(root, 450, 500);
        stage.setScene(scene);
        stage.setTitle("Login");

        // Set initial window size but allow resizing
        stage.setWidth(600);
        stage.setHeight(600);
        stage.setResizable(true);

        stage.show();

        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            if(email.isEmpty() || password.isEmpty()){
                messageLabel.setText("Email and password must not be empty.");
                return;
            }
            if(email.equalsIgnoreCase(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                messageLabel.setText("");
                AdminDashboardUI.showDashboard(stage);
                return;
            }
            User user = UserDAO.getUserByEmail(email);
            if(user == null) {
                messageLabel.setText("User not found.");
                return;
            }
            if(user.getStatus() != null && user.getStatus().equalsIgnoreCase("Disabled")) {
                messageLabel.setText("Your account is disabled. Please contact support.");
                return;
            }
            if(!password.equals(user.getPassword())) {
                messageLabel.setText("Invalid password.");
                return;
            }
            messageLabel.setText("");
            if(user.getRole().equalsIgnoreCase("customer")){
                Session.loggedInCustomerId = user.getId();
                CustomerProductUI.showProductListing(stage);
            } else {
                messageLabel.setText("Access denied.");
            }
        });
    }
}
