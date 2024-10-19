package com.example.doan_music.fragment.drawer;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AllSongs_Fragment extends Fragment {
    View view;
    RecyclerView rcv_songs;
    SongAdapter songAdapter;
    SearchView allSong_searchView;
    DbHelper dbHelper;
    SQLiteDatabase database = null;
    List<Song> songList, filterSongList;
    ArrayList<Integer> arr = new ArrayList<>();
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_allsongs, container, false);

        addControls();
        addEvents();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        createData();
    }

    private void addEvents() {
        allSong_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Lọc dữ liệu khi người dùng nhập văn bản vào SearchView
                filter(newText);
                return false;
            }
        });

        songAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                ConnectionClass sql = new ConnectionClass();  // Assuming ConnectionClass is your helper for SQL Server connections
                Connection connection = sql.conClass();       // Establish connection to SQL Server
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;

                if (connection != null) {
                    try {
                        // Truy vấn bài hát theo tên từ SQL Server
                        String query = "SELECT SongID, Views FROM Song WHERE SongName = ?";
                        preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setString(1, data);  // Gán tên bài hát vào câu truy vấn
                        resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {  // Nếu tìm thấy bài hát
                            int id = resultSet.getInt("SongID");  // Lấy SongID
                            int view = resultSet.getInt("Views");  // Lấy số lượt xem hiện tại

                            // Tăng số lần xem
                            view++;

                            // Cập nhật số lần xem trong cơ sở dữ liệu SQL Server
                            String updateQuery = "UPDATE Song SET Views = ? WHERE SongID = ?";
                            preparedStatement = connection.prepareStatement(updateQuery);
                            preparedStatement.setInt(1, view);  // Gán giá trị lượt xem mới
                            preparedStatement.setInt(2, id);    // Gán SongID để cập nhật bài hát cụ thể
                            preparedStatement.executeUpdate();

                            // Chuyển sang màn hình phát nhạc
                            Intent intent = new Intent(requireContext(), PlayMusicActivity.class);
                            intent.putExtra("SongID", id);
                            intent.putExtra("arrIDSongs", arr);  // Assuming arr is a list of song IDs

                            startActivity(intent);
                        }

                    } catch (SQLException e) {
                        Log.e("SQL Error", e.getMessage());
                    } finally {
                        // Đảm bảo đóng các tài nguyên
                        try {
                            if (resultSet != null) resultSet.close();
                            if (preparedStatement != null) preparedStatement.close();
                            if (connection != null) connection.close();
                        } catch (Exception e) {
                            Log.e("Error closing resources", e.getMessage());
                        }
                    }
                } else {
                    Log.e("Error", "Connection to SQL Server failed");
                }
            }
        });

    }

    // Hàm lọc dữ liệu
    private void filter(String text) {
        filterSongList.clear();
        for (Song song : songList) {
            if (song.getSongName().toLowerCase().contains(text.toLowerCase())) {
                filterSongList.add(song);
            }
        }
        songAdapter.filterList(filterSongList); // Cập nhật dữ liệu đã lọc cho Adapter
    }

    private void createData() {
        ConnectionClass sql = new ConnectionClass();  // Giả sử ConnectionClass là lớp trợ giúp kết nối SQL Server
        Connection connection = sql.conClass();       // Kết nối với SQL Server
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        if (connection != null) {
            try {
                songList.clear(); // Xóa danh sách trước khi thêm dữ liệu mới
                arr.clear(); // Xóa mảng arr trước khi thêm dữ liệu mới

                // Truy vấn tất cả bài hát từ bảng Songs
                String querySongs = "SELECT SongID, SongName, ArtistID, SongImage, LinkSong FROM Song";
                preparedStatement = connection.prepareStatement(querySongs);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("SongID");
                    String songName = resultSet.getString("SongName");
                    int artist = resultSet.getInt("ArtistID");
                    String image = resultSet.getString("SongImage");
                    byte[] img = getImageBytesFromURL(image);
                    String linkSong = resultSet.getString("LinkSong");

                    // Tạo đối tượng Song và thêm vào danh sách
                    Song song = new Song(id, songName, artist, img, linkSong, 0); // 0 vì không có trạng thái yêu thích
                    songList.add(song);
                    arr.add(id);  // Thêm songID vào mảng arr
                }
                songAdapter.notifyDataSetChanged(); // Cập nhật giao diện sau khi thêm dữ liệu
            } catch (SQLException e) {
                Log.e("SQL Error", e.getMessage());
            } finally {
                try {
                    if (resultSet != null) resultSet.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    Log.e("Error closing resources", e.getMessage());
                }
            }
        } else {
            Log.e("Error", "Connection to SQL Server failed");
        }
    }

    private void addControls() {
        rcv_songs = view.findViewById(R.id.rcv_songs);
        allSong_searchView = view.findViewById(R.id.allSong_searchView);

        // đổi màu editText
        EditText editText = allSong_searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setHintTextColor(Color.WHITE);

        songList = new ArrayList<>();
        filterSongList = new ArrayList<>();

        songAdapter = new SongAdapter(requireContext(), songList);
        rcv_songs.setAdapter(songAdapter);

        rcv_songs.setLayoutManager(new LinearLayoutManager(requireContext()));
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