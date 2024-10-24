package com.example.doan_music.activity.admin.accept.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.doan_music.R;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.model.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class user_item_Adapter extends ArrayAdapter<Users> {
    private Context context;

    public user_item_Adapter(Context context, List<Users> users) {
        super(context, 0, users);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_item_layout, parent, false);
        }

        Users user = getItem(position);

        TextView tvUsername = convertView.findViewById(R.id.tvUsername);
        Button btnAccept = convertView.findViewById(R.id.btnAccept);
        Button btnCancel = convertView.findViewById(R.id.btnCancel);

        if (user != null) {
            tvUsername.setText(user.getUsername());
            // Xử lý khi nhấn nút Accept
            btnAccept.setOnClickListener(v -> {
                // Cập nhật trạng thái thành "active" và vai trò thành "artist"
                user.setStatus("active");
                user.setRole("artist");
                updateUserStatusInDatabase(user.getUserID(), "active", "artist");

                notifyDataSetChanged(); // Cập nhật giao diện
            });
            // Xử lý khi nhấn nút Cancel
            btnCancel.setOnClickListener(v -> {
                // Xóa user khỏi database
                deleteUserFromDatabase(user.getUserID());

                // Xóa user khỏi adapter
                remove(user);
                notifyDataSetChanged();
            });
        }

        return convertView;
    }

    // Hàm cập nhật trạng thái user trong database
    private void updateUserStatusInDatabase(int userID, String status, String role) {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.conClass();

        if (connection != null) {
            try {
                connection.setAutoCommit(false); // Bắt đầu transaction

                try {
                    // 1. Cập nhật Users
                    String updateUserQuery = "UPDATE Users SET Status = ?, Role = ? WHERE UserID = ?";
                    PreparedStatement userStatement = connection.prepareStatement(updateUserQuery);
                    userStatement.setString(1, status);
                    userStatement.setString(2, role);
                    userStatement.setInt(3, userID);
                    userStatement.executeUpdate();
                    userStatement.close();

                    // 2. Lấy username từ bảng Users
                    String getUsernameQuery = "SELECT Username FROM Users WHERE UserID = ?";
                    PreparedStatement usernameStatement = connection.prepareStatement(getUsernameQuery);
                    usernameStatement.setInt(1, userID);
                    String username = "";
                    java.sql.ResultSet resultSet = usernameStatement.executeQuery();
                    if (resultSet.next()) {
                        username = resultSet.getString("Username");
                    }
                    usernameStatement.close();

                    // 3. Insert vào bảng Artist (không chỉ định ArtistID vì đã auto-increment)
                    String insertArtistQuery = "INSERT INTO Artist (ArtistName) VALUES (?)";
                    PreparedStatement artistStatement = connection.prepareStatement(insertArtistQuery);
                    artistStatement.setString(1, username);
                    artistStatement.executeUpdate();
                    artistStatement.close();

                    connection.commit(); // Commit transaction nếu tất cả thành công
                } catch (SQLException e) {
                    connection.rollback(); // Rollback nếu có lỗi
                    Log.e("UserAdapter", "Error in transaction: " + e.getMessage());
                    throw e;
                }

            } catch (SQLException e) {
                Log.e("UserAdapter", "Error updating user status and creating artist: " + e.getMessage());
            } finally {
                try {
                    connection.setAutoCommit(true); // Reset auto-commit về true
                    connection.close();
                } catch (SQLException e) {
                    Log.e("UserAdapter", "Error closing connection: " + e.getMessage());
                }
            }
        }
    }


    // Hàm xóa user khỏi database
    private void deleteUserFromDatabase(int userID) {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.conClass();

        if (connection != null) {
            try {
                String query = "DELETE FROM Users WHERE UserID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userID);

                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                Log.e("UserAdapter", "Error deleting user: " + e.getMessage());
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    Log.e("UserAdapter", "Error closing connection: " + e.getMessage());
                }
            }
        }
    }
}
