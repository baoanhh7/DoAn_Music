package com.example.doan_music.loginPackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_music.R;
import com.example.doan_music.activity.Artist.HomeArtistActivity;
import com.example.doan_music.activity.MainActivity;
import com.example.doan_music.activity.admin.AdminActivity;
import com.example.doan_music.database.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login_userActivity extends AppCompatActivity {
    EditText EdtEmail, EdtPassword;
    TextView tvForgotPass, tvSignup;
    Button btnLogin, btn_back;
    SQLiteDatabase database = null;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        AddControl();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCrededentials();
                //checkArtistStatus();
            }
        });
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_userActivity.this, RegisterPhoneUserActivity.class));
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_userActivity.this, UserActivity.class));
            }
        });
    }

    private void checkCrededentials() {
        String email = EdtEmail.getText().toString();
        String password = EdtPassword.getText().toString();

        if (password.isEmpty() || password.length() < 7) {
            showError(EdtPassword, "Your password must be 7 character");
        } else {
            checkDatabase();
            //checkDatabaseSQLite();
        }
    }

    private void checkDatabaseSQLite() {
        String email = EdtEmail.getText().toString();
        String password = EdtPassword.getText().toString();
        database = openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select * from Users", null);
        Intent intent = null;
        while (cursor.moveToNext()) {
            Integer ma = cursor.getInt(0);
            String Name = cursor.getString(1);
            String Email = cursor.getString(2);
            String Password = cursor.getString(3);
            String Role = cursor.getString(4);
            if (email.equals(Email) && password.equals(Password)) {
                // Xử lý đăng nhập thành công
                Toast.makeText(Login_userActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                if ("admin".equalsIgnoreCase(Role)) {
                    // Nếu là admin, chuyển đến AdminActivity
                    intent = new Intent(Login_userActivity.this, AdminActivity.class);
                } else {
                    // Nếu là người dùng thông thường, chuyển đến BeginActivity
                    intent = new Intent(Login_userActivity.this, MainActivity.class);
                    //intent.putExtra("emailU", Email);
                    // intent.putExtra("code",code);
                    intent.putExtra("maU", ma);
                    intent.putExtra("tenU", Name);

                    SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("maU1", ma);
                    editor.putString("ten", Name);
                    editor.apply();
                }
                startActivity(intent);
                break;
            }
        }
        cursor.close();
        if (intent == null)
            Toast.makeText(Login_userActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
    }

    private void checkDatabase() {
        connection = new ConnectionClass().conClass();
        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (connection != null) {
            try {
                query = "SELECT UserID, Role, Status FROM Users WHERE (Phone = ? OR Email = ? OR Username = ? AND Password = ?)";
                PreparedStatement smt = connection.prepareStatement(query);
                smt.setString(1, EdtEmail.getText().toString());
                smt.setString(2, EdtEmail.getText().toString());
                smt.setString(3, EdtEmail.getText().toString());
                smt.setString(4, EdtPassword.getText().toString());
                Toast.makeText(Login_userActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                resultSet = smt.executeQuery();
                if (resultSet.next()) {
                    Integer userID = resultSet.getInt(1);
                    String Role = resultSet.getString(2);
                    String Status = resultSet.getString(3);
                    if ("admin".equalsIgnoreCase(Role)) {
                        // Nếu là admin, chuyển đến AdminActivity
                        intent = new Intent(Login_userActivity.this, AdminActivity.class);
                    } else if ("member".equalsIgnoreCase(Role)) {
                        // Nếu là người dùng thông thường, chuyển đến MainActivity
                        intent = new Intent(Login_userActivity.this, MainActivity.class);
                        intent.putExtra("maU", userID);
                        editor.putInt("userID", userID);
                        editor.apply();
                    }
                    else if ("artist".equalsIgnoreCase(Role)&&"active".equalsIgnoreCase(Status)) {
                            // Nếu là nghệ sĩ và trạng thái là "active", chuyển đến HomeArtistActivity
                            intent = new Intent(Login_userActivity.this, HomeArtistActivity.class);
                            intent.putExtra("UserID", userID);
                        editor.putInt("userID", userID);
                        editor.apply();
                    }
                    else {
                        // Nếu nghệ sĩ nhưng không phải "active", chuyển đến MainActivity (hoặc xử lý khác tùy yêu cầu)
                        intent = new Intent(Login_userActivity.this, MainActivity.class);
                        intent.putExtra("maU", userID);
                    }

                    if (intent != null) {
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login_userActivity.this, "Login failed: Invalid role or inactive status", Toast.LENGTH_SHORT).show();
                    }
                }
                startActivity(intent);
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(Login_userActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Login_userActivity.this, "Cannot connect to database", Toast.LENGTH_SHORT).show();
        }
    }

    private void showError(@NonNull EditText Edt, String s) {
        Edt.setError(s);
        Edt.requestFocus();
    }

    public void AddControl() {
        EdtEmail = findViewById(R.id.EdtEmail);
        EdtPassword = findViewById(R.id.EdtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvSignup = findViewById(R.id.tvSignup);
        btn_back = findViewById(R.id.btn_back);
    }
}