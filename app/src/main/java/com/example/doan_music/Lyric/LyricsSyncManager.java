package com.example.doan_music.Lyric;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LyricsSyncManager {
    private final MediaPlayer mediaPlayer;
    private final TextView lyricsTextView;
    private final Handler handler;
    private List<LyricLine> lyrics;
    private int currentLineIndex;

    public LyricsSyncManager(MediaPlayer mediaPlayer, TextView lyricsTextView) {
        this.mediaPlayer = mediaPlayer;
        this.lyricsTextView = lyricsTextView;
        this.handler = new Handler(Looper.getMainLooper());
        this.lyrics = new ArrayList<>();
        this.currentLineIndex = 0;
    }

    public static class LyricLine {
        public final long startTime;
        public final String text;

        public LyricLine(long startTime, String text) {
            this.startTime = startTime;
            this.text = text;
        }
    }

    public void setLyrics(List<LyricLine> lyrics) {
        this.lyrics = new ArrayList<>(lyrics);
        Collections.sort(this.lyrics, (a, b) -> Long.compare(a.startTime, b.startTime));
        this.currentLineIndex = 0;
    }

    public void start() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    long currentPosition = mediaPlayer.getCurrentPosition();
                    updateLyrics(currentPosition);
                    handler.postDelayed(this, 100); // Update every 100ms
                }
            }
        });
    }

    private void updateLyrics(long currentPosition) {
        if (currentLineIndex < lyrics.size()) {
            LyricLine currentLine = lyrics.get(currentLineIndex);
            if (currentPosition >= currentLine.startTime) {
                lyricsTextView.setText(currentLine.text);
                currentLineIndex++;
            }
        }
    }

    public void stop() {
        handler.removeCallbacksAndMessages(null);
    }
}
