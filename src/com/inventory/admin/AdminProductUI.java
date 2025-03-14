package com.inventory.admin;

import com.inventory.models.Product;
import com.inventory.models.ProductDAO;
import com.inventory.UI.NavigationMenu;
import com.inventory.utils.AlertUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

import java.text.DecimalFormat;

public class AdminProductUI {
    private static final int LOW_STOCK_THRESHOLD = 5;

    public static void showProductManagement(Stage stage) {
        // Title label with modern styling
        Label titleLabel = new Label("Tech Products Inventory");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 10 0 20 0;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10));

        // Navigation bar (with its own inline styling via NavigationMenu)
        BorderPane navBar = NavigationMenu.getNavigationBar("admin", stage);

        // Search field and Low Stock Only checkbox
        TextField searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchField.setMinWidth(500);
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        CheckBox lowStockOnly = new CheckBox("Low Stock Only");
        lowStockOnly.setStyle("-fx-font-size: 14px;");

        HBox searchBox = new HBox(10, searchField, lowStockOnly);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        // TableView to display products with updated column resize policy
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

        // Total Price column: quantity * price, formatted with rupee symbol
        TableColumn<Product, Double> totalPriceCol = new TableColumn<>("Total Price");
        totalPriceCol.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getQuantity() * cellData.getValue().getPrice())
        );
        totalPriceCol.setStyle("-fx-alignment: CENTER;");
        totalPriceCol.setCellFactory(column -> new TableCell<Product, Double>() {
            private final DecimalFormat df = new DecimalFormat("0.00");
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText("₹" + df.format(value));
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, descriptionCol, categoryCol, quantityCol, priceCol, totalPriceCol);

        // Highlight low stock rows in a light red background
        table.setRowFactory(tv -> new TableRow<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.getQuantity() < LOW_STOCK_THRESHOLD) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else {
                    setStyle("");
                }
            }
        });

        ObservableList<Product> productList = FXCollections.observableArrayList(ProductDAO.getAllProducts());
        FilteredList<Product> filteredData = new FilteredList<>(productList, p -> true);

        Runnable updatePredicate = () -> {
            String filter = searchField.getText();
            boolean lowStockFilter = lowStockOnly.isSelected();
            filteredData.setPredicate(product -> {
                boolean matchesSearch = true;
                if (filter != null && !filter.isEmpty()) {
                    String lower = filter.toLowerCase();
                    matchesSearch = product.getName().toLowerCase().contains(lower)
                            || product.getDescription().toLowerCase().contains(lower)
                            || product.getCategory().toLowerCase().contains(lower);
                }
                boolean matchesLowStock = !lowStockFilter || product.getQuantity() < LOW_STOCK_THRESHOLD;
                return matchesSearch && matchesLowStock;
            });
        };

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updatePredicate.run());
        lowStockOnly.selectedProperty().addListener((obs, oldVal, newVal) -> updatePredicate.run());

        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        // CRUD form for product management
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));
        formGrid.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5;");

        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 5 10;");
        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 5 10;");
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 5 10;");
        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 5 10;");

        formGrid.add(new Label("Name:"), 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(new Label("Description:"), 2, 0);
        formGrid.add(descriptionField, 3, 0);
        formGrid.add(new Label("Category:"), 0, 1);
        formGrid.add(categoryField, 1, 1);
        formGrid.add(new Label("Quantity:"), 2, 1);
        formGrid.add(quantityField, 3, 1);
        formGrid.add(new Label("Price:"), 0, 2);
        formGrid.add(priceField, 1, 2);
        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton, clearButton);
        buttonBox.setAlignment(Pos.CENTER);
        formGrid.add(buttonBox, 0, 3, 4, 1);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameField.setText(newVal.getName());
                descriptionField.setText(newVal.getDescription());
                categoryField.setText(newVal.getCategory());
                quantityField.setText(String.valueOf(newVal.getQuantity()));
                priceField.setText(String.valueOf(newVal.getPrice()));
            }
        });

        Runnable clearForm = () -> {
            nameField.clear();
            descriptionField.clear();
            categoryField.clear();
            quantityField.clear();
            priceField.clear();
            table.getSelectionModel().clearSelection();
        };

        addButton.setOnAction(e -> {
            if (table.getSelectionModel().getSelectedItem() != null) {
                AlertUtil.showError("Please clear selection before adding a new product.");
                return;
            }
            try {
                if (nameField.getText().isEmpty() || descriptionField.getText().isEmpty() ||
                        categoryField.getText().isEmpty() || quantityField.getText().isEmpty() || priceField.getText().isEmpty()) {
                    AlertUtil.showError("Please fill in all fields.");
                    return;
                }
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
                if (quantity < 0 || price < 0) {
                    AlertUtil.showError("Quantity and Price must be non-negative.");
                    return;
                }
                Product product = new Product();
                product.setName(nameField.getText());
                product.setDescription(descriptionField.getText());
                product.setCategory(categoryField.getText());
                product.setQuantity(quantity);
                product.setPrice(price);
                if (ProductDAO.addProduct(product)) {
                    AlertUtil.showInfo("Product added successfully.");
                    productList.setAll(ProductDAO.getAllProducts());
                    clearForm.run();
                } else {
                    AlertUtil.showError("Failed to add product.");
                }
            } catch (NumberFormatException ex) {
                AlertUtil.showError("Enter valid numeric values for quantity and price.");
            }
        });

        updateButton.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    if (nameField.getText().isEmpty() || descriptionField.getText().isEmpty() ||
                            categoryField.getText().isEmpty() || quantityField.getText().isEmpty() || priceField.getText().isEmpty()) {
                        AlertUtil.showError("Please fill in all fields.");
                        return;
                    }
                    int quantity = Integer.parseInt(quantityField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    if (quantity < 0 || price < 0) {
                        AlertUtil.showError("Quantity and Price must be non-negative.");
                        return;
                    }
                    selected.setName(nameField.getText());
                    selected.setDescription(descriptionField.getText());
                    selected.setCategory(categoryField.getText());
                    selected.setQuantity(quantity);
                    selected.setPrice(price);
                    if (ProductDAO.updateProduct(selected)) {
                        AlertUtil.showInfo("Product updated successfully.");
                        productList.setAll(ProductDAO.getAllProducts());
                        clearForm.run();
                    } else {
                        AlertUtil.showError("Failed to update product.");
                    }
                } catch (NumberFormatException ex) {
                    AlertUtil.showError("Enter valid numeric values for quantity and price.");
                }
            } else {
                AlertUtil.showError("No product selected for update.");
            }
        });

        deleteButton.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (ProductDAO.deleteProduct(selected.getId())) {
                    AlertUtil.showInfo("Product deleted successfully.");
                    productList.setAll(ProductDAO.getAllProducts());
                    clearForm.run();
                } else {
                    AlertUtil.showError("Failed to delete product.");
                }
            } else {
                AlertUtil.showError("No product selected for deletion.");
            }
        });

        clearButton.setOnAction(e -> clearForm.run());

        VBox mainContent = new VBox(10, searchBox, table, formGrid);
        mainContent.setPadding(new Insets(10));
        mainContent.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, titleBox, navBar, mainContent);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0eafc, #cfdef3);");
        Scene scene = new Scene(layout, 900, 650);
        stage.setScene(scene);
        stage.setTitle("Manage Products");
        stage.setWidth(1200);
        stage.setHeight(720);
        stage.setResizable(true);
        stage.show();
    }
}
