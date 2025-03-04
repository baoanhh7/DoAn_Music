package com.example.doan_music.designPattern.RepositoryPattern.classIPL;

import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.designPattern.RepositoryPattern.IF.AlbumRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlbumRepositoryImpl implements AlbumRepository {
    private ConnectionClass connectionClass;

    public AlbumRepositoryImpl() {
        connectionClass = new ConnectionClass();
    }

    @Override
    public List<String> getAlbumNamesByArtist(int artistId) throws SQLException {
        List<String> albumNames = new ArrayList<>();
        Connection connection = connectionClass.conClass();
        if (connection != null) {
            String query = "SELECT AlbumName FROM Album WHERE ArtistID = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, artistId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                albumNames.add(rs.getString("AlbumName"));
            }
            rs.close();
            ps.close();
            connection.close();
        }
        return albumNames;
    }

    @Override
    public int getAlbumIdByNameAndArtist(String albumName, int artistId) throws SQLException {
        int albumId = -1;
        Connection connection = connectionClass.conClass();
        if (connection != null) {
            String query = "SELECT AlbumID FROM Album WHERE AlbumName = ? AND ArtistID = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, albumName);
            ps.setInt(2, artistId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                albumId = rs.getInt("AlbumID");
            }
            rs.close();
            ps.close();
            connection.close();
        }
        return albumId;
    }
}