package com.example.doan_music.database;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionClass {

//    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.1.251:1433;databaseName=DemoMusic";
//    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.1.55:1433;databaseName=DemoMusic";
    //private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.1.73:1433;databaseName=DemoMusic";
    private static final String DB_URL = "jdbc:jtds:sqlserver://172.20.10.7:1433;databaseName=DemoMusic";
    private static final String USER = "sa";
    private static final String PASS = "1";
    Connection connection;

    //192.168.1.55
    @SuppressLint("NewApi")
    public Connection conClass() {
        StrictMode.ThreadPolicy a = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(a);
        // Tải driver JDBC
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            Log.e("Error is ", e.getMessage());
        }
        return connection;
    }
}
