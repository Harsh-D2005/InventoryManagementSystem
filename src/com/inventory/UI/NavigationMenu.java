package com.inventory.UI;

import com.inventory.admin.AdminDashboardUI;
import com.inventory.customer.CustomerProductUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class NavigationMenu {
    public static BorderPane getNavigationBar(String role, Stage stage) {
        BorderPane navBar = new BorderPane();
        navBar.setPadding(new Insets(15));

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button homeButton = new Button("Home");
        Button logoutButton = new Button("Logout");

        homeButton.setOnAction(e -> {
            if (role.equalsIgnoreCase("admin")) {
                AdminDashboardUI.showDashboard(stage);
            } else {
                CustomerProductUI.showProductListing(stage);
            }
        });
        // DO NOT clear the cart here.
        logoutButton.setOnAction(e -> {
            // Simply navigate to the login page without clearing the cart.
            LoginUI.showLoginPage(stage);
        });

        buttonBox.getChildren().addAll(homeButton, logoutButton);
        navBar.setRight(buttonBox);
        return navBar;
    }
}
