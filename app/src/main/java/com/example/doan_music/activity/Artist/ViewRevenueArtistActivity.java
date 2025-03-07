package com.example.doan_music.activity.Artist;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan_music.R;
import com.example.doan_music.designPattern.MVC.Controller.RevenueController;
import com.example.doan_music.designPattern.MVC.Model.RevenueModel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class ViewRevenueArtistActivity extends AppCompatActivity {
    private GraphView lineChart;
    private TextView tvTotalRevenue;
    private int userID;
    private RevenueController revenueController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_revenue_artist);

        lineChart = findViewById(R.id.lineChart);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        revenueController = new RevenueController();

        userID = getIntent().getIntExtra("UserID", -1);

        setupChart();
        loadData();
    }

    private void setupChart() {
        lineChart.getViewport().setScalable(true);
        lineChart.getViewport().setScalableY(true);
        lineChart.getViewport().setXAxisBoundsManual(true);
        lineChart.getViewport().setMinX(0);
        lineChart.getViewport().setMaxX(12);
        lineChart.getViewport().setYAxisBoundsManual(true);
        lineChart.getViewport().setMinY(0);
        lineChart.getViewport().setMaxY(150);
        lineChart.getGridLabelRenderer().setHorizontalAxisTitle("Months");
        lineChart.getGridLabelRenderer().setVerticalAxisTitle("Views");
        lineChart.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        lineChart.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        lineChart.getGridLabelRenderer().setGridColor(Color.GRAY);
    }

    private void loadData() {
        List<RevenueModel> revenueData = revenueController.getRevenueData(userID);

        if (revenueData.isEmpty()) {
            Log.e("ViewRevenueArtistActivity", "No data found!");
            return;
        }

        List<DataPoint> dataPoints = new ArrayList<>();
        int totalViews = 0;

        for (RevenueModel revenue : revenueData) {
            dataPoints.add(new DataPoint(revenue.getMonth(), revenue.getTotalViews()));
            totalViews += revenue.getTotalViews();
        }

        DataPoint[] dataPointsArray = dataPoints.toArray(new DataPoint[0]);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPointsArray);
        series.setColor(Color.WHITE);
        series.setThickness(5);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setAnimated(true);

        lineChart.addSeries(series);

        long totalRevenue = totalViews * 300;
        tvTotalRevenue.setText(String.format("Số tiền đã thu được: %,d đ", totalRevenue));
    }
}
