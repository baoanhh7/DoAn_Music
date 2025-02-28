package com.example.doan_music.designPattern.DependencyInjectionPK;

import java.sql.Connection;

public interface DatabaseService {
    Connection getConnection();
}
