package com.example.doan_music.offline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.adapter.thuvien.ThuVienAlbumAdapter;
import com.example.doan_music.m_interface.OnItemClickListener;
import com.example.doan_music.model.ThuVien;
import com.example.doan_music.music.PlayMusicActivity;

import java.util.ArrayList;

public class ListSongOffActivity extends AppCompatActivity {

    ImageView img_songoffline_album;
    ImageButton btn_play_songoffline;
    RecyclerView rcv_songoffline;
    int UserID;
    ThuVienAlbumAdapter thuVienAlbumAdapter;
    ArrayList<ThuVien> arr;
    ArrayList<Integer> arr1 = new ArrayList<>();
    OfflineMusicManager offlineMusicManager;
    SQLiteDatabase database = null;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song_off);
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        UserID = sharedPreferences.getInt("userID", -1);  // Lấy userID
        addControls();
        loadData();
        //addEvents();
    }

    private void loadData() {
        database = openOrCreateDatabase("music_db", MODE_PRIVATE, null);
        // Câu truy vấn để lấy dữ liệu
        String query = "SELECT song_id, SongImage, SongName FROM downloaded_songs WHERE user_id = ?";

        // Thực hiện truy vấn
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(UserID)});
        arr.clear();
        arr1.clear();
        // Kiểm tra nếu có dữ liệu
        if (cursor.moveToFirst()) {
            do {

                int songId = cursor.getInt(0);
                byte[] songImage = cursor.getBlob(1);
                String songName = cursor.getString(2);

                // Xử lý dữ liệu songId, songImage, songName ở đây
                ThuVien thuVien = new ThuVien(songImage, songName);
                arr1.add(songId);
                arr.add(thuVien);
                // Ví dụ: log dữ liệu hoặc hiển thị trong giao diện
                Log.d("DownloadedSong", "ID: " + songId + ", Name: " + songName);

            } while (cursor.moveToNext());
        }

// Đóng cursor sau khi hoàn tất
        cursor.close();


    }

    private void addControls() {
        img_songoffline_album = findViewById(R.id.img_songoffline_album);
        btn_play_songoffline = findViewById(R.id.btn_play_songoffline);
        rcv_songoffline = findViewById(R.id.rcv_songoffline);
        arr = new ArrayList<>();
        thuVienAlbumAdapter = new ThuVienAlbumAdapter(ListSongOffActivity.this, arr);
        thuVienAlbumAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                database = openOrCreateDatabase("music_db", MODE_PRIVATE, null);
                String query = "SELECT song_id FROM downloaded_songs WHERE SongName = ?";
                // Thực hiện truy vấn
                Cursor cursor = database.rawQuery(query, new String[]{data});
                while (cursor.moveToNext()) {
                    int songID = cursor.getInt(0);
                    intent = new Intent(ListSongOffActivity.this, PlaySongOffActivity.class);
                    intent.putExtra("SongID", songID);
                    intent.putExtra("arrIDSongs", arr1);
                    break;
                }
                cursor.close();
                startActivity(intent);

            }
        });
        rcv_songoffline.setAdapter(thuVienAlbumAdapter);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        rcv_songoffline.setLayoutManager(linearLayout);
    }
}