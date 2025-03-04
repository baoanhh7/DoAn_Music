package com.example.doan_music.designPattern.RepositoryPattern.classIPL;

import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.designPattern.RepositoryPattern.IF.TypeRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TypeRepositoryImpl implements TypeRepository {
    private ConnectionClass connectionClass;

    public TypeRepositoryImpl() {
        connectionClass = new ConnectionClass();
    }

    @Override
    public List<String> getTypeNames() throws SQLException {
        List<String> typeNames = new ArrayList<>();
        Connection connection = connectionClass.conClass();
        if (connection != null) {
            String query = "SELECT NameType FROM Type";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                typeNames.add(rs.getString("NameType"));
            }
            rs.close();
            ps.close();
            connection.close();
        }
        return typeNames;
    }

    @Override
    public int getTypeIdByName(String typeName) throws SQLException {
        int typeId = -1;
        Connection connection = connectionClass.conClass();
        if (connection != null) {
            String query = "SELECT TypeID FROM Type WHERE NameType = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, typeName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                typeId = rs.getInt("TypeID");
            }
            rs.close();
            ps.close();
            connection.close();
        }
        return typeId;
    }
}