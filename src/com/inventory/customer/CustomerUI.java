package com.inventory.customer;

import com.inventory.UI.NavigationMenu;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CustomerUI {
    public static void showCustomerPage(Stage stage) {
        Label label = new Label("Customer Dashboard");
        label.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 10 0 20 0;");
        StackPane root = new StackPane(label);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0eafc, #cfdef3);");
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Customer Dashboard");
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setResizable(true);
        stage.show();
    }
}
