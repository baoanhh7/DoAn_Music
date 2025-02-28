package com.example.doan_music.designPattern.DependencyInjectionPK.Model;

public class User {
    private String username;
    private String email;
    private String phone;
    private String password;
    private String status;

    public User(String username, String email, String phone, String password, String status) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.status = status;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getStatus() { return status; }
}
