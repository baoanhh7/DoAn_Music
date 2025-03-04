package com.example.doan_music.designPattern.RepositoryPattern.IF;

import java.sql.SQLException;
import java.util.List;

public interface TypeRepository {
    List<String> getTypeNames() throws SQLException;
    int getTypeIdByName(String typeName) throws SQLException;
}