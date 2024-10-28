package com.example.doan_music.loginPackage;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_music.R;
import com.example.doan_music.activity.Artist.StarArtistActivity;
import com.example.doan_music.offline.LoginOfflineActivity;


public class BeginActivity extends AppCompatActivity {

    Button Artist, User;
    private boolean isOnline = true;
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager connectivityManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user2);
        addControls();
        // Kiểm tra trạng thái mạng ngay khi khởi động
        checkInitialNetworkState();
        setupNetworkCallback();
        Artist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeginActivity.this, StarArtistActivity.class);
                startActivity(intent);
            }
        });
        User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeginActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkInitialNetworkState() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        isOnline = capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        handleNetworkChange(isOnline);
    }
    private void setupNetworkCallback() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                runOnUiThread(() -> {
                    isOnline = true;
                    handleNetworkChange(true);
                });
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> {
                    isOnline = false;
                    handleNetworkChange(false);
                });
            }
        };

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    private void handleNetworkChange(boolean isOnline) {
        if (isOnline) {
            // Xử lý khi có kết nối internet
            Toast.makeText(this, "Đã kết nối internet", Toast.LENGTH_LONG).show();
            // Thực hiện các tác vụ online
        } else {
            // Xử lý khi mất kết nối internet
            Toast.makeText(this, "Mất kết nối internet", Toast.LENGTH_LONG).show();
            // Chuyển sang chế độ offline
            startActivity(new Intent(BeginActivity.this, LoginOfflineActivity.class));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Kiểm tra lại trạng thái mạng khi activity được resume
        checkInitialNetworkState();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityManager != null && networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception e) {
                // Ignore exception if callback is already unregistered
            }
        }
    }

    private void addControls() {
        Artist = findViewById(R.id.Artist);
        User = findViewById(R.id.User);
    }
}