package com.example.doan_music.activity.home;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.doan_music.adapter.home.SongsPlayListAdapter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SongsPlayListActivity extends AppCompatActivity {
    RecyclerView rcv_songPlayList;
    SongsPlayListAdapter songsPlayListAdapter;
    ImageButton btn_back, btn_play;
    ImageView img_songPlayList;
    TextView txt_songPlayList;
    Intent intent = null;
    DbHelper dbHelper;
    SQLiteDatabase database = null;
    ArrayList<Integer> arr = new ArrayList<>();
    List<Song> list = new ArrayList<>();

    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_songs_play_list);

        addControls();

        loadInfoPlayList();

        addEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        createData();
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
                        String query = "SELECT * FROM PlayList WHERE SongID = ?";
                        preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setInt(1, idSong);
                        resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            Integer songId = resultSet.getInt("SongID");

                            // Chuyển qua Activity PlayMusicActivity trên Main Thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SongsPlayListActivity.this, PlayMusicActivity.class);
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

        songsPlayListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                ConnectionClass sql = new ConnectionClass();
                Connection connection = sql.conClass();
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;

                if (connection != null) {
                    try {
                        // Truy vấn để lấy thông tin bài hát theo tên
                        String query = "SELECT SongID, Views FROM Song WHERE SongName = ?";
                        preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setString(1, data);
                        resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            int id = resultSet.getInt(1);
                            int view = resultSet.getInt(8);

                            // Tăng số lần xem
                            view++;

                            // Cập nhật số lần xem trong cơ sở dữ liệu
                            String updateQuery = "UPDATE Songs SET Views = ? WHERE SongID = ?";
                            preparedStatement = connection.prepareStatement(updateQuery);
                            preparedStatement.setInt(1, view);
                            preparedStatement.setInt(2, id);
                            preparedStatement.executeUpdate();

                            // Chuyển sang màn hình phát nhạc
                            Intent intent = new Intent(SongsPlayListActivity.this, PlayMusicActivity.class);
                            intent.putExtra("SongID", id);
                            intent.putExtra("arrIDSongs", arr);
                            startActivity(intent);
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

    }

    private void loadInfoPlayList() {
        int albumID = getIntent().getIntExtra("PlayListID", -1);

        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        if (connection != null) {
            try {
                // Sử dụng PreparedStatement để thực hiện truy vấn SQL Server
                String query = "SELECT * FROM PlayList WHERE PlayListID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, albumID);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String name = resultSet.getString(2);
                    byte[] img = resultSet.getBytes(3);

                    // Chuyển đổi byte[] thành Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);

                    // Cập nhật UI ngay lập tức trên Main Thread (không cần runOnUiThread)
                    txt_songPlayList.setText(name);
                    img_songPlayList.setImageBitmap(bitmap);
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


    private void createData() {
        int playListID = getIntent().getIntExtra("PlayListID", -1);

        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        if (connection != null) {
            try {
                // Câu truy vấn để lấy tất cả các bài hát có PlayListID phù hợp
                String query = "SELECT s.SongID, s.SongName, s.SongImage, s.Views" +
                        "FROM Song s " +
                        "INNER JOIN Playlist_Song ps ON s.SongID = ps.SongID " +
                        "WHERE ps.PlaylistID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, playListID);

                resultSet = preparedStatement.executeQuery();
                list.clear(); // Xóa danh sách cũ để thêm dữ liệu mới

                while (resultSet.next()) {
                    Integer id = resultSet.getInt(1);
                    String name = resultSet.getString(2);
                    String img = resultSet.getString(3);
                    byte[] imgBytes = getImageBytesFromURL(img);
                    int view = resultSet.getInt(8);

                    // Tạo đối tượng Song và thêm vào danh sách
                    Song song = new Song(id, name, playListID, imgBytes, img, view);
                    arr.add(id);
                    list.add(song);
                }

                songsPlayListAdapter.notifyDataSetChanged();

                // Sắp xếp danh sách theo thứ tự giảm dần của trường view
                Collections.sort(list, new Comparator<Song>() {
                    @Override
                    public int compare(Song s1, Song s2) {
                        return Integer.compare(s2.getView(), s1.getView());
                    }
                });

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

    private void addControls() {
        rcv_songPlayList = findViewById(R.id.rcv_songPlayList);
        songsPlayListAdapter = new SongsPlayListAdapter(SongsPlayListActivity.this, list);
        rcv_songPlayList.setAdapter(songsPlayListAdapter);
        rcv_songPlayList.setLayoutManager(new LinearLayoutManager(this));

        btn_back = findViewById(R.id.btn_back);
        btn_play = findViewById(R.id.btn_play);
        img_songPlayList = findViewById(R.id.img_songPlayList);
        txt_songPlayList = findViewById(R.id.txt_songPlayList);

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