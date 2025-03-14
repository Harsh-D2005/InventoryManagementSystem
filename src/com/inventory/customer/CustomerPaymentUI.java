package com.inventory.customer;

import com.inventory.UI.NavigationMenu;
import com.inventory.Session;
import com.inventory.models.Order;
import com.inventory.models.OrderDAO;
import com.inventory.models.CartItem;
import com.inventory.models.Product;
import com.inventory.models.ProductDAO;
import com.inventory.utils.AlertUtil;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Optional;

public class CustomerPaymentUI {
    public static void showPayment(Stage stage) {
        // Main layout: BorderPane with navigation bar at the top.
        BorderPane root = new BorderPane();
        root.setTop(NavigationMenu.getNavigationBar("customer", stage));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0eafc, #cfdef3);");

        // Purchase Summary section
        ObservableList<CartItem> cart = CustomerProductUI.cart;
        StringBuilder summaryText = new StringBuilder();
        double totalCost = 0.0;
        for (CartItem item : cart) {
            double itemTotal = item.getProduct().getPrice() * item.getQuantity();
            totalCost += itemTotal;
            summaryText.append(item.getProduct().getName())
                    .append(" - Qty: ").append(item.getQuantity())
                    .append(" - ₹").append(String.format("%.2f", itemTotal))
                    .append("\n");
        }
        summaryText.append("Overall Total: ₹").append(String.format("%.2f", totalCost));
        Label summaryTitle = new Label("Purchase Summary:");
        summaryTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Label summaryLabel = new Label(summaryText.toString());
        summaryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        VBox summaryBox = new VBox(5, summaryTitle, summaryLabel);
        summaryBox.setAlignment(Pos.CENTER_LEFT);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 5; -fx-border-color: #ccc; -fx-border-radius: 5;");

        // Payment Details section
        VBox paymentDetails = new VBox(10);
        paymentDetails.setAlignment(Pos.CENTER_LEFT);

        Label addressLabel = new Label("Billing Address:");
        addressLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        TextField addressField = new TextField();
        addressField.setPromptText("Enter your address");
        addressField.setStyle("-fx-font-size: 14px;");

        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter your 10-digit phone number");
        phoneField.setStyle("-fx-font-size: 14px;");

        Label paymentMethodLabel = new Label("Payment Method:");
        paymentMethodLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        ComboBox<String> paymentMethodBox = new ComboBox<>();
        paymentMethodBox.getItems().addAll("Credit Card", "Debit Card", "Net Banking", "UPI");
        paymentMethodBox.setValue("Credit Card");
        paymentMethodBox.setStyle("-fx-font-size: 14px;");

        // Card Details Section
        Label cardDetailsLabel = new Label("Card Details:");
        cardDetailsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");

        Label cardHolderLabel = new Label("Card Holder Name:");
        cardHolderLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        TextField cardHolderField = new TextField();
        cardHolderField.setPromptText("Enter card holder name");
        cardHolderField.setStyle("-fx-font-size: 14px;");

        Label cardNumberLabel = new Label("Card Number:");
        cardNumberLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("Enter 16-digit card number");
        cardNumberField.setStyle("-fx-font-size: 14px;");

        Label expiryLabel = new Label("Expiry Date (MM/YY):");
        expiryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        TextField expiryField = new TextField();
        expiryField.setPromptText("MM/YY");
        expiryField.setStyle("-fx-font-size: 14px;");

        Label cvvLabel = new Label("CVV:");
        cvvLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        PasswordField cvvField = new PasswordField();
        cvvField.setPromptText("3-digit CVV");
        cvvField.setStyle("-fx-font-size: 14px;");

        VBox cardDetailsBox = new VBox(8, cardDetailsLabel, cardHolderLabel, cardHolderField,
                cardNumberLabel, cardNumberField, expiryLabel, expiryField, cvvLabel, cvvField);
        cardDetailsBox.setPadding(new Insets(10));
        cardDetailsBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5;");

        Button payButton = new Button("Pay Now");
        payButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 5; -fx-padding: 8 16;");

        paymentDetails.getChildren().addAll(addressLabel, addressField, phoneLabel, phoneField,
                paymentMethodLabel, paymentMethodBox, cardDetailsBox, payButton);

        // Assemble the form in one column: Purchase Summary then Payment Details.
        VBox form = new VBox(20, summaryBox, paymentDetails);
        form.setPadding(new Insets(20));
        form.setAlignment(Pos.CENTER);
        form.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        form.setMaxWidth(500);

        // Wrap the form in a ScrollPane to make the page scrollable
        ScrollPane scrollPane = new ScrollPane(form);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 600, 700);

        payButton.setOnAction(e -> {
            // Validate that the cart is not empty
            if (cart.isEmpty()) {
                AlertUtil.showError("Your cart is empty. Please add products before checking out.");
                return;
            }
            // Validate phone number: exactly 10 digits.
            String phone = phoneField.getText().trim();
            if (!phone.matches("^\\d{10}$")) {
                AlertUtil.showError("Phone number must be exactly 10 digits.");
                return;
            }
            // Validate card holder name: alphabets and spaces only.
            String cardHolder = cardHolderField.getText().trim();
            if (!cardHolder.matches("^[A-Za-z ]+$")) {
                AlertUtil.showError("Card holder name must contain only alphabets and spaces.");
                return;
            }
            // Validate card number: exactly 16 digits.
            String cardNumber = cardNumberField.getText().trim();
            if (!cardNumber.matches("^\\d{16}$")) {
                AlertUtil.showError("Card number must be exactly 16 digits (no spaces).");
                return;
            }
            // Validate expiry date: format MM/YY with a valid month.
            String expiry = expiryField.getText().trim();
            if (!expiry.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
                AlertUtil.showError("Expiry date must be in MM/YY format with a valid month.");
                return;
            }
            // Validate CVV: exactly 3 digits.
            String cvv = cvvField.getText().trim();
            if (!cvv.matches("^\\d{3}$")) {
                AlertUtil.showError("CVV must be exactly 3 digits.");
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Payment");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Proceed with payment?");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK) {
                // Update inventory for each cart item.
                for (CartItem item : cart) {
                    Product p = item.getProduct();
                    int cartQty = item.getQuantity();
                    if (p.getQuantity() >= cartQty) {
                        p.setQuantity(p.getQuantity() - cartQty);
                        ProductDAO.updateProduct(p);
                    } else {
                        AlertUtil.showError("Product " + p.getName() + " is out of stock or insufficient.");
                        return;
                    }
                }
                // Build order details from cart.
                StringBuilder productListBuilder = new StringBuilder();
                double total = 0.0;
                for (CartItem item : cart) {
                    productListBuilder.append(item.getProduct().getName())
                            .append(" (")
                            .append(item.getQuantity())
                            .append("), ");
                    total += item.getProduct().getPrice() * item.getQuantity();
                }
                String productList = productListBuilder.toString();
                if (productList.endsWith(", ")) {
                    productList = productList.substring(0, productList.length() - 2);
                }
                Order order = new Order();
                order.setCustomerId(Session.loggedInCustomerId);
                order.setProductList(productList);
                order.setTotalPrice(total);
                if (OrderDAO.addOrder(order)) {
                    // Clear cart only after successful payment.
                    cart.clear();
                    CustomerReceiptUI.showReceipt(stage, order, addressField.getText().trim(), phoneField.getText().trim(), paymentMethodBox.getValue());
                } else {
                    AlertUtil.showError("Failed to record the order. Please try again.");
                }
            }
        });

        stage.setScene(scene);
        stage.setTitle("Payment");
        stage.setWidth(500);
        stage.setHeight(700);
        stage.setResizable(true);
        stage.show();
    }
}
