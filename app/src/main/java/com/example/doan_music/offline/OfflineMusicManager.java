package com.example.doan_music.offline;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.doan_music.model.Song;
import com.example.doan_music.offline.database.DatabaseHelper;
import com.example.doan_music.offline.model.SongOffline;
import com.example.doan_music.offline.model.UserOffline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// OfflineMusicManager.java
public class OfflineMusicManager {
    private Context context;
    private DatabaseHelper dbHelper;
    private int userID;;

    public OfflineMusicManager(Context context, int userID) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.userID = userID;
    }

    public List<SongOffline> getDownloadedSongs() {
        List<SongOffline> songs = new ArrayList<>();

        // Chỉ lấy bài hát nếu user còn premium
//        if (!currentUser.isPremiumValid()) {
//            return songs; // Return empty list if not premium
//        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                 "song_id", "SongImage", "SongName", "Artist", "local_path_song", "download_date", "local_path_lrc"
        };

        String selection = "user_id = ?";
        String[] selectionArgs = { String.valueOf(userID) };

        Cursor cursor = db.query(
                "downloaded_songs",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                "title ASC"
        );

        while (cursor.moveToNext()) {
            SongOffline song = new SongOffline();
            // Lấy dữ liệu từ cursor và gán vào đối tượng song
            song.setSongID(cursor.getInt(cursor.getColumnIndexOrThrow("song_id")));
            song.setSongImage(cursor.getBlob(cursor.getColumnIndexOrThrow("SongImage")));
            song.setSongName(cursor.getString(cursor.getColumnIndexOrThrow("SongName")));
            song.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow("Artist")));
            song.setLinkSong(cursor.getString(cursor.getColumnIndexOrThrow("local_path_song")));
            song.setDownload_date(cursor.getLong(cursor.getColumnIndexOrThrow("download_date")));
            song.setLinkLrc(cursor.getString(cursor.getColumnIndexOrThrow("local_path_lrc")));
            songs.add(song);
        }

        cursor.close();
        return songs;
    }

    // Xóa bài hát khi hết premium
    public void cleanupExpiredDownloads() {
//        if (!currentUser.isPremiumValid()) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Lấy danh sách file cần xóa
            String[] projection = { "local_path_song",  "local_path_lrc" };
            String selection = "user_id = ?";
            String[] selectionArgs = { String.valueOf(userID) };

            Cursor cursor = db.query(
                    "downloaded_songs",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            // Xóa các file nhạc
            while (cursor.moveToNext()) {
                String filePathSong = cursor.getString(0);
                String filePathLRC = cursor.getString(1);
                File fileSong = new File(filePathSong);
                if (fileSong.exists()) {
                    fileSong.delete();
                }
                File fileLRC = new File(filePathLRC);
                if (fileLRC.exists()) {
                    fileLRC.delete();
                }
            }
            cursor.close();

            // Xóa records trong database
            db.delete("downloaded_songs", selection, selectionArgs);
//        }
    }
}