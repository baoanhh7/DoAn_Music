package com.example.doan_music.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.doan_music.R;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.fragment.drawer.AllSongs_Fragment;
import com.example.doan_music.fragment.main.Home_Fragment;
import com.example.doan_music.fragment.main.Library_Fragment;
import com.example.doan_music.fragment.main.Search_Fragment;
import com.example.doan_music.fragment.main.Spotify_Fragment;
import com.example.doan_music.loginPackage.UserActivity;
import com.example.doan_music.music.PlayMusicActivity;
import com.example.doan_music.offline.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    BottomNavigationView bottom_navigation;
    NavigationView navigationView;
    Integer maU;
    String tenU;
    ImageView mini_player_play_pause, mini_player_image;
    TextView mini_player_song_name, mini_player_artist_name;
    int userID;
    MediaPlayer myMusic;
    String songLink;
    int playbackTime;
    LinearLayout clickToSong;
    SharedPreferences sharedPreferences;
    ArrayList<Integer> arr = new ArrayList<>();
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myMusic = new MediaPlayer();
        addControls();
        // Khởi động ở trạng thái pause
        myMusic.pause();
        mini_player_play_pause.setImageResource(R.drawable.ic_play); // Đặt hình ảnh thành nút Play

        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", -1);  // Lấy userID
        Log.e("UserID", String.valueOf(userID));

        // Drawer
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        // Click
        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_home) {
                    replace(new Home_Fragment());
                } else if (itemId == R.id.menu_search) {
                    replace(new Search_Fragment());
                } else if (itemId == R.id.menu_library) {
                    replace(new Library_Fragment());
                } else if (itemId == R.id.menu_spotify) {
                    replace(new Spotify_Fragment());
                }
                return true;
            }
        });

        // Select item in Drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.allSongs) {
                    replace(new AllSongs_Fragment());
                } else if (id == R.id.logout) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Xác nhận đăng xuất");
                    builder.setMessage("Bạn có muốn đăng xuất không ?");

                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(MainActivity.this, UserActivity.class));
                            finish();
                        }
                    });
                    builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                } else if (id == R.id.home) {
                    replace(new Home_Fragment());
                }
                // Xử lý xong sẽ đóng Drawer
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        addEvents();
    }

    private void addEvents() {
        sharedPreferences = getSharedPreferences("musicData", MODE_PRIVATE);
        Integer songID = sharedPreferences.getInt("SongID", -1);

        mini_player_play_pause.setOnClickListener(v -> {
            // Bắt đầu hoặc tạm dừng phát nhạc
            if (myMusic.isPlaying()) {
                myMusic.pause(); // Dừng phát nhạc
                mini_player_play_pause.setImageResource(R.drawable.ic_play); // Đặt hình ảnh thành nút Play
            } else {
                myMusic.start(); // Phát nhạc
                mini_player_play_pause.setImageResource(R.drawable.ic_pause); // Đặt hình ảnh thành nút Pause
            }
        });

        clickToSong.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlayMusicActivity.class);
            intent.putExtra("SongID", songID);
            intent.putExtra("arrIDSongs", arr);
            startActivity(intent);
        });
    }

    public Integer getMyVariable() {
        return maU;
    }

    public String getName() {
        return tenU;
    }

    private void addControls() {
        bottom_navigation = findViewById(R.id.bottomNavigationView);
        replace(new Home_Fragment());

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_drawer);

        mini_player_play_pause = findViewById(R.id.mini_player_play_pause);
        mini_player_artist_name = findViewById(R.id.mini_player_artist_name);
        mini_player_song_name = findViewById(R.id.mini_player_song_name);
        mini_player_image = findViewById(R.id.mini_player_image);

        clickToSong = findViewById(R.id.clickToSong);

        // Lấy Intent đã được chuyển từ Login_userActivity
        Intent intent = getIntent();

        // Kiểm tra xem có dữ liệu "maU" được chuyển không
        if (intent.hasExtra("maU")) {
            // Lấy dữ liệu từ Intent
            maU = intent.getIntExtra("maU", 0);
            tenU = intent.getStringExtra("tenU");

        }

        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();  // Kết nối tới SQL Server

        if (connection != null) {
            try {
                // Truy vấn SQL để lấy tất cả các bài hát
                String query = "SELECT * FROM Song";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    int id = resultSet.getInt("SongID");  // Lấy ID bài hát

                    arr.add(id);
                }

                resultSet.close();
                statement.close();
                connection.close();  // Đóng kết nối

            } catch (SQLException e) {
                Log.e("SQL Error", e.getMessage());
            }
        } else {
            Log.e("Error", "Connection is null");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Lấy lại thông tin mới nhất từ SharedPreferences
        sharedPreferences = getSharedPreferences("musicData", MODE_PRIVATE);
        String songName = sharedPreferences.getString("SongName", "Unknown Song");
        String artistName = sharedPreferences.getString("ArtistName", "Unknown Artist");
//        playbackTime = sharedPreferences.getInt("playbackTime", 0); // Thời gian mặc định là 0
        Integer songID = sharedPreferences.getInt("SongID", -1);

        // Cập nhật giao diện mini player
        mini_player_song_name.setText(songName);
        mini_player_artist_name.setText(artistName);

        // Initialize the SQL Server connection using your custom ConnectionClass
        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();  // Assuming conClass() returns a Connection object

        if (connection != null) {
            try {
                // SQL query to get link song from the Songs table for a specific SongID
                String query = "SELECT LinkSong, SongImage FROM Song WHERE SongID = " + songID;
                Statement smt = connection.createStatement();
                ResultSet resultSet = smt.executeQuery(query);

                if (resultSet.next()) {
                    songLink = resultSet.getString("LinkSong");
                    String img = resultSet.getString("SongImage");

                    // Phát bài hát
                    if (myMusic == null) {
                        myMusic = new MediaPlayer();
                    } else {
                        myMusic.reset();  // Reset MediaPlayer nếu đã khởi tạo trước đó
                    }

                    myMusic.setDataSource(songLink);
                    myMusic.prepare();
                    myMusic.seekTo(playbackTime); // Đặt thời gian phát lại

                    // Lấy ảnh bài hát và cập nhật
                    byte[] imageBytes = getImageBytesFromURL(img);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    mini_player_image.setImageBitmap(bitmap);
                }
                resultSet.close();  // Đóng ResultSet
                smt.close();  // Đóng Statement
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection is null");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myMusic != null) {
            myMusic.release(); // Giải phóng tài nguyên
            myMusic = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myMusic != null && myMusic.isPlaying()) {
            // Lưu thời gian phát nhạc
//            SharedPreferences sharedPreferences = getSharedPreferences("musicData", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putInt("playbackTime", myMusic.getCurrentPosition());
//            editor.apply();

            myMusic.pause(); // Dừng phát nhạc
        }
    }

    // Nhấn nút back device để trở về(sử dụng nút trong device)
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    public void replace(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

    private byte[] getImageBytesFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (IOException e) {
            Log.e("Error: ", "Failed to load image from URL: " + e.getMessage());
            return null; // Hoặc có thể trả về mảng byte rỗng nếu muốn
        }
    }
}
