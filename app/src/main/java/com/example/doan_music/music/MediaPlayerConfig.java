package com.example.doan_music.music;

class MediaPlayerConfig {
    private boolean isLooping;
    private int volume;

    private MediaPlayerConfig(Builder builder) {
        this.isLooping = builder.isLooping;
        this.volume = builder.volume;
    }

    public static class Builder {
        private boolean isLooping = false;
        private int volume = 50; // Mặc định 50%

        public Builder setLooping(boolean isLooping) {
            this.isLooping = isLooping;
            return this;
        }

        public Builder setVolume(int volume) {
            this.volume = volume;
            return this;
        }

        public MediaPlayerConfig build() {
            return new MediaPlayerConfig(this);
        }
    }
}
