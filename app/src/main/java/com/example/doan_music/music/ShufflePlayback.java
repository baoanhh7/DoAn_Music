package com.example.doan_music.music;

import java.util.ArrayList;
import java.util.Random;

class ShufflePlayback implements Playback {
    @Override
    public int getNextPosition(int currentPosition, ArrayList<Integer> playlist) {
        return new Random().nextInt(playlist.size()-1);
    }

    @Override
    public int getPrePosition(int currentPosition, ArrayList<Integer> playlist) {
        return new Random().nextInt(playlist.size()-1);
    }
}
