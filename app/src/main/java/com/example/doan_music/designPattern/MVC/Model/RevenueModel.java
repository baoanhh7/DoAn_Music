package com.example.doan_music.designPattern.MVC.Model;

public class RevenueModel {
    private int month;
    private int totalViews;
    private long totalRevenue;

    public RevenueModel(int month, int totalViews) {
        this.month = month;
        this.totalViews = totalViews;
        this.totalRevenue = calculateRevenue(totalViews);
    }

    private long calculateRevenue(int views) {
        return views * 300;  // 300đ mỗi view
    }

    public int getMonth() {
        return month;
    }

    public int getTotalViews() {
        return totalViews;
    }

    public long getTotalRevenue() {
        return totalRevenue;
    }
}