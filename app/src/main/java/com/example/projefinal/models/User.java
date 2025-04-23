package com.example.projefinal.models;

public class User {
    private int id;
    private String phone;
    private String password;
    private boolean isAdmin;

    public User() {}

    public User(String phone, String password, boolean isAdmin) {
        this.phone = phone;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public User(int id, String phone, String password, boolean isAdmin) {
        this.id = id;
        this.phone = phone;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // Getters
    public int getId() { return id; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public boolean isAdmin() { return isAdmin; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
}
