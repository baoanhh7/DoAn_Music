package com.example.doan_music.music;

import java.util.ArrayList;

public class RepeatPlayback implements Playback{
    @Override
    public int getNextPosition(int currentPosition, ArrayList<Integer> playlist) {
        return currentPosition;
    }

    @Override
    public int getPrePosition(int currentPosition, ArrayList<Integer> playlist) {
        return currentPosition;
    }
}
