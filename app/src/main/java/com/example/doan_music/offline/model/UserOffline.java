package com.example.doan_music.offline.model;

public class UserOffline {
    private int userID;
    private String username;
    private String email;
    private String password;
    private String status;
    private String role;
    private boolean isPremium;
    private long premiumExpireDate; // Thời gian hết hạn premium

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

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public long getPremiumExpireDate() {
        return premiumExpireDate;
    }

    public void setPremiumExpireDate(long premiumExpireDate) {
        this.premiumExpireDate = premiumExpireDate;
    }

    // Getters, setters
    public boolean isPremiumValid() {
        return isPremium && premiumExpireDate > System.currentTimeMillis();
    }
}
