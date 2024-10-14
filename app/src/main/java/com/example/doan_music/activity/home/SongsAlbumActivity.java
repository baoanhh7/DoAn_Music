package com.example.doan_music.activity.home;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.adapter.home.SongAdapter;
import com.example.doan_music.data.DbHelper;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.m_interface.OnItemClickListener;
import com.example.doan_music.model.Song;
import com.example.doan_music.music.PlayMusicActivity;

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

public class SongsAlbumActivity extends AppCompatActivity {

    RecyclerView rcv_songlist;
    SongAdapter songAdapter;
    ImageButton btn_back, btn_play;
    ImageView img_songlist;
    TextView txt_songlist, txt_album_view;
    Intent intent = null;
    ArrayList<Integer> arr = new ArrayList<>();
    SQLiteDatabase database = null;
    DbHelper dbHelper;

    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_album);
        addControls();

        loadInfoAlbum();

        addEvents();
    }

    private void loadInfoAlbum() {
        int albumID = getIntent().getIntExtra("albumID", -1);

        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "SELECT * FROM Album WHERE AlbumID = " + albumID;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                while (resultSet.next()) {
                    String AlbumName = resultSet.getString(2);
                    String AlbumImage = resultSet.getString(3);
                    // Chuyển đổi linkImage thành byte[]
                    byte[] imageBytes = getImageBytesFromURL(AlbumImage);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                    txt_songlist.setText(AlbumName);
                    img_songlist.setImageBitmap(bitmap);
                    txt_album_view.setText("0");
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
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer idSong = arr.get(0);
                ConnectionClass sql = new ConnectionClass();
                connection = sql.conClass();
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;

                if (connection != null) {
                    try {
                        // Sử dụng PreparedStatement để tránh SQL Injection
                        String query = "SELECT * FROM Song WHERE SongID = ?";
                        preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setInt(1, idSong);
                        resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            Integer songId = resultSet.getInt("SongID");

                            // Chuyển qua Activity PlayMusicActivity trên Main Thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SongsAlbumActivity.this, PlayMusicActivity.class);
                                    intent.putExtra("SongID", songId);
                                    intent.putExtra("arrIDSongs", arr);
                                    startActivity(intent);
                                }
                            });
                        }

                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                    } finally {
                        // Đảm bảo đóng các tài nguyên
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

        songAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                ConnectionClass sql = new ConnectionClass();  // Assuming ConnectionClass is your helper for SQL Server connections
                Connection connection = sql.conClass();       // Establish connection to SQL Server

                if (connection != null) {
                    try {
                        // Use a PreparedStatement to safely fetch the song by name
                        String query = "SELECT * FROM Song WHERE SongName = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setString(1, data);  // Bind the song name to the query

                        ResultSet resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            int id = resultSet.getInt(1);   // Assuming SongID is the primary key
                            String songName = resultSet.getString(2);
                            int view = resultSet.getInt(8);

                            // Update the view count
                            view++;

                            // Prepare the update statement to increment the view count
                            String updateQuery = "UPDATE Song SET Views = ? WHERE SongID = ?";
                            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                            updateStatement.setInt(1, view);
                            updateStatement.setInt(2, id);
                            updateStatement.executeUpdate();

                            // Start the PlayMusicActivity and pass the necessary data
                            Intent intent = new Intent(SongsAlbumActivity.this, PlayMusicActivity.class);
                            intent.putExtra("SongID", id);
                            intent.putExtra("arrIDSongs", arr);   // Assuming arr is a list of song IDs

                            startActivity(intent);
                        }

                        // Close the ResultSet and the PreparedStatements
                        resultSet.close();
                        preparedStatement.close();
                        connection.close();  // Close the SQL connection
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                } else {
                    Log.e("Error", "Connection to SQL Server failed");
                }
            }
        });
    }

    private List<Song> getList() {
        List<Song> list = new ArrayList<>();
        int albumID = getIntent().getIntExtra("albumID", -1);

        // Initialize the SQL Server connection using your custom ConnectionClass
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();  // Assuming conClass() returns a Connection object

        if (connection != null) {
            try {
                // SQL query to get songs from the Songs table for a specific album
                query = "SELECT * FROM Song WHERE AlbumID = " + albumID;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                // Iterate through the result set
                while (resultSet.next()) {
                    Integer id = resultSet.getInt(1);     // Song ID
                    Integer AlbumID = resultSet.getInt(4); // Album ID
                    String name = resultSet.getString(2);   // Song name

                    String image = resultSet.getString(3);  // Song image
                    byte[] img = getImageBytesFromURL(image);    // Image bytes (assuming stored as a blob)
                    int view = resultSet.getInt(8);        // Number of views

                    // Create a new Song object and add it to the list
                    Song song = new Song(id, name, img, AlbumID, view);
                    arr.add(id);  // Add the song ID to the arr list (if needed)
                    list.add(song);  // Add the song to the list
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection is null");
        }

        return list;
    }

    private void addControls() {
        rcv_songlist = findViewById(R.id.rcv_songlist);

        songAdapter = new SongAdapter(this, getList());
        rcv_songlist.setAdapter(songAdapter);

        rcv_songlist.setLayoutManager(new LinearLayoutManager(this));

        img_songlist = findViewById(R.id.img_songlist);
        txt_songlist = findViewById(R.id.txt_songlist);
        txt_album_view = findViewById(R.id.txt_album_view);

        btn_back = findViewById(R.id.btn_back);
        btn_play = findViewById(R.id.btn_play);
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