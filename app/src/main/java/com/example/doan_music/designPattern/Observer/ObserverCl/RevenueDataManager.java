package com.example.doan_music.designPattern.Observer.ObserverCl;

import android.util.Log;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.designPattern.Observer.Model.RevenueStatistics;
import com.example.doan_music.designPattern.Observer.OBserverIF.RevenueObserver;
import com.example.doan_music.designPattern.Observer.OBserverIF.RevenueSubject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RevenueDataManager implements RevenueSubject {
    private static final String TAG = "RevenueDataManager";
    private final List<RevenueObserver> observers = new ArrayList<>();
    private final RevenueStatistics statistics = new RevenueStatistics();
    private final ConnectionClass connectionClass = new ConnectionClass();
    private final int artistId;
    private static final double REVENUE_PER_VIEW = 300; // 300đ per view

    public RevenueDataManager(int artistId) {
        this.artistId = artistId;
    }

    @Override
    public void registerObserver(RevenueObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(RevenueObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (RevenueObserver observer : observers) {
            observer.onRevenueUpdate(statistics);
        }
    }

    public void fetchMonthlyRevenue() {
        new Thread(() -> {
            try (Connection connection = connectionClass.conClass()) {
                if (connection != null) {
                    String query = "SELECT MONTH(SongHistory.PlayDate) AS Month, " +
                            "COALESCE(SUM(Song.Views), 0) AS TotalViews " +
                            "FROM Song " +
                            "JOIN SongHistory ON Song.SongID = SongHistory.SongID " +
                            "WHERE Song.ArtistID = ? " +
                            "AND YEAR(SongHistory.PlayDate) = YEAR(GETDATE()) " +
                            "GROUP BY MONTH(SongHistory.PlayDate) " +
                            "ORDER BY Month";

                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setInt(1, artistId);
                        try (ResultSet resultSet = statement.executeQuery()) {
                            statistics.clearMonthlyData();
                            while (resultSet.next()) {
                                int month = resultSet.getInt("Month");
                                int views = resultSet.getInt("TotalViews");
                                statistics.addMonthlyData(month, views);
                            }
                            notifyObservers(); // Cập nhật dữ liệu cho Observer
                        }
                    }
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error fetching monthly revenue: " + e.getMessage());
            }
        }).start();
    }
}
