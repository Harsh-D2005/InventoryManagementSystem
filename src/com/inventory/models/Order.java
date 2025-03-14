package com.inventory.models;

import java.sql.Date;

public class Order {
    private int id;
    private int customerId;
    private String productList; // Comma-separated list of product names
    private double totalPrice;
    private Date orderDate; // Only date is needed
    private String orderStatus; // New field: "Pending" or "Completed"

    // Getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    public String getProductList() {
        return productList;
    }
    public void setProductList(String productList) {
        this.productList = productList;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public Date getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
