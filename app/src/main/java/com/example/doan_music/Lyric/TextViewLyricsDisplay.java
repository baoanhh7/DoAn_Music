package com.example.doan_music.Lyric;

import android.widget.TextView;

public class TextViewLyricsDisplay implements LyricsDisplay {
    private TextView textView;

    public TextViewLyricsDisplay(TextView textView) {
        if (textView == null) {
            throw new IllegalArgumentException("TextView must not be null");
        }
        this.textView = textView;
    }

    @Override
    public void displayLyric(String text) {
        textView.setText(text);
    }
}
