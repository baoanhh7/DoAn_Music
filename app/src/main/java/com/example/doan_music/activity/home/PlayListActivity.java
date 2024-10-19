package com.example.doan_music.activity.home;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.adapter.home.PlayListAdapter;
import com.example.doan_music.data.DbHelper;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.m_interface.OnItemClickListener;
import com.example.doan_music.model.Playlists;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlayListActivity extends AppCompatActivity {
    RecyclerView rcv_playlist;
    PlayListAdapter playListAdapter;
    Button btn_back;
    TextView txt_playlist;
    Playlists playlists;
    DbHelper dbHelper;
    SQLiteDatabase database = null;

    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        addControls();
        addEvents();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_playlist.setLayoutManager(linearLayoutManager);

    }

    private List<Playlists> getPlaylists() {
        List<Playlists> list = new ArrayList<>();

        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "SELECT * FROM Playlist";
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                while (resultSet.next()) {
                    Integer PlaylistID = resultSet.getInt(1);
                    String PlaylistName = resultSet.getString(2);
                    String PlaylistImage = resultSet.getString(3);
                    // Chuyển đổi linkImage thành byte[]
                    byte[] img = getImageBytesFromURL(PlaylistImage);
                    Playlists playlists = new Playlists(PlaylistID, PlaylistName, img);
                    list.add(playlists);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }

        return list;
    }

    private void addEvents() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        playListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                ConnectionClass sql = new ConnectionClass();
                Connection connection = sql.conClass();
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;

                if (connection != null) {
                    try {
                        // Câu truy vấn để lấy tất cả các playlist
                        String query = "SELECT PlaylistID, PlaylistName FROM Playlist WHERE PlaylistName = ?";
                        preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setString(1, data);
                        resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            int id = resultSet.getInt(1);

                            // Chuyển sang màn hình danh sách bài hát trong playlist
                            Intent intent = new Intent(PlayListActivity.this, SongsPlayListActivity.class);
                            intent.putExtra("PlayListID", id);
                            startActivity(intent);
                        }

                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                    } finally {
                        // Đảm bảo đóng tài nguyên
                        try {
                            if (resultSet != null) resultSet.close();
                            if (preparedStatement != null) preparedStatement.close();
                            if (connection != null) connection.close();
                        } catch (Exception e) {
                            Log.e("Error closing: ", e.getMessage());
                        }
                    }
                } else {
                    Log.e("Error: ", "Connection null");
                }
            }
        });

    }

    private void addControls() {
        rcv_playlist = findViewById(R.id.rcv_playlist);
        playListAdapter = new PlayListAdapter(this, getPlaylists());
        rcv_playlist.setAdapter(playListAdapter);

        txt_playlist = findViewById(R.id.txt_playlist);
        btn_back = findViewById(R.id.btn_back);

        Intent intent = getIntent();
        txt_playlist.setText(intent.getStringExtra("c"));
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