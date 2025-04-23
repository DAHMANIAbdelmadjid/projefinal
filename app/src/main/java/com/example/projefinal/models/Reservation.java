package com.example.projefinal.models;

public class Reservation {
    private int id;
    private int userId;
    private String productBarcode;
    private String status; // PENDING, CONFIRMED, CANCELLED
    private long timestamp;
    private String userPhone; // For easier access to contact information
    private String productName; // For easier display of product information

    public Reservation() {}

    public Reservation(int userId, String productBarcode) {
        this.userId = userId;
        this.productBarcode = productBarcode;
        this.status = "PENDING";
        this.timestamp = System.currentTimeMillis();
    }

    public Reservation(int id, int userId, String productBarcode, String status, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.productBarcode = productBarcode;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getProductBarcode() { return productBarcode; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
    public String getUserPhone() { return userPhone; }
    public String getProductName() { return productName; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setProductBarcode(String productBarcode) { this.productBarcode = productBarcode; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }
    public void setProductName(String productName) { this.productName = productName; }

    // Helper methods
    public boolean isPending() { return "PENDING".equals(status); }
    public boolean isConfirmed() { return "CONFIRMED".equals(status); }
    public boolean isCancelled() { return "CANCELLED".equals(status); }
}
