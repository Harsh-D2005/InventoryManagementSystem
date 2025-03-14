package com.inventory.admin;

import com.inventory.models.User;
import com.inventory.models.UserDAO;
import com.inventory.UI.NavigationMenu;
import com.inventory.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminCustomerUI {
    public static void showCustomerManagement(Stage stage) {
        // Create TableView for customers
        TableView<User> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 14px;");

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, Integer> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<User, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if(item.equalsIgnoreCase("Enabled")) {
                        setStyle("-fx-text-fill: green;");
                    } else if(item.equalsIgnoreCase("Disabled")) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, emailCol, ageCol, genderCol, statusCol);

        // Get all customers
        ObservableList<User> customerList = FXCollections.observableArrayList(UserDAO.getAllCustomers());

        // Create a FilteredList for real-time search filtering
        FilteredList<User> filteredData = new FilteredList<>(customerList, p -> true);

        // Search field for real-time filtering
        TextField searchField = new TextField();
        searchField.setPromptText("Search by ID or Name");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                filteredData.setPredicate(user -> true);
            } else {
                String lowerCaseFilter = newVal.trim().toLowerCase();
                try {
                    int searchId = Integer.parseInt(lowerCaseFilter);
                    filteredData.setPredicate(user -> user.getId() == searchId);
                } catch (NumberFormatException ex) {
                    filteredData.setPredicate(user -> user.getName().toLowerCase().contains(lowerCaseFilter));
                }
            }
        });

        table.setItems(filteredData);

        // Action buttons for customer management
        Button deleteButton = new Button("Delete Customer");
        deleteButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 16px; " +
                "-fx-background-radius: 5; -fx-padding: 8 16;");
        deleteButton.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if(selected != null) {
                if(UserDAO.deleteUser(selected.getId())){
                    AlertUtil.showInfo("Customer deleted successfully.");
                    customerList.setAll(UserDAO.getAllCustomers());
                } else {
                    AlertUtil.showError("Failed to delete customer.");
                }
            } else {
                AlertUtil.showError("No customer selected.");
            }
        });

        Button toggleStatusButton = new Button("Toggle Status");
        toggleStatusButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 16px; " +
                "-fx-background-radius: 5; -fx-padding: 8 16;");
        toggleStatusButton.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if(selected != null) {
                String newStatus = selected.getStatus().equalsIgnoreCase("Enabled") ? "Disabled" : "Enabled";
                if(UserDAO.updateUserStatus(selected.getId(), newStatus)){
                    AlertUtil.showInfo("Status updated to " + newStatus + ".");
                    customerList.setAll(UserDAO.getAllCustomers());
                } else {
                    AlertUtil.showError("Failed to update status.");
                }
            } else {
                AlertUtil.showError("No customer selected.");
            }
        });

        Button editButton = new Button("Edit Customer");
        editButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 16px; " +
                "-fx-background-radius: 5; -fx-padding: 8 16;");
        editButton.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if(selected != null){
                showEditCustomerDialog(selected, stage, customerList);
            } else {
                AlertUtil.showError("No customer selected.");
            }
        });

        HBox buttonBox = new HBox(10, deleteButton, toggleStatusButton, editButton);
        buttonBox.setPadding(new Insets(10));

        VBox content = new VBox(10, searchField, table, buttonBox);
        content.setPadding(new Insets(10));

        VBox layout = new VBox(NavigationMenu.getNavigationBar("admin", stage), content);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0eafc, #cfdef3);");
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Manage Customers");
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setResizable(true);
        stage.show();
    }

    private static void showEditCustomerDialog(User user, Stage stage, ObservableList<User> customerList) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit Customer");
        dialog.setHeaderText("Edit details for " + user.getName());

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(user.getName());
        TextField emailField = new TextField(user.getEmail());
        TextField ageField = new TextField(String.valueOf(user.getAge()));

        // Use a ComboBox for gender with options "Male", "Female", and "Other"
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        String currentGender = user.getGender();
        if (currentGender.equalsIgnoreCase("Male") || currentGender.equalsIgnoreCase("Female") || currentGender.equalsIgnoreCase("Other")) {
            genderComboBox.setValue(currentGender);
        } else {
            genderComboBox.setValue("Other");
        }
        genderComboBox.setStyle("-fx-font-size: 14px;");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Age:"), 0, 2);
        grid.add(ageField, 1, 2);
        grid.add(new Label("Gender:"), 0, 3);
        grid.add(genderComboBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                user.setName(nameField.getText());
                user.setEmail(emailField.getText());
                try {
                    int newAge = Integer.parseInt(ageField.getText());
                    if(newAge < 1 || newAge > 100) {
                        AlertUtil.showError("Age must be between 1 and 100.");
                        return null;
                    }
                    user.setAge(newAge);
                } catch (NumberFormatException ex) {
                    AlertUtil.showError("Invalid age entered.");
                    return null;
                }
                user.setGender(genderComboBox.getValue());
                return user;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedUser -> {
            if (UserDAO.updateUser(updatedUser)) {
                AlertUtil.showInfo("Customer updated successfully.");
                customerList.setAll(UserDAO.getAllCustomers());
            } else {
                AlertUtil.showError("Failed to update customer.");
            }
        });
    }
}
