package com.example.doan_music.music;

import android.media.MediaPlayer;

import java.util.ArrayList;

class OfflinePlayer implements MusicPlayer {
    private MediaPlayer mediaPlayer;

    public OfflinePlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
    @Override
    public void play(ArrayList<Integer> arr) {

    }

    @Override
    public void next() {

    }

    @Override
    public void pre() {

    }

    @Override
    public void pause() {

    }
}