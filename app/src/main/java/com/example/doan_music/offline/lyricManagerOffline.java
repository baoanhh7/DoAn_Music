package com.example.doan_music.offline;

import android.media.MediaPlayer;
import android.widget.TextView;

import com.example.doan_music.Lyric.LRCParser;
import com.example.doan_music.Lyric.LyricsSyncManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class lyricManagerOffline {
    private MediaPlayer mediaPlayer;
    private TextView lyricsTextView;
    private LyricsSyncManager lyricsSyncManager;
    private String currentLRCUrl;

    public lyricManagerOffline(MediaPlayer mediaPlayer, TextView lyricsTextView) {
        this.mediaPlayer = mediaPlayer;
        this.lyricsTextView = lyricsTextView;
    }

    public void loadLyricsFromFile(String filePath) {
        // Nếu filePath null hoặc rỗng
        if (filePath == null || filePath.isEmpty()) {
            lyricsTextView.setText("Không tìm thấy file lyrics");
            return;
        }

        new Thread(() -> {
            File file = new File(filePath);
            if (!file.exists()) {
                lyricsTextView.post(() -> lyricsTextView.setText("File lyrics không tồn tại"));
                return;
            }

            try (InputStream inputStream = new FileInputStream(file)) {
                List lyrics = LRCParser.parse(inputStream);

                // Chạy trên UI thread để cập nhật UI
                lyricsTextView.post(() -> {
                    if (lyricsSyncManager == null) {
                        lyricsSyncManager = new LyricsSyncManager(mediaPlayer, lyricsTextView);
                    }
                    lyricsSyncManager.setLyrics(lyrics);
                    lyricsSyncManager.start();
                });
            } catch (IOException e) {
                e.printStackTrace();
                lyricsTextView.post(() -> lyricsTextView.setText("Không thể tải lời bài hát từ file"));
            }
        }).start();
    }

    public void resetLyrics() {
        if (lyricsSyncManager != null) {
            lyricsSyncManager.stop();
            lyricsSyncManager = null;
        }
        lyricsTextView.setText("");
        currentLRCUrl = null;
    }

    public void release() {
        resetLyrics();
        mediaPlayer = null;
        lyricsTextView = null;
    }
}
