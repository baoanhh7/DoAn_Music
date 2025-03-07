package com.example.doan_music.designPattern.DependencyInjectionPK.Model;

import android.util.Log;

import com.example.doan_music.designPattern.DependencyInjectionPK.IF.DatabaseService;
import com.example.doan_music.designPattern.DependencyInjectionPK.IF.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SQLUserRepository implements UserRepository {
    private final DatabaseService databaseService;

    public SQLUserRepository(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public boolean saveUser(User user) {
        Connection con = databaseService.getConnection();
        if (con == null) return false;

        try {
            String query = "INSERT INTO Users (username, email, phone, password, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getStatus());

            int rowsInserted = preparedStatement.executeUpdate();
            con.close();
            return rowsInserted > 0;
        } catch (Exception e) {
            Log.e("Database Error", e.getMessage());
            return false;
        }
    }
}
