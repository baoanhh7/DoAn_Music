package com.example.doan_music.activity.Artist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_music.R;

public class ViewRevenueArtistActivity extends AppCompatActivity {
    private int userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_revenue_artist);

        Intent intent = getIntent();
        userID = intent.getIntExtra("UserID", -1);

        if (userID == -1) {
            // Xử lý lỗi: không có userID hợp lệ
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }
}