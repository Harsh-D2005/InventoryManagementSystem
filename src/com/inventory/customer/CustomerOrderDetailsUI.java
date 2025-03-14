package com.inventory.customer;

import com.inventory.models.CartItem;
import com.inventory.UI.NavigationMenu;
import com.inventory.utils.AlertUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CustomerOrderDetailsUI {
    public static void showOrderDetails(Stage stage) {
        ObservableList<CartItem> cart = CustomerProductUI.cart;
        if(cart.isEmpty()){
            AlertUtil.showError("Your cart is empty!");
            CustomerProductUI.showProductListing(stage);
            return;
        }

        Label title = new Label("Order Details");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 10 0 20 0;");

        TableView<CartItem> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 14px;");
        table.setItems(cart);

        TableColumn<CartItem, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getName()));

        TableColumn<CartItem, Double> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getProduct().getPrice()).asObject());

        TableColumn<CartItem, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        TableColumn<CartItem, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData -> {
            double total = cellData.getValue().getProduct().getPrice() * cellData.getValue().getQuantity();
            return new SimpleDoubleProperty(total).asObject();
        });

        table.getColumns().addAll(nameCol, priceCol, quantityCol, totalCol);

        double overallTotal = cart.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
        Label totalLabel = new Label("Overall Total: ₹" + overallTotal);
        totalLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 10 0 20 0;");

        Button doneButton = new Button("Done");
        doneButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 5; -fx-padding: 8 16;");
        doneButton.setOnAction(e -> {
            cart.clear();
            CustomerProductUI.showProductListing(stage);
        });

        VBox content = new VBox(10, title, table, totalLabel, doneButton);
        content.setPadding(new Insets(10));
        VBox layout = new VBox(NavigationMenu.getNavigationBar("customer", stage), content);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0eafc, #cfdef3);");
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Order Details");
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setResizable(true);
        stage.show();
    }
}
