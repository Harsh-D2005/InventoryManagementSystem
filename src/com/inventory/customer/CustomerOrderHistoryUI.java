package com.inventory.customer;

import com.inventory.Session;
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

import java.util.List;

public class CustomerOrderHistoryUI {
    public static void showOrderHistory(Stage stage) {
        Label title = new Label("Your Order History");

        // TableView to display orders
        TableView<Order> table = new TableView<>();
        TableColumn<Order, Integer> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Order, String> productListCol = new TableColumn<>("Products");
        productListCol.setCellValueFactory(new PropertyValueFactory<>("productList"));
        TableColumn<Order, Double> totalCol = new TableColumn<>("Total Price");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        TableColumn<Order, String> dateCol = new TableColumn<>("Order Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        table.getColumns().addAll(idCol, productListCol, totalCol, dateCol);

        // Fetch orders for the logged-in customer
        List<Order> orders = OrderDAO.getOrdersByCustomer(Session.loggedInCustomerId);
        ObservableList<Order> orderList = FXCollections.observableArrayList(orders);
        table.setItems(orderList);

        Button backButton = new Button("Back to Products");
        backButton.setOnAction(e -> CustomerProductUI.showProductListing(stage));

        VBox content = new VBox(10, title, table, backButton);
        content.setPadding(new Insets(10));

        VBox layout = new VBox();
        layout.getChildren().addAll(NavigationMenu.getNavigationBar("customer", stage), content);

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Order History");
        stage.show();
    }
}
