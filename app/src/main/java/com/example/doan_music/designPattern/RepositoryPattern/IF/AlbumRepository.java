package com.example.doan_music.designPattern.RepositoryPattern.IF;

import java.sql.SQLException;
import java.util.List;

public interface AlbumRepository {
    List<String> getAlbumNamesByArtist(int artistId) throws SQLException;
    int getAlbumIdByNameAndArtist(String albumName, int artistId) throws SQLException;
}
