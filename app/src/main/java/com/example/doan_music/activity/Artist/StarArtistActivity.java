package com.example.doan_music.activity.Artist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_music.R;
import com.example.doan_music.loginPackage.Login_userActivity;

public class StarArtistActivity extends AppCompatActivity {
    Button ArtistSingin, ArtistSingup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_artist); // Thiết lập layout trước
        EdgeToEdge.enable(this); // Sau đó mới kích hoạt EdgeToEdge
        addControls();

        ArtistSingin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StarArtistActivity.this, Login_userActivity.class);
                startActivity(intent);
            }
        });

        ArtistSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StarArtistActivity.this, ArtistSingupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addControls() {
        ArtistSingin = findViewById(R.id.ArtistSingin);
        ArtistSingup = findViewById(R.id.ArtistSingup);
    }
}
