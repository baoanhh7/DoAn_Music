package com.example.doan_music.music;

import android.media.MediaPlayer;

public class MediaPlayerManager {
    private static MediaPlayerManager instance;
    private MediaPlayer mediaPlayer;

    private MediaPlayerManager() {
        mediaPlayer = new MediaPlayer();
    }

    public static synchronized MediaPlayerManager getInstance() {
        if (instance == null) {
            instance = new MediaPlayerManager();
        }
        return instance;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    public static MusicPlayer getMusicPlayer(boolean isOnline) {
        if (isOnline) {
            return new OnlinePlayer();
        } else {
            return new OfflinePlayer();
        }
    }
}
