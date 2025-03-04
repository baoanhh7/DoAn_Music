package com.example.doan_music.designPattern.RepositoryPattern.IF;

import com.example.doan_music.model.Song;

import java.sql.SQLException;

public interface SongRepository {
    boolean addSong(Song song);
}
