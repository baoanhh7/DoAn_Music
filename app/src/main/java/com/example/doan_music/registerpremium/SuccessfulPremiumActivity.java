package com.example.doan_music.registerpremium;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan_music.R;
import com.example.doan_music.activity.MainActivity;
import com.example.doan_music.activity.SplashActivity;
import com.example.doan_music.loginPackage.BeginActivity;

public class SuccessfulPremiumActivity extends AppCompatActivity {
 TextView txt_notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_successful_premium);
        addControls();
        Intent intent = getIntent();
        txt_notification.setText(intent.getStringExtra("resultPremium"));
        if(txt_notification.getText().toString().equals("Thanh toan thanh cong")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SuccessfulPremiumActivity.this, MainActivity.class));
                    finish();
                }
            }, 5555);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 5555);
        }
    }

    private void addControls() {
        txt_notification = findViewById(R.id.txt_notification);
    }
}