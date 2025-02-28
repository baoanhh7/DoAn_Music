package com.example.doan_music.music;

import java.util.ArrayList;

public interface Playback {
    int getNextPosition(int currentPosition, ArrayList<Integer> playlist);
    int getPrePosition(int currentPosition, ArrayList<Integer> playlist);
}
