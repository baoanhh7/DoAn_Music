package com.example.doan_music.music;

import java.util.ArrayList;

class NormalPlayback implements Playback{
    @Override
    public int getNextPosition(int currentPosition, ArrayList<Integer> playlist) {
        return (currentPosition < playlist.size() - 1) ? currentPosition + 1 : 0;
    }

    @Override
    public int getPrePosition(int currentPosition, ArrayList<Integer> playlist) {
        return (currentPosition >0) ? currentPosition - 1 : playlist.size() - 1;
    }
}
