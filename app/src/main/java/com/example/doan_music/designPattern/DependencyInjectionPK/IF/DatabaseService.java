package com.example.doan_music.designPattern.DependencyInjectionPK.IF;

import java.sql.Connection;

public interface DatabaseService {
    Connection getConnection();
}
