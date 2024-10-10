package com.example.doan_music.activity.Artist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

public class ArtistSingupActivity extends AppCompatActivity {
    EditText username, email, phone, password, confirmPassword;
    Button btnBack, btnConfirm;
    TextView title;
    ImageView imagePlaceholder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_artist_singup);
        addControls();

        // Xử lý sự kiện click btnConfirm
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String userEmail = email.getText().toString();
                String userPhone = phone.getText().toString();
                String userPassword = password.getText().toString();
                String userConfirmPassword = confirmPassword.getText().toString();

                // Kiểm tra các trường đã được nhập đầy đủ và mật khẩu xác nhận đúng
                if (user.isEmpty() || userEmail.isEmpty() || userPhone.isEmpty() || userPassword.isEmpty() || userConfirmPassword.isEmpty()) {
                    Toast.makeText(ArtistSingupActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (!userPassword.equals(userConfirmPassword)) {
                    Toast.makeText(ArtistSingupActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                } else {
                    // Gọi phương thức để lưu thông tin tài khoản vào cơ sở dữ liệu
                    saveAccount(user, userEmail, userPhone, userPassword, "pending");
                }
            }
        });

        // Xử lý sự kiện btnBack
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArtistSingupActivity.this, StarArtistActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void addControls() {
         title = findViewById(R.id.title);
         imagePlaceholder = findViewById(R.id.imagePlaceholder);
         username = findViewById(R.id.username);
         email = findViewById(R.id.email);
         phone = findViewById(R.id.phone);
         password = findViewById(R.id.password);
         confirmPassword = findViewById(R.id.confirm_password);
         btnBack = findViewById(R.id.btn_back);
         btnConfirm = findViewById(R.id.btn_confirm);
    }
    private void saveAccount(String username, String email, String phone, String password, String status) {
        ConnectionClass connectionClass = new ConnectionClass(); // Lớp kết nối của bạn
        Connection con = connectionClass.conClass();

        if (con != null) {
            try {
                String query = "INSERT INTO Users (username, email, phone, password, status) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, phone);
                preparedStatement.setString(4, password);
                preparedStatement.setString(5, status);

                // Thực thi câu lệnh SQL
                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    Toast.makeText(ArtistSingupActivity.this, "Tài khoản đăng kí thành công", Toast.LENGTH_SHORT).show();
                }

                con.close(); // Đóng kết nối sau khi thực hiện
            } catch (Exception e) {
                Log.e("Database Error", e.getMessage());
                Toast.makeText(ArtistSingupActivity.this, "Lỗi khi lưu tài khoản", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ArtistSingupActivity.this, "Không thể kết nối cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }
}