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
    //private final TextView lyricsTextView;
    private final Handler handler;
    private List<LyricLine> lyrics;
    private int currentLineIndex;
    private boolean isRunning = false;
    private final LyricsDisplay display;
//    public LyricsSyncManager(MediaPlayer mediaPlayer, TextView lyricsTextView) {
//        this.mediaPlayer = mediaPlayer;
//        this.lyricsTextView = lyricsTextView;
//        this.handler = new Handler(Looper.getMainLooper());
//        this.lyrics = new ArrayList<>();
//        this.currentLineIndex = 0;
//    }
public LyricsSyncManager(MediaPlayer mediaPlayer, LyricsDisplay display) {
    this.mediaPlayer = mediaPlayer;
    this.display = display;
    this.handler = new Handler(Looper.getMainLooper());
    this.lyrics = new ArrayList<>();
    this.currentLineIndex = 0;
}

    public void setLyrics(List<LyricLine> lyrics) {
        this.lyrics = new ArrayList<>(lyrics);
        Collections.sort(this.lyrics, (a, b) -> Long.compare(a.startTime, b.startTime));
        this.currentLineIndex = 0;
    }

    public void start() {
        if (isRunning) return;
        isRunning = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isRunning && mediaPlayer != null && mediaPlayer.isPlaying()) {
                    long currentPosition = mediaPlayer.getCurrentPosition();
                    updateLyrics(currentPosition);
                    handler.postDelayed(this, 100);
                }
            }
        });
    }

    private void updateLyrics(long currentPosition) {
//        if (currentLineIndex < lyrics.size()) {
//            LyricLine currentLine = lyrics.get(currentLineIndex);
//            if (currentPosition >= currentLine.startTime) {
//                lyricsTextView.setText(currentLine.text);
//                currentLineIndex++;
//            }
//        }
        if (lyrics.isEmpty()) return;

        int index = Collections.binarySearch(lyrics, new LyricLine(currentPosition, ""),
                (a, b) -> Long.compare(a.startTime, b.startTime));
        if (index < 0) {
            index = -index - 2; // Tìm dòng trước đó
        }
        if (index >= 0 && index < lyrics.size() && currentPosition >= lyrics.get(index).startTime) {
            currentLineIndex = index;
//            lyricsTextView.setText(lyrics.get(currentLineIndex).text);
            display.displayLyric(lyrics.get(currentLineIndex).text);
        }
    }

    public void stop() {
        isRunning = false;
        handler.removeCallbacksAndMessages(null);
    }

    public void release() {
        stop();
        lyrics.clear();
        handler.removeCallbacksAndMessages(null);
    }
    public static class LyricLine {
        public final long startTime;
        public final String text;

        public LyricLine(long startTime, String text) {
            this.startTime = startTime;
            this.text = text;
        }
    }
}
