package com.example.doan_music.music;

import java.util.ArrayList;

public interface MusicPlayer {
    void play(ArrayList<Integer> arr);
    void next();
    void pre();
    void pause();
}
