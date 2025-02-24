package com.example.doan_music.music;

import android.media.MediaPlayer;
import android.os.Build;

import java.io.IOException;

public class MediaPlayerManager {
    private static volatile MediaPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private boolean isLooping;
    private float speed;
    private int volume;
    private String currentPath;
    private boolean isPrepared;

    private MediaPlayerManager() {
        initializeMediaPlayer();
    }

    private void initializeMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        isPrepared = false;

        // Set up MediaPlayer listeners
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            handleError("MediaPlayer error: " + what);
            return true;
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            if (!isLooping) {
                isPrepared = false;
            }
        });
    }

    public static MediaPlayerManager getInstance() {
        if (instance == null) {
            synchronized (MediaPlayerManager.class) {
                if (instance == null) {
                    instance = new MediaPlayerManager();
                }
            }
        }
        return instance;
    }

    // Áp dụng cấu hình từ Builder vào MediaPlayer
    private void applyConfig(Builder builder) {
        this.isLooping = builder.isLooping;
        this.speed = validateSpeed(builder.speed);
        this.volume = validateVolume(builder.volume);
        configureMediaPlayer();
    }

    private float validateSpeed(float speed) {
        if (speed < 0.5f) return 0.5f;
        if (speed > 2.0f) return 2.0f;
        return speed;
    }

    private int validateVolume(int volume) {
        if (volume < 0) return 0;
        if (volume > 100) return 100;
        return volume;
    }

    private void configureMediaPlayer() {
        try {
            if (mediaPlayer == null) {
                initializeMediaPlayer();
            }

            mediaPlayer.setLooping(isLooping);
            float normalizedVolume = volume / 100f;
            mediaPlayer.setVolume(normalizedVolume, normalizedVolume);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer.setPlaybackParams(
                        mediaPlayer.getPlaybackParams().setSpeed(speed)
                );
            }
        } catch (Exception e) {
            handleError("Error configuring MediaPlayer: " + e.getMessage());
        }
    }

    public void setDataSource(String path) {
        try {
            if (currentPath != null && currentPath.equals(path) && isPrepared) {
                return; // Không cần set lại nếu đang phát cùng file
            }

            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            currentPath = path;
            isPrepared = true;
            configureMediaPlayer();
        } catch (IOException e) {
            handleError("Error setting data source: " + e.getMessage());
        }
    }

    public void play() {
        try {
            if (!isPrepared) {
                handleError("MediaPlayer not prepared");
                return;
            }
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } catch (Exception e) {
            handleError("Error playing: " + e.getMessage());
        }
    }

    public void pause() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (Exception e) {
            handleError("Error pausing: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            mediaPlayer.stop();
            isPrepared = false;
        } catch (Exception e) {
            handleError("Error stopping: " + e.getMessage());
        }
    }

    public void seekTo(int position) {
        try {
            if (isPrepared) {
                mediaPlayer.seekTo(position);
            }
        } catch (Exception e) {
            handleError("Error seeking: " + e.getMessage());
        }
    }

    public int getCurrentPosition() {
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            handleError("Error getting position: " + e.getMessage());
            return 0;
        }
    }

    public int getDuration() {
        try {
            return mediaPlayer.getDuration();
        } catch (Exception e) {
            handleError("Error getting duration: " + e.getMessage());
            return 0;
        }
    }

    private void handleError(String message) {
        // Implement your error handling here
        System.err.println(message);
        // Có thể thêm callback để thông báo lỗi cho UI
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPrepared = false;
            currentPath = null;
        }
    }

    public static class Builder {
        private static volatile Builder builderInstance;
        private boolean isLooping = false;
        private float speed = 1.0f;
        private int volume = 50;

        private Builder() {}

        public static Builder getInstance() {
            if (builderInstance == null) {
                synchronized (Builder.class) {
                    if (builderInstance == null) {
                        builderInstance = new Builder();
                    }
                }
            }
            return builderInstance;
        }

        public Builder setLooping(boolean isLooping) {
            this.isLooping = isLooping;
            return this;
        }

        public Builder setSpeed(float speed) {
            this.speed = speed;
            return this;
        }

        public Builder setVolume(int volume) {
            this.volume = volume;
            return this;
        }

        public void apply() {
            MediaPlayerManager.getInstance().applyConfig(this);
        }

        public void reset() {
            this.isLooping = false;
            this.speed = 1.0f;
            this.volume = 50;
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public boolean isPrepared() {
        return isPrepared;
    }
}