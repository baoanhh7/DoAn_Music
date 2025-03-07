package com.example.doan_music.designPattern.Observer.Model;

import java.util.HashMap;
import java.util.Map;

public class RevenueStatistics {
    private int totalViews;
    private long totalRevenue;

    public int getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(int totalViews) {
        this.totalViews = totalViews;
    }

    public long getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(long totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    private final Map<Integer, Integer> monthlyViews = new HashMap<>();

    public void addMonthlyData(int month, int views) {
        monthlyViews.put(month, views);
    }

    public void clearMonthlyData() {
        monthlyViews.clear();
    }

    public Map<Integer, Integer> getMonthlyViews() {
        return monthlyViews;
    }
}
