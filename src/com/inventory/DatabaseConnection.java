package com.inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Update these with your MySQL database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/inventorydb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "harsh123";

    public static Connection getConnection() throws SQLException {
        try {
            // Load the MySQL JDBC driver (if needed)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found.");
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
