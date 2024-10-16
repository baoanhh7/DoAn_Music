package com.example.doan_music.activity.admin.accept;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_music.R;
import com.example.doan_music.activity.admin.accept.adapter.user_item_Adapter;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.model.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AcceptActivity extends AppCompatActivity {
    private ListView lvUsers;
    private user_item_Adapter adapter;
    private List<Users> pendingUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept);

        lvUsers = findViewById(R.id.lvArtist_admin);
        pendingUsers = new ArrayList<>();

        // Khởi tạo adapter và gắn vào ListView
        adapter = new user_item_Adapter(this, pendingUsers);
        lvUsers.setAdapter(adapter);

        // Lấy danh sách Users có trạng thái "pending" từ database
        loadPendingUsers();

        // Xử lý sự kiện khi nhấn nút Back
        findViewById(R.id.btn_back_accept_admin).setOnClickListener(v -> finish());
    }

    // Hàm lấy danh sách user có trạng thái "pending" từ cơ sở dữ liệu
    private void loadPendingUsers() {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.conClass();

        if (connection != null) {
            try {
                String query = "SELECT UserID, Username, Email, Role, Status FROM Users WHERE Status = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "pending");

                ResultSet resultSet = preparedStatement.executeQuery();

                pendingUsers.clear();
                while (resultSet.next()) {
                    Users user = new Users(
                            resultSet.getInt("UserID"),
                            resultSet.getString("Username"),
                            resultSet.getString("Email"),
                            null, // Không lấy password
                            resultSet.getString("Status"),
                            resultSet.getString("Role")
                    );

                    pendingUsers.add(user); // Thêm user vào danh sách
                }

                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                Log.e("LoadPendingUsers", "Error loading pending users: " + e.getMessage());
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    Log.e("LoadPendingUsers", "Error closing connection: " + e.getMessage());
                }
            }
        } else {
            Log.e("LoadPendingUsers", "Connection is null");
        }

        // Cập nhật adapter với dữ liệu mới
        runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
        });
    }
}
