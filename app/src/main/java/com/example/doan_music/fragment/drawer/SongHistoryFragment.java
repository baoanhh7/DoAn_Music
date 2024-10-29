package com.example.doan_music.fragment.drawer;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.adapter.home.SongsHistoryAdapter;
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

public class SongHistoryFragment extends Fragment {

    View view;
    RecyclerView rcv_songshistory;
    List<Song> songList;
    ArrayList<Integer> arr = new ArrayList<>();
    SongsHistoryAdapter songsHistoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_song_history, container, false);
        addControls();

        songsHistoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
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
                            String songName = resultSet.getString("SongName");  // Lấy tên bài hát

                            if (data.equals(songName)) {
                                // Tạo Intent và truyền dữ liệu qua PlayMusicActivity
                                Intent intent = new Intent(requireContext(), PlayMusicActivity.class);
                                intent.putExtra("SongID", id);
                                intent.putExtra("arrIDSongs", arr);

                                startActivity(intent);
                                break;
                            }
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

                songsHistoryAdapter.notifyDataSetChanged();  // Cập nhật lại giao diện nếu cần
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        createData();
    }

    private void createData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userID", -1);  // Lấy userID

        ConnectionClass sql = new ConnectionClass();

        // Chỉ lấy bài hát có lịch sử nghe của người dùng
        String query = "SELECT s.* FROM SongHistory h JOIN Song s ON h.SongID = s.SongID WHERE h.UserID = ? ORDER BY h.PlayTime DESC";
        try (Connection connection = sql.conClass();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            songList.clear(); // Xóa danh sách trước khi thêm mới

            while (rs.next()) {
                int id = rs.getInt("SongID");
                String songName = rs.getString("SongName");
                String image = rs.getString("SongImage");
                byte[] imageBytes = getImageBytesFromURL(image); // Tải ảnh từ URL
                String linkSong = rs.getString("LinkSong");

                Song song = new Song(id, songName, imageBytes, linkSong);
                songList.add(song);
                arr.add(id);
            }

            rs.close();

        } catch (SQLException e) {
            Log.e("Error", "Không thể lấy lịch sử bài hát: " + e.getMessage());
        }

        songsHistoryAdapter.notifyDataSetChanged();  // Cập nhật giao diện sau khi lấy dữ liệu
    }


    private void addControls() {
        rcv_songshistory = view.findViewById(R.id.rcv_songshistory);
        songList = new ArrayList<>();

        songsHistoryAdapter = new SongsHistoryAdapter(requireContext(), songList);
        rcv_songshistory.setAdapter(songsHistoryAdapter);

        rcv_songshistory.setLayoutManager(new LinearLayoutManager(requireContext()));
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