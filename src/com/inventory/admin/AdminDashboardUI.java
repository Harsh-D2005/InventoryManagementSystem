package com.inventory.admin;

import com.inventory.UI.NavigationMenu;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminDashboardUI {
    public static void showDashboard(Stage stage) {
        Label label = new Label("Admin Dashboard");
        label.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 10 0 20 0;");

        Button productButton = new Button("Manage Products");
        productButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 5; -fx-padding: 8 16;");
        productButton.setOnAction(e -> AdminProductUI.showProductManagement(stage));

        Button customerButton = new Button("Manage Customers");
        customerButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 5; -fx-padding: 8 16;");
        customerButton.setOnAction(e -> AdminCustomerUI.showCustomerManagement(stage));

        Button orderButton = new Button("View Orders");
        orderButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 5; -fx-padding: 8 16;");
        orderButton.setOnAction(e -> AdminOrderHistoryUI.showOrderHistory(stage));

        VBox vbox = new VBox(15, label, productButton, customerButton, orderButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 0);");

        VBox layout = new VBox(NavigationMenu.getNavigationBar("admin", stage), vbox);
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Admin Dashboard");
        stage.setWidth(500);
        stage.setHeight(500);
        stage.setResizable(true); // This is the default, but you can set it explicitly.

        stage.show();
    }
}
