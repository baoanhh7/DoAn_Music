package com.example.doan_music.offline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan_music.R;
import com.example.doan_music.loginPackage.Login_userActivity;

public class LoginOfflineActivity extends AppCompatActivity {
    EditText EdtEmail_loginoffline, EdtPassword_loginoffline;
    Button btnLogin_loginoffline, btn_back_loginoffline;
    SQLiteDatabase database = null;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_offline);
        AddControl();
        btnLogin_loginoffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCrededentials();
                //checkArtistStatus();
            }
        });
        btn_back_loginoffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void AddControl() {
        EdtEmail_loginoffline = findViewById(R.id.EdtEmail_loginoffline);
        EdtPassword_loginoffline = findViewById(R.id.EdtPassword_loginoffline);
        btnLogin_loginoffline = findViewById(R.id.btnLogin_loginoffline);
        btn_back_loginoffline = findViewById(R.id.btn_back_loginoffline);

    }

    private void checkCrededentials() {
        //String email = EdtEmail_loginoffline.getText().toString();
        String password = EdtPassword_loginoffline.getText().toString();

        if (password.isEmpty() || password.length() < 7) {
            showError(EdtPassword_loginoffline, "Your password must be 7 character");
        } else {
            //checkDatabase();
            checkDatabaseSQLite();
        }
    }

    private void checkDatabaseSQLite() {
        String email = EdtEmail_loginoffline.getText().toString();
        String password = EdtPassword_loginoffline.getText().toString();
        database = openOrCreateDatabase("music_db", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select * from users", null);
        Intent intent = null;
        while (cursor.moveToNext()) {
            Integer ma = cursor.getInt(0);
            String Name = cursor.getString(1);
            String Password = cursor.getString(2);
            Integer IsPremium = cursor.getInt(3);
            if (email.equals(Name) && password.equals(Password)&& IsPremium == 1) {
                // Xử lý đăng nhập thành công
                Toast.makeText(LoginOfflineActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                // Nếu là người dùng thông thường, chuyển đến BeginActivity
                intent = new Intent(LoginOfflineActivity.this, ListSongOffActivity.class);
                //intent.putExtra("emailU", Email);
                // intent.putExtra("code",code);
                //intent.putExtra("maU", ma);
                //intent.putExtra("tenU", Name);

                SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("userID", ma);
                editor.apply();
                startActivity(intent);
                break;
            }
        }
        cursor.close();
        if (intent == null)
            Toast.makeText(LoginOfflineActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
    }

    private void showError(@NonNull EditText Edt, String s) {
        Edt.setError(s);
        Edt.requestFocus();
    }
}