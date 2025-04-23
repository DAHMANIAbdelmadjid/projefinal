package com.example.projefinal.models;

public class Product {
    private String barcode;
    private String name;
    private String type;
    private String brand;
    private double price;
    private String imageUrl;
    private boolean isArchived;

    public Product() {}

    public Product(String barcode, String name, String type, String brand, double price) {
        this.barcode = barcode;
        this.name = name;
        this.type = type;
        this.brand = brand;
        this.price = price;
        this.isArchived = false;
    }

    public Product(String barcode, String name, String type, String brand, double price, String imageUrl) {
        this(barcode, name, type, brand, price);
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getBrand() { return brand; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public boolean isArchived() { return isArchived; }

    // Setters
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setArchived(boolean archived) { isArchived = archived; }
}
