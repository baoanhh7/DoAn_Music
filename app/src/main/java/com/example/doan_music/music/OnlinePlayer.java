package com.example.doan_music.music;

import android.media.MediaPlayer;

import java.util.ArrayList;

class OnlinePlayer implements MusicPlayer {
    private MediaPlayer mediaPlayer;
    Integer currentPosition = -1;
    public OnlinePlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
    @Override
    public void play(ArrayList<Integer> arr) {
        mediaPlayer.reset();
        if (currentPosition < arr.size() - 1) {
            currentPosition++;
        } else {
            currentPosition = 0;
        }
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

