package com.inventory.utils;

import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;
import com.inventory.models.Product;

public class UIHelper {
    public static void addProductSearchFilter(TextField searchField, FilteredList<Product> filteredList) {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String lowerCaseFilter = newValue == null ? "" : newValue.toLowerCase();
            filteredList.setPredicate(product -> {
                if (lowerCaseFilter.isEmpty()) {
                    return true;
                }
                return product.getName().toLowerCase().contains(lowerCaseFilter) ||
                        product.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                        product.getCategory().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }
}
