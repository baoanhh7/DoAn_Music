package com.example.doan_music.activity.admin.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_music.R;
import com.example.doan_music.database.ConnectionClass;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ViewRevenueAdminActivity extends AppCompatActivity {

    private GraphView lineChart;
    private TextView tvTotalRevenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_revenue_admin);

        lineChart = findViewById(R.id.lineChart);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);

        setupChart();
        loadData();
    }

    private void setupChart() {
        lineChart.getViewport().setScalable(true);
        lineChart.getViewport().setScalableY(true);
        lineChart.getViewport().setXAxisBoundsManual(true);
        lineChart.getViewport().setMinX(0);
        lineChart.getViewport().setMaxX(11);

        lineChart.getViewport().setYAxisBoundsManual(true);
        lineChart.getViewport().setMinY(0);
        lineChart.getViewport().setMaxY(10000000); // Điều chỉnh giá trị tối đa dựa trên dữ liệu thực tế

        lineChart.getGridLabelRenderer().setHorizontalAxisTitle("Months");
        lineChart.getGridLabelRenderer().setVerticalAxisTitle("Revenue (VNĐ)");
        lineChart.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        lineChart.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        lineChart.getGridLabelRenderer().setGridColor(Color.GRAY);
    }

    private void loadData() {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.conClass();
        if (connection != null) {
            try {
                // Query để lấy tổng doanh thu theo tháng trong năm hiện tại
                String query = "SELECT MONTH(Date) as Month, " +
                        "SUM(Total) as TotalRevenue " +
                        "FROM HoaDon_Admin " +
                        "WHERE YEAR(Date) = YEAR(GETDATE()) " +
                        "GROUP BY MONTH(Date) " +
                        "ORDER BY Month";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                List<DataPoint> dataPoints = new ArrayList<>();
                double totalRevenue = 0;

                while (resultSet.next()) {
                    int month = resultSet.getInt("Month");
                    double revenue = resultSet.getDouble("TotalRevenue");

                    Log.d("DBResult", "Month: " + month + ", Revenue: " + revenue);

                    totalRevenue += revenue;
                    dataPoints.add(new DataPoint(month, revenue));
                }

                DataPoint[] dataPointsArray = dataPoints.toArray(new DataPoint[0]);
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPointsArray);
                series.setColor(Color.WHITE);
                series.setThickness(5);
                series.setDrawDataPoints(true);
                series.setDataPointsRadius(10);
                series.setAnimated(true);

                lineChart.removeAllSeries(); // Xóa series cũ (nếu có)
                lineChart.addSeries(series);
                lineChart.onDataChanged(true, true);

                // Hiển thị tổng doanh thu
                tvTotalRevenue.setText(String.format("Tổng doanh thu: %,.0f đ", totalRevenue));

                preparedStatement.close();
                resultSet.close();
            } catch (Exception e) {
                Log.e("Database Error", "Error loading data: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.e("Database Error", "Connection is null");
        }
    }
}