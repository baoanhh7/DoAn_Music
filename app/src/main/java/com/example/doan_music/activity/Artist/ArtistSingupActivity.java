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

import com.example.doan_music.R;
import com.example.doan_music.designPattern.DependencyInjectionPK.Class.SQLDatabaseService;
import com.example.doan_music.designPattern.DependencyInjectionPK.DatabaseService;
import com.example.doan_music.designPattern.DependencyInjectionPK.IF.UserRepository;
import com.example.doan_music.designPattern.DependencyInjectionPK.Model.SQLUserRepository;
import com.example.doan_music.designPattern.DependencyInjectionPK.Model.User;

public class ArtistSingupActivity extends AppCompatActivity {

    private EditText username, email, phone, password, confirmPassword;
    private Button btnBack, btnConfirm;
    private TextView title;
    private ImageView imagePlaceholder;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_artist_singup);

        // Khởi tạo UserRepository với Dependency Injection
        DatabaseService databaseService = new SQLDatabaseService();
        userRepository = new SQLUserRepository(databaseService);

        addControls();
        setupListeners();
    }

    private void setupListeners() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    User user = new User(
                            username.getText().toString(),
                            email.getText().toString(),
                            phone.getText().toString(),
                            password.getText().toString(),
                            "pending"
                    );

                    if (userRepository.saveUser(user)) {
                        Toast.makeText(ArtistSingupActivity.this, "Tài khoản đăng kí thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ArtistSingupActivity.this, "Lỗi khi lưu tài khoản", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArtistSingupActivity.this, StarArtistActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validateInputs() {
        String userStr = username.getText().toString();
        String userEmail = email.getText().toString();
        String userPhone = phone.getText().toString();
        String userPassword = password.getText().toString();
        String userConfirmPassword = confirmPassword.getText().toString();

        if (userStr.isEmpty() || userEmail.isEmpty() || userPhone.isEmpty() ||
                userPassword.isEmpty() || userConfirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!userPassword.equals(userConfirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
}
