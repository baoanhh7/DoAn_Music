package com.example.doan_music.activity.Artist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_music.R;

public class HomeActivity extends AppCompatActivity {

    ImageButton btnHome, btnSearch, btnAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);  // Giả sử tên layout của bạn là activity_home

        // Ánh xạ các nút ImageButton
        btnHome = findViewById(R.id.btn_home);
        btnSearch = findViewById(R.id.btn_search);
        btnAccount = findViewById(R.id.btn_account);

        // Xử lý sự kiện click từng nút
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeIcon(R.id.btn_home);
                // Chuyển tới trang Home (giữ nguyên Activity này)
                // Nếu HomeActivity là trang Home, thì có thể không cần xử lý chuyển activity ở đây
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeIcon(R.id.btn_search);
                // Chuyển sang trang SearchActivity
                // Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                // startActivity(intent);
            }
        });

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeIcon(R.id.btn_account);
                // Chuyển sang trang AccountActivity
                // Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
                // startActivity(intent);
            }
        });
    }

    // Hàm để thay đổi icon của nút được chọn
    private void changeIcon(int selectedButtonId) {
        // Reset icons to default
        btnHome.setImageResource(R.drawable.home);
        btnSearch.setImageResource(R.drawable.search);
        btnAccount.setImageResource(R.drawable.user);

        // Change icon basedon selection
        if (selectedButtonId == R.id.btn_home) {
            btnHome.setImageResource(R.drawable.home_choise);
        } else if (selectedButtonId == R.id.btn_search) {
            btnSearch.setImageResource(R.drawable.search_choise);
        } else if (selectedButtonId == R.id.btn_account) {
            btnAccount.setImageResource(R.drawable.user_choise);
        }
    }
}
