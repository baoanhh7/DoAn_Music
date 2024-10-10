package com.example.doan_music.activity.library;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.LoadImage.LoadImageFromUrl;
import com.example.doan_music.LoadImage.LoadImageTask;
import com.example.doan_music.R;
import com.example.doan_music.adapter.thuvien.ThuVienAlbumAdapter;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.m_interface.OnItemClickListener;
import com.example.doan_music.model.ThuVien;
import com.example.doan_music.music.PlayMusicActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ArtistSongActivity extends AppCompatActivity implements OnItemClickListener {
    ImageButton btnback, btnplay;
    ImageView imgHinh;
    RecyclerView rcv;
    ThuVienAlbumAdapter thuVienAlbumAdapter;
    ArrayList<ThuVien> arr;
    ArrayList<Integer> arr1 = new ArrayList<>();
    SQLiteDatabase database = null;
    Intent intent = null;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    private int IDArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ablum_song);
        IDArtist = getIntent().getIntExtra("MaArtist", -1);
        addControls();
        loadData();
        loadImgArtist();
        addEvents();
    }

    private void addEvents() {
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionClass sql = new ConnectionClass();
                connection = sql.conClass();
                Integer idSong = arr1.get(0);
                if (connection != null) {
                    try {
                        // Truy vấn SQL Server để lấy dữ liệu
                        query = "SELECT * FROM Song WHERE SongID = " + idSong;
                        smt = connection.createStatement();
                        resultSet = smt.executeQuery(query);

                        while (resultSet.next()) {
                            Integer Id = resultSet.getInt(1);
                            intent = new Intent(ArtistSongActivity.this, PlayMusicActivity.class);
                            intent.putExtra("SongID", Id);
                            intent.putExtra("arrIDSongs", arr1);
                            break;
                        }
                        connection.close();
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                    }
                } else {
                    Log.e("Error: ", "Connection null");
                }
//                database = openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
//                Cursor cursor = database.rawQuery("select * from Songs", null);
//                while (cursor.moveToNext()) {
//                    Integer Id = cursor.getInt(0);
//                    String ten = cursor.getString(2);
//                    if (idSong.equals(Id)) {
//                        intent = new Intent(ArtistSongActivity.this, PlayMusicActivity.class);
//                        intent.putExtra("SongID", Id);
//                        intent.putExtra("arrIDSongs", arr1);
//                        break;
//                    }
//                }
//                cursor.close();
//                startActivity(intent);
            }
        });
    }

    private void loadImgArtist() {
        // Kết nối đến SQL Server
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "SELECT * FROM Artist WHERE ArtistID = " + IDArtist;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                while (resultSet.next()) {
                    // Lấy URL từ cột tương ứng (ví dụ: cột thứ 3)
                    String imgUrl = resultSet.getString(3);
//                    if (resultSet.getInt(1) == IDArtist) {
                    // Tải ảnh từ URL và hiển thị
                    new LoadImageTask(imgHinh).execute(imgUrl);
//                    }
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void loadData() {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "SELECT * FROM Song WHERE ArtistID = " + IDArtist;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                arr.clear();
                arr1.clear();
                while (resultSet.next()) {
                    Integer id = resultSet.getInt(1);          // Cột 0: id
                    String ten = resultSet.getString(2);      // Cột 2: tên bài hát
                    String linkImage = resultSet.getString(3); // Cột chứa URL ảnh

                    // Tải ảnh từ URL và chuyển thành byte[]
                    byte[] img = new LoadImageFromUrl(linkImage).getImageBytes();
                    ThuVien thuVien = new ThuVien(img, ten);
                    arr1.add(id);
                    arr.add(thuVien);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void addControls() {
        btnback = findViewById(R.id.btn_back_album);
        btnplay = findViewById(R.id.btn_playalbum);
        imgHinh = findViewById(R.id.img_song_album);
        rcv = findViewById(R.id.rcv_songlalbum);
        arr = new ArrayList<>();
        thuVienAlbumAdapter = new ThuVienAlbumAdapter(ArtistSongActivity.this, arr);
        thuVienAlbumAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                ConnectionClass sql = new ConnectionClass();
                connection = sql.conClass();
                if (connection != null) {
                    try {
                        // Truy vấn SQL Server để lấy dữ liệu
                        query = "SELECT * FROM Song WHERE SongName = ?";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, data);
                        resultSet = statement.executeQuery();

                        while (resultSet.next()) {
                            Integer Id = resultSet.getInt(1);
                            intent = new Intent(ArtistSongActivity.this, PlayMusicActivity.class);
                            intent.putExtra("SongID", Id);
                            intent.putExtra("arrIDSongs", arr1);
                            break;
                        }
                        connection.close();
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                    }
                } else {
                    Log.e("Error: ", "Connection null");
                }
//                database = openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
//                Cursor cursor = database.rawQuery("select * from Songs", null);
//                while (cursor.moveToNext()) {
//
//                    Integer Id = cursor.getInt(0);
//                    String ten = cursor.getString(2);
//                    if (data.equals(ten)) {
//                        intent = new Intent(ArtistSongActivity.this, PlayMusicActivity.class);
//                        intent.putExtra("SongID", Id);
//                        intent.putExtra("arrIDSongs", arr1);
//                        break;
//                    }
//                }
//                cursor.close();
//                startActivity(intent);
            }
        });
        rcv.setAdapter(thuVienAlbumAdapter);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        rcv.setLayoutManager(linearLayout);
    }


    @Override
    public void onItemClick(String data) {

    }
}