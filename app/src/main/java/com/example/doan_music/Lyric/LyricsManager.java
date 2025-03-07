package com.example.doan_music.Lyric;

import android.media.MediaPlayer;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LyricsManager {
    private MediaPlayer mediaPlayer;
    private TextView lyricsTextView;
    private LyricsSyncManager lyricsSyncManager;

    private TextViewLyricsDisplay textViewLyricsDisplay;

    private String currentLRCUrl;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> currentTask;
    public LyricsManager(MediaPlayer mediaPlayer, TextView lyricsTextView) {
        if (mediaPlayer == null || lyricsTextView == null) {
            throw new IllegalArgumentException("MediaPlayer and TextView must not be null");
        }
        this.mediaPlayer = mediaPlayer;
        this.lyricsTextView = lyricsTextView;
        textViewLyricsDisplay = new TextViewLyricsDisplay(lyricsTextView);
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

// Hủy tác vụ trước đó nếu đang chạy
        if (currentTask != null && !currentTask.isDone()) {
            currentTask.cancel(true);
        }
        currentTask = executor.submit(() -> {
            try {
                URL url = new URL(lrcUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try (InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream())) {
                    List<LyricsSyncManager.LyricLine> lyrics = LRCParser.parse(inputStream);
                    lyricsTextView.post(() -> {
                        if (lyricsSyncManager == null) {
//                            lyricsSyncManager = new LyricsSyncManager(mediaPlayer, lyricsTextView);
                            lyricsSyncManager = new LyricsSyncManager(mediaPlayer, textViewLyricsDisplay);
                        }
                        lyricsSyncManager.setLyrics(lyrics);
                        lyricsSyncManager.start();
                    });
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                lyricsTextView.post(() -> lyricsTextView.setText("Không thể tải lời bài hát"));
            }
        });
    }

    public void resetLyrics() {
        if (lyricsSyncManager != null) {
            lyricsSyncManager.release();
            lyricsSyncManager = null;
        }
        lyricsTextView.setText("");
        currentLRCUrl = null;
    }

    public void release() {
        resetLyrics();
        executor.shutdown();
        mediaPlayer = null;
        lyricsTextView = null;
    }
}
