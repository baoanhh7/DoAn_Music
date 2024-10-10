package com.example.doan_music.activity.library;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.doan_music.LoadImage.LoadImageFromUrl;
import com.example.doan_music.LoadImage.LoadImageFromUrl;
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

public class PlaylistUserLoveActivity extends AppCompatActivity {
    ImageButton btnback, btnplay;
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
    private int IDPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_user_love);
        IDPlaylist = getIntent().getIntExtra("MaPlaylist", -1);
        addControls();
        loadData();
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
                            intent = new Intent(PlaylistUserLoveActivity.this, PlayMusicActivity.class);
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
//                        intent = new Intent(PlaylistUserLoveActivity.this, PlayMusicActivity.class);
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

    private void loadData() {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "SELECT * FROM Playlist_User_Song WHERE ID_Playlist_User = " + IDPlaylist;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                arr.clear();
                arr1.clear();
                while (resultSet.next()) {
                    Integer idsong = resultSet.getInt(2);
                    String query1 = "SELECT * FROM Song WHERE SongID = " + idsong;
                    Statement smt1 = connection.createStatement();
                    ResultSet resultSet1 = smt1.executeQuery(query1);
                    while (resultSet1.next()) {
                        Integer id = resultSet1.getInt(1);
                        String ten = resultSet1.getString(2);
                        String linkImage = resultSet1.getString(3);
                        byte[] img = new LoadImageFromUrl(linkImage).getImageBytes();
                        ThuVien thuVien = new ThuVien(img, ten);
                        arr1.add(id);
                        arr.add(thuVien);
                    }
                }
                connection.close();
                thuVienAlbumAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
//        database = openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
//        Cursor cursor = database.rawQuery("select * from Playlist_User_Song", null);
//        arr.clear();
//        while (cursor.moveToNext()) {
//            Integer idplaylist = cursor.getInt(1);
//            Integer idsong = cursor.getInt(3);
//            if (IDPlaylist.equals(idplaylist)) {
//                Cursor cursor1 = database.rawQuery("select * from Songs", null);
//
//                while (cursor1.moveToNext()) {
//                    Integer id = cursor1.getInt(0);
//                    String ten = cursor1.getString(2);
//                    byte[] img = cursor1.getBlob(3);
//                    if (idsong.equals(id)) {
//                        ThuVien thuVien = new ThuVien(img, ten);
//                        arr1.add(id);
//                        arr.add(thuVien);
//                    }
//                }
//                cursor1.close();
//            }
//        }
//        thuVienAlbumAdapter.notifyDataSetChanged();
//        cursor.close();
    }

    private void addControls() {
        btnback = findViewById(R.id.btn_back_playlistuser);
        btnplay = findViewById(R.id.btn_playplaylistuser);
        rcv = findViewById(R.id.rcv_playlistuser);
        arr = new ArrayList<>();
        thuVienAlbumAdapter = new ThuVienAlbumAdapter(PlaylistUserLoveActivity.this, arr);
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
                            intent = new Intent(PlaylistUserLoveActivity.this, PlayMusicActivity.class);
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
//                        intent = new Intent(PlaylistUserLoveActivity.this, PlayMusicActivity.class);
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
}