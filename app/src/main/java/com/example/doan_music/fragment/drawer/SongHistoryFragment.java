package com.example.doan_music.fragment.drawer;

import android.content.Intent;
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
import com.example.doan_music.activity.MainActivity;
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
        MainActivity mainActivity = (MainActivity) getActivity();
        Integer maU = mainActivity.getMyVariable();  // Lấy UserID từ MainActivity
        List<Integer> listHistory = new ArrayList<>();   // Danh sách các SongID yêu thích

        // Kết nối tới SQL Server
        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();

        if (connection != null) {
            try {
                // Truy vấn bảng User_SongLove để lấy danh sách bài hát yêu thích của người dùng
                String query1 = "SELECT * FROM HistorySong WHERE UserID = ?";
                PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
                preparedStatement1.setInt(1, maU);
                ResultSet resultSet1 = preparedStatement1.executeQuery();

                listHistory.clear();

                // Lấy SongID từ kết quả truy vấn
                while (resultSet1.next()) {
                    int songID = resultSet1.getInt("SongID");  // Cột SongID
                    listHistory.add(songID);
                }

                resultSet1.close();
                preparedStatement1.close();

                // Truy vấn bảng Songs để lấy danh sách bài hát
                String query2 = "SELECT * FROM Song";
                Statement statement2 = connection.createStatement();
                ResultSet resultSet2 = statement2.executeQuery(query2);

                songList.clear();

                // Duyệt qua các bài hát và kiểm tra xem có trong danh sách history không
                while (resultSet2.next()) {
                    int id = resultSet2.getInt("SongID");
                    String songName = resultSet2.getString("SongName");

                    String image = resultSet2.getString("SongImage");
                    byte[] imageBytes = getImageBytesFromURL(image);

                    String linkSong = resultSet2.getString("LinkSong");

                    // Nếu bài hát nằm trong danh sách yêu thích, thêm vào songList
                    if (listHistory.contains(id)) {
                        Song song = new Song(id, songName, imageBytes, linkSong);
                        songList.add(song);
                        arr.add(id);
                    }
                }

                resultSet2.close();
                statement2.close();

                connection.close();  // Đóng kết nối

            } catch (SQLException e) {
                Log.e("SQL Error", e.getMessage());
            }
        } else {
            Log.e("Error", "Connection is null");
        }

        songsHistoryAdapter.notifyDataSetChanged();  // Cập nhật giao diện
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