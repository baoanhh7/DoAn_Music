package com.example.doan_music.activity.Artist.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.activity.Artist.HomeArtistActivity;
import com.example.doan_music.activity.Artist.adapter.SongAdapter;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.model.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListSongArtistFragment extends Fragment {

    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<Song> songList;
    private int userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_song_artist, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        songList = new ArrayList<>();
        songAdapter = new SongAdapter(getContext(), songList);
        recyclerView.setAdapter(songAdapter);

        // Lấy artistId từ Activity
        if (getActivity() instanceof HomeArtistActivity) {
            userID = ((HomeArtistActivity) getActivity()).getUserID();
        }

        loadSongs();

        return view;
    }

    private void loadSongs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = new ConnectionClass().conClass();
                if (connection != null) {
                    try {
                        // Log việc kết nối thành công
                        Log.d("LoadSongs", "Connected to the database.");

                        // Truy vấn SQL để lấy bài hát của nghệ sĩ dựa trên userID
                        String query = "SELECT SongID, SongName, SongImage " +
                                "FROM Song " +
                                "WHERE ArtistID = ? AND ArtistID = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setInt(1, userID);
                        preparedStatement.setInt(2, userID);  // Set ArtistID to be the same as userID

                        // Log câu lệnh SQL đang được thực thi
                        Log.d("LoadSongs", "Executing query: " + preparedStatement.toString());

                        ResultSet resultSet = preparedStatement.executeQuery();

                        final List<Song> tempList = new ArrayList<>();
                        while (resultSet.next()) {
                            int songId = resultSet.getInt("SongID");
                            String songName = resultSet.getString("SongName");
                            String songImage = resultSet.getString("SongImage");

                            // Log các thông tin bài hát lấy được
                            Log.d("LoadSongs", "SongID: " + songId + ", SongName: " + songName + ", SongImage: " + songImage);

                            tempList.add(new Song(songId, songName, songImage));
                        }

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    songList.clear();
                                    songList.addAll(tempList);

                                    // Log việc cập nhật giao diện người dùng
                                    Log.d("LoadSongs", "Song list updated, total songs: " + songList.size());

                                    songAdapter.notifyDataSetChanged();
                                }
                            });
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                        // Log lỗi SQL
                        Log.e("LoadSongs", "SQL Error: " + e.getMessage());
                    } finally {
                        try {
                            connection.close();
                            // Log việc đóng kết nối
                            Log.d("LoadSongs", "Database connection closed.");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            // Log lỗi đóng kết nối
                            Log.e("LoadSongs", "Error closing connection: " + e.getMessage());
                        }
                    }
                } else {
                    // Log lỗi kết nối
                    Log.e("LoadSongs", "Failed to connect to the database.");
                }
            }
        }).start();
    }
}