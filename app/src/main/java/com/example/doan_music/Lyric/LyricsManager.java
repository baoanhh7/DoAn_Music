package com.example.doan_music.Lyric;

import android.media.MediaPlayer;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LyricsManager {
    private MediaPlayer mediaPlayer;
    private TextView lyricsTextView;
    private LyricsSyncManager lyricsSyncManager;
    private String currentLRCUrl;

    public LyricsManager(MediaPlayer mediaPlayer, TextView lyricsTextView) {
        this.mediaPlayer = mediaPlayer;
        this.lyricsTextView = lyricsTextView;
    }

    public void loadLyrics(String lrcUrl) {
        // Nếu URL mới khác với URL hiện tại, chúng ta cần reset
        if (lrcUrl == null || lrcUrl.equalsIgnoreCase("null") || lrcUrl.isEmpty()) {
            lyricsTextView.setText("Lyric đang cập nhật");
            return;
        } else if (!lrcUrl.equals(currentLRCUrl)) {
            resetLyrics();
            currentLRCUrl = lrcUrl;
        }


        new Thread(() -> {
            try {
                URL url = new URL(lrcUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                List<LyricsSyncManager.LyricLine> lyrics = LRCParser.parse(inputStream);

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
                lyricsTextView.post(() -> lyricsTextView.setText("Không thể tải lời bài hát"));
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
