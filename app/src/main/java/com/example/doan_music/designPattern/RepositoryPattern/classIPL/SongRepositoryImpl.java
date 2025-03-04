package com.example.doan_music.designPattern.RepositoryPattern.classIPL;

import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.designPattern.RepositoryPattern.IF.SongRepository;
import com.example.doan_music.model.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SongRepositoryImpl implements SongRepository {
    private ConnectionClass connectionClass;

    public SongRepositoryImpl() {
        connectionClass = new ConnectionClass();
    }

//    @Override
//    public boolean addSong(Song song) {
//        Connection connection = connectionClass.conClass();
//        if (connection != null) {
//            try {
//                String query = "INSERT INTO Song (SongName, SongImage, LinkSong, AlbumID, TypeID, ArtistID, Views, LinkLRC) " +
//                        "VALUES (?, ?, ?, ?, ?, ?, 0, ?)";
//                PreparedStatement ps = connection.prepareStatement(query);
//                ps.setString(1, song.getSongName());
//                // Vì SongImage là byte[], sử dụng setBytes()
//                ps.setBytes(2, song.getSongImage());
//                ps.setString(3, song.getLinkSong());
//                if (song.getAlbumID() <= 0) {
//                    ps.setNull(4, java.sql.Types.INTEGER);
//                } else {
//                    ps.setInt(4, song.getAlbumID());
//                }
//                if (song.getTypeID() <= 0) {
//                    ps.setNull(5, java.sql.Types.INTEGER);
//                } else {
//                    ps.setInt(5, song.getTypeID());
//                }
//                ps.setInt(6, song.getArtistID());
//                if (song.getLinkLrc() == null) {
//                    ps.setNull(7, java.sql.Types.VARCHAR);
//                } else {
//                    ps.setString(7, song.getLinkLrc());
//                }
//                int rowsAffected = ps.executeUpdate();
//                ps.close();
//                connection.close();
//                return rowsAffected > 0;
//            } catch (SQLException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//        return false;
//    }
//}
@Override
public boolean addSong(Song song) {
    Connection connection = connectionClass.conClass();
    if (connection != null) {
        try {
            String query = "INSERT INTO Song (SongName, SongImage, LinkSong, AlbumID, TypeID, ArtistID, Views, LinkLRC) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 0, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, song.getSongName());
            // Chuyển đổi byte[] (chứa URL) thành chuỗi để lưu vào DB
            ps.setString(2, new String(song.getSongImage()));
            ps.setString(3, song.getLinkSong());
            if (song.getAlbumID() <= 0) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, song.getAlbumID());
            }
            if (song.getTypeID() <= 0) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, song.getTypeID());
            }
            ps.setInt(6, song.getArtistID());
            if (song.getLinkLrc() == null) {
                ps.setNull(7, java.sql.Types.VARCHAR);
            } else {
                ps.setString(7, song.getLinkLrc());
            }
            int rowsAffected = ps.executeUpdate();
            ps.close();
            connection.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    return false;
}
}
