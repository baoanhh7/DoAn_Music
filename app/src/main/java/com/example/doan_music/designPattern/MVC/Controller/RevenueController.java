package com.example.doan_music.designPattern.MVC.Controller;

import android.util.Log;

import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.designPattern.MVC.Model.RevenueModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RevenueController {
    private static final String TAG = "RevenueController";
    private ConnectionClass connectionClass;

    public RevenueController() {
        this.connectionClass = new ConnectionClass();
    }

    public List<RevenueModel> getRevenueData(int artistId) {
        List<RevenueModel> revenueList = new ArrayList<>();
        Connection connection = connectionClass.conClass();

        if (connection != null) {
            try {
                String query = "SELECT MONTH(DATEADD(MONTH, number, GETDATE())) AS Month, " +
                        "COALESCE(SUM(Views), 0) AS TotalViews " +
                        "FROM master.dbo.spt_values " +
                        "LEFT JOIN Song ON Song.ArtistID = ? " +
                        "WHERE type = 'P' AND number BETWEEN 0 AND 11 " +
                        "GROUP BY MONTH(DATEADD(MONTH, number, GETDATE())) " +
                        "ORDER BY MONTH(DATEADD(MONTH, number, GETDATE()))";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, artistId);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int month = resultSet.getInt("Month");
                    int views = resultSet.getInt("TotalViews");

                    Log.d(TAG, "Month: " + month + ", Views: " + views);
                    revenueList.add(new RevenueModel(month, views));
                }

                preparedStatement.close();
                resultSet.close();
                connection.close();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching revenue data: " + e.getMessage());
            }
        }

        return revenueList;
    }
}