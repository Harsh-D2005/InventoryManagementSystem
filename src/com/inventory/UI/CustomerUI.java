package com.inventory.UI;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CustomerUI {
    public static void showCustomerPage(Stage stage) {
        Label label = new Label("Customer Page - View Products, Search & Cart");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Customer");
        stage.show();
    }
}
