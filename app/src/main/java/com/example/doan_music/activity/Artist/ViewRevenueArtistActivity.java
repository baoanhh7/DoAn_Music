package com.example.doan_music.activity.Artist;

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
        // Thiết lập giới hạn thủ công cho trục y
        lineChart.getViewport().setYAxisBoundsManual(true);
        lineChart.getViewport().setMinY(0);  // Ví dụ: Bắt đầu từ 0 lượt xem
        lineChart.getViewport().setMaxY(150);  // Ví dụ: Giới hạn đến 150 lượt xem, tùy chỉnh theo dữ liệu của bạn
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
                String query = "SELECT MONTH(DATEADD(MONTH, number, GETDATE())) AS Month, " +
                        "COALESCE(SUM(Views), 0) AS TotalViews " +
                        "FROM master.dbo.spt_values " +
                        "LEFT JOIN Song ON Song.ArtistID = ? " +
                        "WHERE type = 'P' AND number BETWEEN 0 AND 11 " +
                        "GROUP BY MONTH(DATEADD(MONTH, number, GETDATE())) " +
                        "ORDER BY MONTH(DATEADD(MONTH, number, GETDATE()))";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userID);  // Đặt ArtistID
                ResultSet resultSet = preparedStatement.executeQuery();

                List<DataPoint> dataPoints = new ArrayList<>();
                int totalViews = 0;

                while (resultSet.next()) {
                    int month = resultSet.getInt("Month");
                    int views = resultSet.getInt("TotalViews");

                    Log.d("DBResult", "Month: " + month + ", Views: " + views);  // Log kết quả từ DB

                    totalViews += views;

                    // Sử dụng trực tiếp giá trị tháng (1-12) làm xValue
                    double xValue = month;
                    dataPoints.add(new DataPoint(xValue, views));
                }

                // Chuyển đổi danh sách thành mảng và tạo series cho biểu đồ
                DataPoint[] dataPointsArray = dataPoints.toArray(new DataPoint[0]);
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPointsArray);
                series.setColor(Color.WHITE);
                series.setThickness(5);
                series.setDrawDataPoints(true);
                series.setDataPointsRadius(10);
                series.setAnimated(true);

                // Cấu hình biểu đồ
                lineChart.getViewport().setXAxisBoundsManual(false);  // Tắt giới hạn thủ công trục X
                lineChart.getViewport().setYAxisBoundsManual(false);  // Tắt giới hạn thủ công trục Y
                lineChart.addSeries(series);
                lineChart.onDataChanged(true, true);  // Cập nhật dữ liệu biểu đồ

                long totalRevenue = totalViews * 300;  // Tính tổng doanh thu
                tvTotalRevenue.setText(String.format("Số tiền đã thu được: %,d đ", totalRevenue));

                preparedStatement.close();
                resultSet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
