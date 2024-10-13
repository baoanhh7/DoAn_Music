package com.example.doan_music.activity.Artist;

import android.graphics.Color;
import android.os.Bundle;
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
import java.util.Calendar;
import java.util.List;

public class ViewRevenueArtistActivity extends AppCompatActivity {

    private GraphView lineChart;
    private TextView tvTotalRevenue;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_revenue_artist);

        lineChart = findViewById(R.id.lineChart);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);

        userID = getIntent().getIntExtra("UserID", -1);

        setupChart();
        loadData();
    }

    private void setupChart() {
        lineChart.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        lineChart.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        lineChart.getViewport().setXAxisBoundsManual(true);
        lineChart.getViewport().setMinX(0);
        lineChart.getViewport().setMaxX(11);
        lineChart.getGridLabelRenderer().setHorizontalAxisTitle("Months");
        lineChart.getGridLabelRenderer().setVerticalAxisTitle("Views");
        lineChart.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        lineChart.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        lineChart.getGridLabelRenderer().setGridColor(Color.GRAY);
    }

    private List<String> getMonths() {
        List<String> months = new ArrayList<>();
        String[] monthNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        for (int i = 0; i < 12; i++) {
            months.add(monthNames[(currentMonth + i) % 12]);
        }
        return months;
    }

    private void loadData() {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.conClass();
        if (connection != null) {
            try {
                String query = "SELECT MONTH(GETDATE()) as CurrentMonth, MONTH(DATEADD(MONTH, number, GETDATE())) as Month, " +
                        "COALESCE(SUM(Views), 0) as TotalViews " +
                        "FROM master.dbo.spt_values " +
                        "LEFT JOIN Song ON MONTH(DATEADD(MONTH, number, GETDATE())) = MONTH(GETDATE()) AND ArtistID = ? " +
                        "WHERE type = 'P' AND number BETWEEN 0 AND 11 " +
                        "GROUP BY MONTH(DATEADD(MONTH, number, GETDATE())) " +
                        "ORDER BY MONTH(DATEADD(MONTH, number, GETDATE()))";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userID);
                ResultSet resultSet = preparedStatement.executeQuery();

                List<DataPoint> dataPoints = new ArrayList<>();
                int totalViews = 0;
                int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // Adding 1 because Calendar.MONTH is zero-based

                while (resultSet.next()) {
                    int month = resultSet.getInt("Month");
                    int views = resultSet.getInt("TotalViews");
                    totalViews += views;

                    // Adjust the x-value to match the chart's month order
                    double xValue = (month - currentMonth + 12) % 12;
                    dataPoints.add(new DataPoint(xValue, views));
                }

                // Convert the list to array and create the series for GraphView
                DataPoint[] dataPointsArray = dataPoints.toArray(new DataPoint[0]);
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPointsArray);
                series.setColor(Color.WHITE);
                series.setThickness(5);
                series.setDrawDataPoints(true);
                series.setDataPointsRadius(10);
                series.setAnimated(true);

                // Add the series to the graph
                lineChart.addSeries(series);

                long totalRevenue = totalViews * 300;
                tvTotalRevenue.setText(String.format("Số tiền đã thu được: %,d đ", totalRevenue));

                preparedStatement.close();
                resultSet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
