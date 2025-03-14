package com.inventory.customer;

import com.inventory.UI.NavigationMenu;
import com.inventory.models.Order;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomerReceiptUI {
    public static void showReceipt(Stage stage, Order order, String billingAddress, String phone, String paymentMethod) {
        Label header = new Label("RECEIPT");
        header.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: black; -fx-alignment: center;");

        Label orderIdLabel = new Label("Order ID: " + order.getId());
        Label dateLabel = new Label("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        Label billingLabel = new Label("Billing Address: " + billingAddress);
        Label phoneLabel = new Label("Phone: " + phone);
        Label paymentLabel = new Label("Payment Method: " + paymentMethod);
        Label productListLabel = new Label("Products: " + order.getProductList());
        Label totalLabel = new Label("Total Amount: ₹" + order.getTotalPrice());

        String receiptStyle = "-fx-font-family: 'Courier New', monospace; -fx-font-size: 16px; -fx-text-fill: black;";
        orderIdLabel.setStyle(receiptStyle);
        dateLabel.setStyle(receiptStyle);
        billingLabel.setStyle(receiptStyle);
        phoneLabel.setStyle(receiptStyle);
        paymentLabel.setStyle(receiptStyle);
        productListLabel.setStyle(receiptStyle);
        totalLabel.setStyle(receiptStyle);

        VBox receiptBox = new VBox(10, header, orderIdLabel, dateLabel, billingLabel, phoneLabel, paymentLabel, productListLabel, totalLabel);
        receiptBox.setPadding(new Insets(20));
        receiptBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: white;");
        receiptBox.setAlignment(Pos.CENTER);

        Label thankYouLabel = new Label("Thank you for your purchase!");
        thankYouLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black; -fx-padding: 20 0 0 0;");

        VBox content = new VBox(10, receiptBox, thankYouLabel);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        VBox layout = new VBox(NavigationMenu.getNavigationBar("customer", stage), content);
        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
        stage.setTitle("Receipt");
        stage.setWidth(600);
        stage.setHeight(500);
        stage.setResizable(true);
        stage.show();
    }
}
