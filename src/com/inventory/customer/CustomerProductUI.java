package com.inventory.customer;

import com.inventory.models.Product;
import com.inventory.models.CartItem;
import com.inventory.models.ProductDAO;
import com.inventory.UI.NavigationMenu;
import com.inventory.utils.AlertUtil;
import com.inventory.utils.UIHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Optional;

public class CustomerProductUI {
    public static ObservableList<CartItem> cart = FXCollections.observableArrayList();

    public static void showProductListing(Stage stage) {
        Label titleLabel = new Label("Tech Products Inventory");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 10 0 20 0;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10));

        BorderPane navBar = NavigationMenu.getNavigationBar("customer", stage);

        TextField searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchField.setMaxWidth(300);

        TableView<Product> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 14px;");

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Product, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Product, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<Product, Void>() {
            private final Button addButton = new Button("Add to Cart");
            {
                addButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-background-radius: 5; -fx-padding: 5 10;");
                addButton.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    if(product.getQuantity() <= 0){
                        AlertUtil.showError("Product Unavailable.");
                        return;
                    }
                    TextInputDialog dialog = new TextInputDialog("1");
                    dialog.setTitle("Select Quantity");
                    dialog.setHeaderText("Enter quantity for " + product.getName());
                    dialog.setContentText("Quantity:");
                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(qtyStr -> {
                        try {
                            int qty = Integer.parseInt(qtyStr);
                            if(qty > product.getQuantity()){
                                AlertUtil.showError("Requested quantity exceeds available stock (" + product.getQuantity() + ").");
                                return;
                            }
                            if(qty <= 0){
                                AlertUtil.showError("Quantity must be greater than 0.");
                                return;
                            }
                            CartItem existingItem = cart.stream()
                                    .filter(item -> item.getProduct().getId() == product.getId())
                                    .findFirst()
                                    .orElse(null);
                            if(existingItem != null){
                                if(existingItem.getQuantity() + qty > product.getQuantity()){
                                    AlertUtil.showError("Total quantity in cart exceeds available stock (" + product.getQuantity() + ").");
                                    return;
                                }
                                existingItem.setQuantity(existingItem.getQuantity() + qty);
                                AlertUtil.showInfo(product.getName() + " quantity updated in cart.");
                            } else {
                                cart.add(new CartItem(product, qty));
                                AlertUtil.showInfo(product.getName() + " added to cart.");
                            }
                        } catch(NumberFormatException ex) {
                            AlertUtil.showError("Please enter a valid number.");
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if(empty){
                    setGraphic(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    if(product.getQuantity() <= 0){
                        Label unavailableLabel = new Label("Product Unavailable");
                        unavailableLabel.setStyle("-fx-text-fill: red; -fx-font-weight: normal;");
                        setGraphic(unavailableLabel);
                    } else {
                        setGraphic(addButton);
                    }
                }
            }
        });
        table.getColumns().addAll(idCol, nameCol, descriptionCol, categoryCol, quantityCol, priceCol, actionCol);

        ObservableList<Product> products = FXCollections.observableArrayList(ProductDAO.getAllProducts());
        FilteredList<Product> filteredData = new FilteredList<>(products, p -> true);
        UIHelper.addProductSearchFilter(searchField, filteredData);
        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        Button viewCartButton = new Button("View Cart");
        viewCartButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-background-radius: 5; -fx-padding: 5 10;");
        viewCartButton.setOnAction(e -> CustomerCartUI.showCart(stage));

        HBox searchBox = new HBox(10, searchField, viewCartButton);
        searchBox.setPadding(new Insets(10));
        VBox content = new VBox(10, searchBox, table);
        content.setPadding(new Insets(10));
        VBox layout = new VBox(10, titleBox, navBar, content);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0eafc, #cfdef3);");

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Product Listing");
        stage.setWidth(1000);
        stage.setHeight(700);
        stage.setResizable(true);
        stage.show();
    }
}
