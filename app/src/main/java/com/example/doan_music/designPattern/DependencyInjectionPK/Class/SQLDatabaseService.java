package com.example.doan_music.designPattern.DependencyInjectionPK.Class;

import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.designPattern.DependencyInjectionPK.DatabaseService;

import java.sql.Connection;

public class SQLDatabaseService implements DatabaseService {
    private final ConnectionClass connectionClass;

    public SQLDatabaseService() {
        this.connectionClass = new ConnectionClass();
    }

    @Override
    public Connection getConnection() {
        return connectionClass.conClass();
    }
}
