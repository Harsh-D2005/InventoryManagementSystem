package com.inventory.admin;

import com.inventory.models.Order;
import com.inventory.models.OrderDAO;
import com.inventory.UI.NavigationMenu;
import com.inventory.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdminOrderHistoryUI {

    // Inner class to represent a summarized order in one row.
    public static class OrderSummary {
        private int orderId;
        private String productNames;      // Product names separated by newline
        private String productQuantities; // Quantities separated by newline
        private double totalPrice;
        private String orderDate;         // Formatted date string
        private String orderStatus;

        public OrderSummary(int orderId, String productNames, String productQuantities,
                            double totalPrice, String orderDate, String orderStatus) {
            this.orderId = orderId;
            this.productNames = productNames;
            this.productQuantities = productQuantities;
            this.totalPrice = totalPrice;
            this.orderDate = orderDate;
            this.orderStatus = orderStatus;
        }

        public int getOrderId() {
            return orderId;
        }
        public String getProductNames() {
            return productNames;
        }
        public String getProductQuantities() {
            return productQuantities;
        }
        public double getTotalPrice() {
            return totalPrice;
        }
        public String getOrderDate() {
            return orderDate;
        }
        public String getOrderStatus() {
            return orderStatus;
        }
    }

    // Helper method to transform an Order into an OrderSummary.
    // It splits the productList string (format: "iPhone (5), MacBook (2), AirPods (3)")
    // into two newline-separated strings: one for product names and one for quantities.
    private static OrderSummary createOrderSummary(Order order) {
        String productList = order.getProductList();
        StringBuilder namesBuilder = new StringBuilder();
        StringBuilder qtyBuilder = new StringBuilder();
        if (productList != null && !productList.trim().isEmpty()) {
            String[] parts = productList.split(",\\s*");
            for (String part : parts) {
                int idx = part.lastIndexOf("(");
                int idx2 = part.lastIndexOf(")");
                if (idx != -1 && idx2 != -1 && idx < idx2) {
                    String name = part.substring(0, idx).trim();
                    String qtyStr = part.substring(idx + 1, idx2).trim();
                    namesBuilder.append(name).append("\n");
                    qtyBuilder.append(qtyStr).append("\n");
                }
            }
            // Remove trailing newline if present
            if(namesBuilder.length() > 0) {
                namesBuilder.setLength(namesBuilder.length() - 1);
            }
            if(qtyBuilder.length() > 0) {
                qtyBuilder.setLength(qtyBuilder.length() - 1);
            }
        }
        String formattedDate = order.getOrderDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return new OrderSummary(order.getId(), namesBuilder.toString(), qtyBuilder.toString(),
                order.getTotalPrice(), formattedDate, order.getOrderStatus());
    }

    public static void showOrderHistory(Stage stage) {
        Label title = new Label("Order History");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 10 0 20 0;");

        // Fetch all orders.
        List<Order> allOrders = OrderDAO.getAllOrders();

        // Separate orders into pending and delivered.
        List<Order> pendingOrdersList = new ArrayList<>();
        List<Order> deliveredOrdersList = new ArrayList<>();
        for (Order order : allOrders) {
            if (order.getOrderStatus().equalsIgnoreCase("Pending")) {
                pendingOrdersList.add(order);
            } else if (order.getOrderStatus().equalsIgnoreCase("Delivered")) {
                deliveredOrdersList.add(order);
            }
        }

        // Convert orders to OrderSummary objects.
        ObservableList<OrderSummary> pendingSummaries = FXCollections.observableArrayList();
        for (Order order : pendingOrdersList) {
            pendingSummaries.add(createOrderSummary(order));
        }
        ObservableList<OrderSummary> deliveredSummaries = FXCollections.observableArrayList();
        for (Order order : deliveredOrdersList) {
            deliveredSummaries.add(createOrderSummary(order));
        }

        // Create the table for displaying order summaries.
        TableView<OrderSummary> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 14px;");

        TableColumn<OrderSummary, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderIdCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<OrderSummary, String> productNamesCol = new TableColumn<>("Product Name");
        productNamesCol.setCellValueFactory(new PropertyValueFactory<>("productNames"));
        productNamesCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<OrderSummary, String> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("productQuantities"));
        quantityCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<OrderSummary, Double> totalPriceCol = new TableColumn<>("Total Price");
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceCol.setStyle("-fx-alignment: CENTER;");
        totalPriceCol.setCellFactory(col -> new TableCell<OrderSummary, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item == null){
                    setText(null);
                } else {
                    setText("₹" + String.format("%.2f", item));
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        TableColumn<OrderSummary, String> dateCol = new TableColumn<>("Order Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        dateCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<OrderSummary, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        statusCol.setStyle("-fx-alignment: CENTER;");
        statusCol.setCellFactory(col -> new TableCell<OrderSummary, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item == null){
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-alignment: CENTER;");
                    if(item.equalsIgnoreCase("Delivered")){
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: green;");
                    } else if(item.equalsIgnoreCase("Pending")){
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: orange;");
                    }
                }
            }
        });

        // Action column for pending orders: "Mark as Delivered" button.
        TableColumn<OrderSummary, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<OrderSummary, Void>() {
            private final Button markDeliveredButton = new Button("Mark as Delivered");
            {
                markDeliveredButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 12px; " +
                        "-fx-background-radius: 5; -fx-padding: 3 8;");
                markDeliveredButton.setOnAction(e -> {
                    OrderSummary summary = getTableView().getItems().get(getIndex());
                    // Confirmation dialog
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirm Delivery");
                    confirmAlert.setHeaderText(null);
                    confirmAlert.setContentText("Are you sure you want to mark order " + summary.getOrderId() + " as Delivered?");
                    ButtonType result = confirmAlert.showAndWait().orElse(ButtonType.CANCEL);
                    if(result == ButtonType.OK) {
                        if(OrderDAO.updateOrderStatus(summary.getOrderId(), "Delivered")){
                            AlertUtil.showInfo("Order marked as Delivered.");
                            pendingSummaries.remove(summary);
                            summary = new OrderSummary(summary.getOrderId(), summary.getProductNames(), summary.getProductQuantities(),
                                    summary.getTotalPrice(), summary.getOrderDate(), "Delivered");
                            deliveredSummaries.add(summary);
                        } else {
                            AlertUtil.showError("Failed to update order status.");
                        }
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                // Show the button only for pending orders.
                if (empty || getTableView().getItems().get(getIndex()).getOrderStatus().equalsIgnoreCase("Delivered")) {
                    setGraphic(null);
                } else {
                    setGraphic(markDeliveredButton);
                }
            }
        });

        // Add columns to table.
        table.getColumns().addAll(orderIdCol, productNamesCol, quantityCol, totalPriceCol, dateCol, statusCol, actionCol);

        // Create a TabPane with two tabs: Pending Orders and Delivered Orders.
        TableView<OrderSummary> pendingTable = new TableView<>(pendingSummaries);
        pendingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        pendingTable.getColumns().addAll(orderIdCol, productNamesCol, quantityCol, totalPriceCol, dateCol, statusCol, actionCol);

        // For delivered orders, create new instances of columns.
        TableColumn<OrderSummary, Integer> orderIdCol2 = new TableColumn<>("Order ID");
        orderIdCol2.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderIdCol2.setStyle("-fx-alignment: CENTER;");

        TableColumn<OrderSummary, String> productNamesCol2 = new TableColumn<>("Product Name");
        productNamesCol2.setCellValueFactory(new PropertyValueFactory<>("productNames"));
        productNamesCol2.setStyle("-fx-alignment: CENTER;");

        TableColumn<OrderSummary, String> quantityCol2 = new TableColumn<>("Quantity");
        quantityCol2.setCellValueFactory(new PropertyValueFactory<>("productQuantities"));
        quantityCol2.setStyle("-fx-alignment: CENTER;");

        TableColumn<OrderSummary, Double> totalPriceCol2 = new TableColumn<>("Total Price");
        totalPriceCol2.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceCol2.setStyle("-fx-alignment: CENTER;");
        totalPriceCol2.setCellFactory(col -> new TableCell<OrderSummary, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item == null){
                    setText(null);
                } else {
                    setText("₹" + String.format("%.2f", item));
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        TableColumn<OrderSummary, String> dateCol2 = new TableColumn<>("Order Date");
        dateCol2.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        dateCol2.setStyle("-fx-alignment: CENTER;");

        TableColumn<OrderSummary, String> statusCol2 = new TableColumn<>("Status");
        statusCol2.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        statusCol2.setStyle("-fx-alignment: CENTER;");
        statusCol2.setCellFactory(col -> new TableCell<OrderSummary, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item == null){
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-alignment: CENTER;");
                    if(item.equalsIgnoreCase("Delivered")){
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: green;");
                    } else if(item.equalsIgnoreCase("Pending")){
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: orange;");
                    }
                }
            }
        });

        TableView<OrderSummary> deliveredTable = new TableView<>(deliveredSummaries);
        deliveredTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        deliveredTable.getColumns().addAll(orderIdCol2, productNamesCol2, quantityCol2, totalPriceCol2, dateCol2, statusCol2);

        Tab pendingTab = new Tab("Pending Orders", pendingTable);
        Tab deliveredTab = new Tab("Delivered Orders", deliveredTable);
        TabPane tabPane = new TabPane(pendingTab, deliveredTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 5; -fx-padding: 8 16;");
        backButton.setOnAction(e -> AdminDashboardUI.showDashboard(stage));

        VBox content = new VBox(10, title, tabPane, backButton);
        content.setPadding(new Insets(10));
        VBox layout = new VBox(NavigationMenu.getNavigationBar("admin", stage), content);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0eafc, #cfdef3);");

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Order History");
        stage.setWidth(850);
        stage.setHeight(650);
        stage.setResizable(true);
        stage.show();
    }
}
