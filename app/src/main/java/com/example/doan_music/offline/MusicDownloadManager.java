package com.example.doan_music.offline;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.doan_music.model.Song;
import com.example.doan_music.offline.database.DatabaseHelper;
import com.example.doan_music.offline.model.SongOffline;
import com.example.doan_music.offline.model.UserOffline;

import java.io.File;

// MusicDownloadManager.java
public class MusicDownloadManager {
    private Context context;
    private DatabaseHelper dbHelper;
    private UserOffline currentUser;

    public MusicDownloadManager(Context context, UserOffline user) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.currentUser = user;
    }

    public boolean downloadSong(SongOffline song) throws Exception {
        // Kiểm tra quyền premium
        if (!currentUser.isPremiumValid()) {
            throw new Exception("Premium membership required to download songs");
        }

        // Tạo thư mục lưu nhạc
        File musicDir = new File(context.getExternalFilesDir(null), "offline_music");
        if (!musicDir.exists()) {
            musicDir.mkdirs();
        }
        // Tạo thư mục lưu lrc
        File lrcDir = new File(context.getExternalFilesDir(null), "lrc");
        if (!lrcDir.exists()) {
            lrcDir.mkdirs();
        }

        // Tạo tên file độc nhất cho mỗi user và bài hát
        @SuppressLint("DefaultLocale")
        String fileNameSong = String.format("user_%d_song_%d.mp3", currentUser.getUserID(), song.getSongID());
        File localFileSong = new File(musicDir, fileNameSong);
        @SuppressLint("DefaultLocale")
        String fileNameLRC = String.format("user_%d_song_%d.lrc", currentUser.getUserID(), song.getSongID());
        File localFileLRC = new File(musicDir, fileNameLRC);

        // Download file từ server
        boolean downloadSuccessSong = downloadFileFromServer(song.getLinkSong(), localFileSong);
        // Download file từ server
        boolean downloadSuccessLRC = downloadFileFromServer(song.getLinkLrc(), localFileLRC);

        if (downloadSuccessSong && downloadSuccessLRC) {
            // Lưu thông tin vào database
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("song_id", song.getSongID());
            values.put("user_id", currentUser.getUserID());
            values.put("SongName", song.getSongName());
            values.put("SongImage",song.getSongImage());
            values.put("artist", song.getArtistName());
            values.put("local_path_song", localFileSong.getAbsolutePath());
            values.put("local_path_lrc", localFileLRC.getAbsolutePath());
            values.put("download_date", System.currentTimeMillis());

            db.insert("downloaded_songs", null, values);
            return true;
        }
        return false;
    }

    private boolean downloadFileFromServer(String url, File destination) {
        // Implement download logic using OkHttp hoặc other network library
        return true; // Return true if download successful
    }
}