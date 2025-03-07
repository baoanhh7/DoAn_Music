package com.example.doan_music.designPattern.CommandPK.Class;

import android.util.Log;

import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.designPattern.CommandPK.ICommand.Command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateAlbumCommand implements Command {
    private String albumName;
    private String imageUrl;
    private int userID;
    private ConnectionClass connectionClass;

    public UpdateAlbumCommand(String albumName, String imageUrl, int userID) {
        this.albumName = albumName;
        this.imageUrl = imageUrl;
        this.userID = userID;
        this.connectionClass = new ConnectionClass();
    }

    @Override
    public void execute() {
        Connection connection = connectionClass.conClass();

        if (connection == null) {
            Log.e("UpdateAlbumCommand", "Không thể kết nối cơ sở dữ liệu");
            return;
        }

        try {
            String query = "INSERT INTO Album (AlbumName, AlbumImage, ArtistID) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, albumName);
            preparedStatement.setString(2, imageUrl);
            preparedStatement.setInt(3, userID);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Log.d("UpdateAlbumCommand", "Album đã được thêm thành công!");
            } else {
                Log.e("UpdateAlbumCommand", "Thêm album thất bại");
            }

            preparedStatement.close();
        } catch (SQLException e) {
            Log.e("UpdateAlbumCommand", "SQL Error: " + e.getMessage(), e);
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                Log.e("UpdateAlbumCommand", "Failed to close connection: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void undo() {
        Connection connection = connectionClass.conClass();

        if (connection == null) {
            Log.e("UpdateAlbumCommand", "Không thể kết nối cơ sở dữ liệu");
            return;
        }

        try {
            String query = "DELETE FROM Album WHERE AlbumName = ? AND AlbumImage = ? AND ArtistID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, albumName);
            preparedStatement.setString(2, imageUrl);
            preparedStatement.setInt(3, userID);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Log.d("UpdateAlbumCommand", "Hoàn tác: Album đã bị xóa!");
            } else {
                Log.e("UpdateAlbumCommand", "Hoàn tác thất bại: Không tìm thấy album để xóa");
            }

            preparedStatement.close();
        } catch (SQLException e) {
            Log.e("UpdateAlbumCommand", "SQL Error: " + e.getMessage(), e);
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                Log.e("UpdateAlbumCommand", "Failed to close connection: " + e.getMessage(), e);
            }
        }
    }
}

