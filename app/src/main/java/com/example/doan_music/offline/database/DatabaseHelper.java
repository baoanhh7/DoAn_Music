package com.example.doan_music.offline.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// DatabaseHelper.java
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "music_db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bảng User
        String CREATE_USER_TABLE = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY ," +
                "username TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "is_premium INTEGER DEFAULT 0," +
                "premium_expire_date TEXT )";

        // Bảng Downloaded Songs với reference đến user
        String CREATE_DOWNLOADED_SONGS_TABLE = "CREATE TABLE downloaded_songs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "song_id INTEGER," +
                "user_id INTEGER," +
                "SongImage BLOB," +
                "SongName TEXT," +
                "Artist TEXT," +
                "local_path_song TEXT," +
                "download_date Date," +
                "local_path_lrc TEXT," +
                "FOREIGN KEY (user_id) REFERENCES users(id))";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_DOWNLOADED_SONGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS downloaded_songs");
        onCreate(db);
    }
}
