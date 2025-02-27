package com.example.doan_music.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
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
import androidx.media3.common.BuildConfig;

import com.example.doan_music.R;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.fragment.drawer.AllSongs_Fragment;
import com.example.doan_music.fragment.drawer.SongHistoryFragment;
import com.example.doan_music.fragment.main.Home_Fragment;
import com.example.doan_music.fragment.main.Library_Fragment;
import com.example.doan_music.fragment.main.Search_Fragment;
import com.example.doan_music.fragment.main.Spotify_Fragment;
import com.example.doan_music.loginPackage.UserActivity;
import com.example.doan_music.music.PlayMusicActivity;
import com.example.doan_music.offline.database.DatabaseHelper;
import com.example.doan_music.offline.model.UserOffline;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    BottomNavigationView bottom_navigation;
    NavigationView navigationView;
    Integer maU;
    String tenU;
    ImageView mini_player_play_pause, mini_player_image;
    TextView mini_player_song_name, mini_player_artist_name;
    int userID, userIDOFF;
    MediaPlayer myMusic;
    String songLink;
    int playbackTime;
    LinearLayout clickToSong;
    SharedPreferences sharedPreferences;
    ArrayList<Integer> arr = new ArrayList<>();
    DatabaseHelper databaseHelper;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    String Role;
    String UserName, Password;
    SQLiteDatabase database = null;
    UserOffline userOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
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

//        loadRoleUser(userID);
//
////        loadDataOff(userID);
//        databaseHelper = new DatabaseHelper(this);
//        database =  databaseHelper.getWritableDatabase();
//        databaseHelper.onUpgrade(database,2,1);
//        if (Role.equalsIgnoreCase("premium") && loadDataOff(userID)) {
//            saveDataOntoOff(userID);
//            databaseHelper.getReadableDatabase();
//            databaseHelper = new DatabaseHelper(this);
//            database = databaseHelper.getWritableDatabase();
//            ContentValues values = new ContentValues();
//            values.put("id", userID);
//            values.put("username", userOffline.getUsername());
//            values.put("password", userOffline.getPassword());
//            values.put("is_premium", userOffline.isPremium() ? 1 : 0);
//            // Chuyển long thành định dạng ngày trước khi lưu
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            String formattedDate = dateFormat.format(new Date(userOffline.getPremiumExpireDate()));
//            values.put("premium_expire_date", formattedDate);
////            values.put("premium_expire_date",userOffline.getPremiumExpireDate());
//            database.insert("users", null, values);
//        }
//        Log.e("loaddataOff", String.valueOf(loadDataOff(userID)));
//        if (Role.equalsIgnoreCase("member") && !loadDataOff(userID)) {
//
//            databaseHelper = new DatabaseHelper(this);
//            SQLiteDatabase db = databaseHelper.getWritableDatabase();
//            ContentValues values = new ContentValues();
//            values.put("is_premium", 0); // Cập nhật is_premium thành 0
//            // Cập nhật bản ghi với userID tương ứng
//            int rowsAffected = db.update("users", values, "id = ?", new String[]{String.valueOf(userID)});
//
//            // Kiểm tra cập nhật thành công
//            if (rowsAffected > 0) {
//                // Thành công
//                Log.d("DatabaseUpdate", "Cập nhật thành công: " + rowsAffected + " bản ghi");
//            } else {
//                // Không có bản ghi nào được cập nhật
//                Log.d("DatabaseUpdate", "Cập nhật không thành công");
//            }
//        }
//        if (Role.equalsIgnoreCase("premium") && !loadDataOff(userID)) {
//
//            databaseHelper = new DatabaseHelper(this);
//            SQLiteDatabase db = databaseHelper.getWritableDatabase();
//            ContentValues values = new ContentValues();
//            values.put("is_premium", 1); // Cập nhật is_premium thành 0
//            // Cập nhật bản ghi với userID tương ứng
//            int rowsAffected = db.update("users", values, "id = ?", new String[]{String.valueOf(userID)});
//
//            // Kiểm tra cập nhật thành công
//            if (rowsAffected > 0) {
//                // Thành công
//                Log.d("DatabaseUpdate", "Cập nhật thành công: " + rowsAffected + " bản ghi");
//            } else {
//                // Không có bản ghi nào được cập nhật
//                Log.d("DatabaseUpdate", "Cập nhật không thành công");
//            }
//        }
        // Drawer
        // Chuyển tác vụ nặng sang background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            loadRoleUser(userID);
            databaseHelper = new DatabaseHelper(this);
            database = databaseHelper.getWritableDatabase();

            boolean isUserOffline = loadDataOff(userID);

            if (Role.equalsIgnoreCase("premium") && isUserOffline) {
                saveDataOntoOff(userID);
                ContentValues values = new ContentValues();
                values.put("id", userID);
                values.put("username", userOffline.getUsername());
                values.put("password", userOffline.getPassword());
                values.put("is_premium", userOffline.isPremium() ? 1 : 0);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = dateFormat.format(new Date(userOffline.getPremiumExpireDate()));
                values.put("premium_expire_date", formattedDate);
                database.insert("users", null, values);
            } else if (Role.equalsIgnoreCase("member") && !isUserOffline) {
                ContentValues values = new ContentValues();
                values.put("is_premium", 0);
                database.update("users", values, "id = ?", new String[]{String.valueOf(userID)});
            } else if (Role.equalsIgnoreCase("premium") && !isUserOffline) {
                ContentValues values = new ContentValues();
                values.put("is_premium", 1);
                database.update("users", values, "id = ?", new String[]{String.valueOf(userID)});
            }

            executor.shutdown();
        });
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
                } else if (id == R.id.songHistory) {
                    replace(new SongHistoryFragment());
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

    private boolean loadDataOff(int userID) {
        database = openOrCreateDatabase("music_db", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select * from users", null);
        while (cursor.moveToNext()) {
            userIDOFF = cursor.getInt(0);
            if (userID == userIDOFF) {
                return false;
            }
        }
        cursor.close();
        return true;
    }

    private void saveDataOntoOff(int userID) {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "SELECT Username,Password FROM Users WHERE UserID = " + userID;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                if (resultSet.next()) {
                    UserName = resultSet.getString(1);
                    Password = resultSet.getString(2);
                }
                query = "SELECT EndDate FROM HoaDon_Admin WHERE UserID = " + userID;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                if (resultSet.next()) {
                    Date endDate = resultSet.getDate(1); // Lấy kiểu Date từ CSDL
                    long endDateLong = endDate.getTime(); // Chuyển đổi sang Unix timestamp
                    userOffline = new UserOffline(UserName, Password, true, endDateLong);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void loadRoleUser(int userID) {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "SELECT * FROM Users WHERE UserID = " + userID;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                if (resultSet.next()) {
                    Role = resultSet.getString(5);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }

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

        // Chuyển tác vụ nặng sang background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                ConnectionClass sql = new ConnectionClass();
                Connection connection = sql.conClass();

                if (connection != null) {
                    String query = "SELECT LinkSong, SongImage FROM Song WHERE SongID = " + songID;
                    Statement smt = connection.createStatement();
                    ResultSet resultSet = smt.executeQuery(query);

                    if (resultSet.next()) {
                        songLink = resultSet.getString("LinkSong");
                        String img = resultSet.getString("SongImage");

                        // Tải ảnh từ URL
                        byte[] imageBytes = getImageBytesFromURL(img);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        // Chuẩn bị MediaPlayer
                        if (myMusic == null) {
                            myMusic = new MediaPlayer();
                        } else {
                            myMusic.reset();
                        }
                        myMusic.setDataSource(songLink);
                        myMusic.prepare(); // Chuẩn bị trong background thread

                        // Cập nhật UI trên main thread
                        runOnUiThread(() -> {
                            mini_player_image.setImageBitmap(bitmap);
                            mini_player_play_pause.setImageResource(myMusic.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
                        });

                        resultSet.close();
                        smt.close();
                        connection.close();
                    }
                } else {
                    Log.e("Error: ", "Connection is null");
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            } finally {
                executor.shutdown(); // Đóng executor sau khi hoàn thành
            }
        });
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
            myMusic.pause();
            mini_player_play_pause.setImageResource(R.drawable.ic_play);
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
//            URL url = new URL(imageUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap bitmap = BitmapFactory.decodeStream(input);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            return stream.toByteArray();
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                stream.write(buffer, 0, bytesRead);
            }
            input.close();
            connection.disconnect();
            return stream.toByteArray();
        } catch (IOException e) {
            Log.e("Error: ", "Failed to load image from URL: " + e.getMessage());
            return null; // Hoặc có thể trả về mảng byte rỗng nếu muốn
        }
    }
}
