package com.example.doan_music.loginPackage;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.doan_music.R;
import com.example.doan_music.activity.MainActivity;
import com.example.doan_music.data.DbHelper;
import com.example.doan_music.database.ConnectionClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class RegisterPhoneUserActivity extends AppCompatActivity {

    EditText EdtUsername, EdtEmail, EdtPassword, EdtRepassword, EdtPhone;
    Button btnRegister, btn_back;
    TextView tvLogin;
    DbHelper dbHelper;
    SQLiteDatabase database = null;
    FirebaseAuth auth;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);
        auth = FirebaseAuth.getInstance();
        addControls();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();

            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterPhoneUserActivity.this, Login_userActivity.class));
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterPhoneUserActivity.this, UserActivity.class));
            }
        });
    }

    private void checkCredentials() {
        String username = EdtUsername.getText().toString();
        //String email = EdtEmail.getText().toString();
        String phone = EdtPhone.getText().toString();
        String password = EdtPassword.getText().toString();
        String repassword = EdtRepassword.getText().toString();


        if (username.isEmpty()) {
            showError(EdtUsername, "Tên đăng nhập không được để trống");
        }
//        else if (email.isEmpty() || !email.contains("@gmail.com")) {
//            showError(EdtEmail, "Your Email is not valid!");
//        }
        else if (password.isEmpty() || password.length() < 7) {
            showError(EdtPassword, "Your password must be at least 8 character");
        } else if (repassword.isEmpty() || !repassword.equals(password)) {
            showError(EdtRepassword, "Your password is not match");
        } else if (phone.length() != 10) {
            showError(EdtPhone, "Your phone is not valid!");
        } else {
            connection = new ConnectionClass().conClass();
            if (connection != null) {
                try {
                    query = "SELECT * FROM Users ";
                    smt = connection.createStatement();
                    resultSet = smt.executeQuery(query);
                    while (resultSet.next()) {
                        String Phone = resultSet.getString(6);
                        String UserName = resultSet.getString(2);
                        if (phone.equals(Phone)) {
                            showError(EdtPhone, "Số điện thoại đã được đăng ký");
                            return;
                        } else if (username.equals(UserName)) {
                            showError(EdtUsername, "Tên đăng nhập đã được dùng");
                            return;
                        }
                    }
                    connection.close();
                    gotoOTP(EdtPhone.getText().toString().trim());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
//            database = openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
//            Cursor cursor = database.rawQuery("select * from Users", null);
//            while (cursor.moveToNext()) {
//                String Email = cursor.getString(2);
//                if (email.equals(Email)) {
//                    showError(EdtEmail, "This email has been registered");
//                    return;
//                }
//            }
//            cursor.close();
            onClickverifyPhone(phone);
        }
    }


    private void onClickverifyPhone(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                // This callback will be invoked in two situations:
                                // 1 - Instant verification. In some cases the phone number can be instantly
                                //     verified without needing to send or enter a verification code.
                                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                                //     detect the incoming verification SMS and perform verification without
                                //     user action.

                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(RegisterPhoneUserActivity.this, "Verification Fail ", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                //gotoOTP(phone, verificationId);
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void gotoOTP(String phone) {
        Intent intent = new Intent(this, OTPActivity.class);
        intent.putExtra("phone", phone);
        //intent.putExtra("verification_Id", verificationId);
        intent.putExtra("Username", EdtUsername.getText().toString());
        //intent.putExtra("Email", EdtEmail.getText().toString());
        intent.putExtra("Password", EdtPassword.getText().toString());
        startActivity(intent);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            goToLogin();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(RegisterPhoneUserActivity.this, "/ The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void goToLogin() {
        // Lấy dữ liệu từ các trường nhập liệu
        String username = EdtUsername.getText().toString();
        String phone = EdtPhone.getText().toString();
        String password = EdtPassword.getText().toString();

        // Tạo kết nối SQL Server
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass(); // Tạo kết nối

        if (connection != null) {
            try {
                // Truy vấn SQL để chèn dữ liệu vào bảng Users
                String query = "INSERT INTO Users (Username, Phone, Password, Role) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, phone);
                preparedStatement.setString(3, password);
                preparedStatement.setString(3, "member");
                // Thực thi truy vấn
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    Toast.makeText(this, "Register Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterPhoneUserActivity.this, Login_userActivity.class));
                } else {
                    Toast.makeText(RegisterPhoneUserActivity.this, "Register Fail", Toast.LENGTH_SHORT).show();
                }

                // Đóng kết nối
                connection.close();
            } catch (SQLException e) {
                Log.e("Error: ", e.getMessage());
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("Error: ", "Connection is null");
            Toast.makeText(this, "Connection to database failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void showError(EditText Edt, String s) {
        Edt.setError(s);
        Edt.requestFocus();
    }

    private void addControls() {
        EdtUsername = findViewById(R.id.EdtUsername);
        EdtEmail = findViewById(R.id.EdtEmail);
        EdtPassword = findViewById(R.id.EdtPassword);
        EdtRepassword = findViewById(R.id.EdtRepassword);
        btnRegister = findViewById(R.id.btnRegister);
        EdtPhone = findViewById(R.id.EdtPhone);
        tvLogin = findViewById(R.id.tvLogin);
        btn_back = findViewById(R.id.btn_back);
    }
}