package com.example.doan_music.activity.Artist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.doan_music.R;
import com.example.doan_music.activity.Artist.fragment.AccountFragment;
import com.example.doan_music.activity.Artist.fragment.ArtistMenuDialogFragment;
import com.example.doan_music.activity.Artist.fragment.ListSongArtistFragment;
import com.example.doan_music.activity.Artist.fragment.SearchArtistFragment;
import com.example.doan_music.database.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeArtistActivity extends AppCompatActivity {


    ImageButton btnHome, btnSearch, btnAccount, btnMenu;
    TextView userArtist_Name;
    Connection connection;
    String query;
    ResultSet resultSet;
    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_artist);

        // Ánh xạ các nút ImageButton
        btnHome = findViewById(R.id.btn_home);
        btnSearch = findViewById(R.id.btn_search);
        btnAccount = findViewById(R.id.btn_account);
        userArtist_Name = findViewById(R.id.UserArtist_Name);
        btnMenu = findViewById(R.id.btn_menu_artist);


        Intent intent = getIntent();
        userID = intent.getIntExtra("UserID", -1);
        // Lấy username từ cơ sở dữ liệu
        if (userID != -1) {
            getUsernameByUserId(userID);
        }
        loadDefaultFragment();
        // Xử lý sự kiện click từng nút
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeIcon(R.id.btn_home);
                loadDefaultFragment();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeIcon(R.id.btn_search);
                loadFragment(new SearchArtistFragment());
            }
        });

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeIcon(R.id.btn_account);
                loadFragment(new AccountFragment());
            }
        });

        // Xử lý sự kiện click cho nút Menu
//        btnMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPopupMenu(v);
//            }
//        });
        // Xử lý sự kiện click cho nút Menu
        btnMenu.setOnClickListener(v -> showCustomMenuDialog());
    }

    private void showCustomMenuDialog() {
        ArtistMenuDialogFragment dialogFragment = ArtistMenuDialogFragment.newInstance(userID);
        dialogFragment.show(getSupportFragmentManager(), "ArtistMenuDialog");
    }
    // Hàm hiển thị PopupMenu
//    private void showPopupMenu(View view) {
//        PopupMenu popupMenu = new PopupMenu(this, view);
//        popupMenu.getMenuInflater().inflate(R.menu.artist_menu, popupMenu.getMenu());
//
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int id = item.getItemId();
//
//                // Sử dụng if-else thay vì switch-case
//                if (id == R.id.menu_add_song) {
//                    // Xử lý thêm bài hát
//                    Toast.makeText(HomeArtistActivity.this, "Thêm bài hát", Toast.LENGTH_SHORT).show();
//                    return true;
//                } else if (id == R.id.menu_update_album) {
//                    // Xử lý cập nhật album
//                    Toast.makeText(HomeArtistActivity.this, "Cập nhật album", Toast.LENGTH_SHORT).show();
//                    return true;
//                } else if (id == R.id.menu_view_revenue) {
//                    // Xử lý xem doanh thu
//                    Toast.makeText(HomeArtistActivity.this, "Xem doanh thu", Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        popupMenu.show();
//    }

    private void getUsernameByUserId(int userID) {
        connection = new ConnectionClass().conClass();
        if (connection != null) {
            try {
                // SQL query để lấy username
                query = "SELECT Username FROM Users WHERE UserID = ?";
                PreparedStatement smt = connection.prepareStatement(query);
                smt.setInt(1, userID);
                resultSet = smt.executeQuery();

                if (resultSet.next()) {
                    String username = resultSet.getString("Username");
                    // Gán username vào TextView
                    userArtist_Name.setText(username);
                } else {
                    // Xử lý trường hợp không tìm thấy người dùng
                    userArtist_Name.setText("User not found");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Xử lý lỗi
                userArtist_Name.setText("Error retrieving username");
            }
        } else {
            // Kết nối cơ sở dữ liệu không thành công
            userArtist_Name.setText("Database connection failed");
        }
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

    public void loadFragment(Fragment fragment) {
        // Replace the current fragment with the new fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void loadDefaultFragment() {
        Fragment defaultFragment = new ListSongArtistFragment();
        loadFragment(defaultFragment);
    }

    public int getUserID() {
        return userID;
    }
}