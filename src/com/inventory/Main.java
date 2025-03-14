package com.inventory;

import com.inventory.UI.LoginUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Launch the login page at startup
        LoginUI.showLoginPage(stage);
    }
}
