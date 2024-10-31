package com.example.doan_music.activity.admin.Types;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan_music.R;
import com.example.doan_music.database.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UpdateNameTypeActivity extends AppCompatActivity {
    private EditText etNameType;
    private Button btnBack, btnAccept;
    private Connection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_name_type);
        etNameType = findViewById(R.id.etNameType);
        btnBack = findViewById(R.id.btnBack);
        btnAccept = findViewById(R.id.btnAccept);

        // Get database connection
        ConnectionClass connectionClass = new ConnectionClass();
        connection = connectionClass.conClass();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to previous screen
                finish();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNameType = etNameType.getText().toString().trim();
                if (!newNameType.isEmpty()) {
                    updateNameType(newNameType);
                } else {
                    Toast.makeText(UpdateNameTypeActivity.this, "Please enter a new name type", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateNameType(String newNameType) {
        try {
            String sql = "INSERT INTO Type (NameType) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, newNameType);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // Get the auto-generated ID
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    Toast.makeText(this, "NameType updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "NameType updated successfully, but failed to retrieve new ID", Toast.LENGTH_SHORT).show();
                }
                finish(); // Navigate back to previous screen
            } else {
                Toast.makeText(this, "Failed to update NameType", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error updating NameType: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}