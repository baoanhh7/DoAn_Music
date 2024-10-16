package com.example.doan_music.model;

import java.io.Serializable;

public class Users implements Serializable {
    // Thuộc tính của người dùng
    private int userID;
    private String username;
    private String email;
    private String password;
    private String status;
    private String role;

    // Constructor không tham số
    public Users() {
    }

    // Constructor đầy đủ tham số
    public Users(int userID, String username, String email, String password, String status, String role) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
        this.role = role;
    }

    // Getter và Setter
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
