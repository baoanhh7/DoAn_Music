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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

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
import java.util.concurrent.Executor;

public class Login_userActivity extends AppCompatActivity {
    EditText EdtEmail, EdtPassword;
    ImageButton btnFingerprint;
    TextView tvForgotPass, tvSignup;
    Button btnLogin, btn_back;
    SQLiteDatabase database = null;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    Intent intent;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        AddControl();
        setupBiometricPrompt();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();
            }
        });

        btnFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
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

    private void checkCredentials() {
        String email = EdtEmail.getText().toString();
        String password = EdtPassword.getText().toString();

        if (email.isEmpty()) {
            showError(EdtEmail, "Please enter your email/phone/username");
            return;
        }

        if (password.isEmpty() || password.length() < 7) {
            showError(EdtPassword, "Password must be at least 7 characters");
            return;
        }

        // If credentials are valid, proceed with database check
        checkDatabase();
    }

    private void checkDatabase() {
        connection = new ConnectionClass().conClass();
        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (connection != null) {
            try {
                query = "SELECT UserID, Role, Status, DeviceID FROM Users WHERE (Phone = ? OR Email = ? OR Username = ?) AND Password = ?";
                PreparedStatement smt = connection.prepareStatement(query);
                smt.setString(1, EdtEmail.getText().toString());
                smt.setString(2, EdtEmail.getText().toString());
                smt.setString(3, EdtEmail.getText().toString());
                smt.setString(4, EdtPassword.getText().toString());

                resultSet = smt.executeQuery();

                if (resultSet.next()) {
                    Integer userID = resultSet.getInt("UserID");
                    String role = resultSet.getString("Role");
                    String status = resultSet.getString("Status");
                    String deviceID = resultSet.getString("DeviceID");
                    String currentDeviceID = getDeviceID();

                    // Show success message
                    Toast.makeText(Login_userActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    // Check if DeviceID is null or empty
                    if (deviceID == null || deviceID.isEmpty()) {
                        // Show confirmation dialog for saving DeviceID
                        new AlertDialog.Builder(this)
                                .setTitle("Enable Fingerprint Login")
                                .setMessage("Would you like to enable fingerprint login for future use?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            // Update DeviceID in database
                                            PreparedStatement updateSmt = connection.prepareStatement(
                                                    "UPDATE Users SET DeviceID = ? WHERE UserID = ?");
                                            updateSmt.setString(1, currentDeviceID);
                                            updateSmt.setInt(2, userID);
                                            updateSmt.executeUpdate();

                                            Toast.makeText(Login_userActivity.this,
                                                    "Fingerprint login enabled", Toast.LENGTH_SHORT).show();

                                            // Proceed with login
                                            navigateByRole(role, status, userID, editor);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            Toast.makeText(Login_userActivity.this,
                                                    "Failed to enable fingerprint login", Toast.LENGTH_SHORT).show();
                                            // Still proceed with login even if saving DeviceID fails
                                            navigateByRole(role, status, userID, editor);
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Proceed with login without saving DeviceID
                                        navigateByRole(role, status, userID, editor);
                                    }
                                })
                                .show();
                    } else {
                        // DeviceID exists, proceed with normal login
                        navigateByRole(role, status, userID, editor);
                    }
                } else {
                    Toast.makeText(Login_userActivity.this,
                            "Invalid email/phone/username or password", Toast.LENGTH_SHORT).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(Login_userActivity.this,
                        "Database error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Login_userActivity.this,
                    "Cannot connect to database", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(Login_userActivity.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        String currentDeviceID = getDeviceID();
                        connection = new ConnectionClass().conClass();

                        if (connection != null) {
                            try {
                                query = "SELECT UserID, Role, Status FROM Users WHERE DeviceID = ? AND Status = 'active'";
                                PreparedStatement smt = connection.prepareStatement(query);
                                smt.setString(1, currentDeviceID);
                                resultSet = smt.executeQuery();

                                if (resultSet.next()) {
                                    Integer userID = resultSet.getInt("UserID");
                                    String role = resultSet.getString("Role");
                                    String status = resultSet.getString("Status");

                                    SharedPreferences preferences = getSharedPreferences("data",
                                            Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();

                                    Toast.makeText(Login_userActivity.this,
                                            "Fingerprint authentication successful", Toast.LENGTH_SHORT).show();

                                    // Navigate with the same logic as password login
                                    navigateByRole(role, status, userID, editor);
                                } else {
                                    Toast.makeText(Login_userActivity.this,
                                            "No active account found for this device",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Toast.makeText(Login_userActivity.this,
                                        "Database error", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Login_userActivity.this,
                                    "Cannot connect to database", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onAuthenticationError(int errorCode,
                                                      @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(Login_userActivity.this,
                                "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(Login_userActivity.this,
                                "Fingerprint not recognized", Toast.LENGTH_SHORT).show();
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Login")
                .setSubtitle("Log in using your fingerprint")
                .setNegativeButtonText("Cancel")
                .build();
    }

    private void navigateByRole(String role, String status, int userID,
                                SharedPreferences.Editor editor) {
        intent = null;

        if ("admin".equalsIgnoreCase(role)) {
            intent = new Intent(Login_userActivity.this, AdminActivity.class);
            editor.putInt("userID", userID);
            editor.apply();
        } else if (("member".equalsIgnoreCase(role) ||
                "premium".equalsIgnoreCase(role)) &&
                "active".equalsIgnoreCase(status)) {
            intent = new Intent(Login_userActivity.this, MainActivity.class);
            intent.putExtra("maU", userID);
            editor.putInt("userID", userID);
            editor.apply();
        } else if ("artist".equalsIgnoreCase(role) &&
                "active".equalsIgnoreCase(status)) {
            intent = new Intent(Login_userActivity.this, HomeArtistActivity.class);
            intent.putExtra("UserID", userID);
            editor.putInt("userID", userID);
            editor.apply();
        }

        if (intent != null) {
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(Login_userActivity.this,
                    "Login failed: Invalid role or inactive status",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String getDeviceID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void showError(@NonNull EditText input, String message) {
        input.setError(message);
        input.requestFocus();
    }

    private void AddControl() {
        EdtEmail = findViewById(R.id.EdtEmail);
        EdtPassword = findViewById(R.id.EdtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvSignup = findViewById(R.id.tvSignup);
        btn_back = findViewById(R.id.btn_back);
        btnFingerprint = findViewById(R.id.btnFingerprint);
    }
}

