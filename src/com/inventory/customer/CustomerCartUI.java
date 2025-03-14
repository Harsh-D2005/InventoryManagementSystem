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
import java.util.Optional;

public class CustomerCartUI {
    public static void showCart(Stage stage) {
        Label title = new Label("Your Cart");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 10 0 20 0;");

        TableView<CartItem> cartTable = new TableView<>();
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cartTable.setStyle("-fx-font-size: 14px;");
        ObservableList<CartItem> cart = CustomerProductUI.cart;
        cartTable.setItems(cart);

        TableColumn<CartItem, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getName()));

        TableColumn<CartItem, Double> unitPriceCol = new TableColumn<>("Unit Price");
        unitPriceCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getProduct().getPrice()).asObject());

        TableColumn<CartItem, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        TableColumn<CartItem, Double> totalCol = new TableColumn<>("Total Price");
        totalCol.setCellValueFactory(cellData -> {
            double total = cellData.getValue().getProduct().getPrice() * cellData.getValue().getQuantity();
            return new SimpleDoubleProperty(total).asObject();
        });

        TableColumn<CartItem, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<CartItem, Void>() {
            private final Button removeButton = new Button("Remove");
            private final Button editButton = new Button("Edit Quantity");
            private final HBox pane = new HBox(5, removeButton, editButton);

            {
                removeButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-background-radius: 5; -fx-padding: 5 10;");
                editButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-background-radius: 5; -fx-padding: 5 10;");
                removeButton.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cart.remove(item);
                    AlertUtil.showInfo(item.getProduct().getName() + " removed from cart.");
                });
                editButton.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(String.valueOf(item.getQuantity()));
                    dialog.setTitle("Edit Quantity");
                    dialog.setHeaderText("Edit quantity for " + item.getProduct().getName());
                    dialog.setContentText("New Quantity:");
                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(qtyStr -> {
                        try {
                            int newQty = Integer.parseInt(qtyStr);
                            if(newQty <= 0) {
                                AlertUtil.showError("Quantity must be greater than 0.");
                                return;
                            }
                            int available = item.getProduct().getQuantity();
                            if(newQty > available) {
                                AlertUtil.showError("Requested quantity exceeds available stock (" + available + ").");
                                return;
                            }
                            item.setQuantity(newQty);
                            cartTable.refresh();
                            AlertUtil.showInfo("Quantity updated for " + item.getProduct().getName());
                        } catch(NumberFormatException ex) {
                            AlertUtil.showError("Please enter a valid number.");
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        cartTable.getColumns().addAll(nameCol, unitPriceCol, quantityCol, totalCol, actionsCol);

        // Checkout button now checks if the cart is empty immediately.
        Button checkoutButton = new Button("Checkout");
        checkoutButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-background-radius: 5; -fx-padding: 5 10;");
        checkoutButton.setOnAction(e -> {
            if (cart.isEmpty()) {
                AlertUtil.showError("Your cart is empty. Please add products before checking out.");
            } else {
                CustomerPaymentUI.showPayment(stage);
            }
        });

        VBox content = new VBox(10, title, cartTable, checkoutButton);
        content.setPadding(new Insets(10));
        VBox layout = new VBox(NavigationMenu.getNavigationBar("customer", stage), content);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0eafc, #cfdef3);");

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Your Cart");
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setResizable(true);
        stage.show();
    }
}
