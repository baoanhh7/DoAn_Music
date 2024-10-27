package com.example.doan_music.offline;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.doan_music.offline.model.SongOffline;
import com.example.doan_music.offline.model.UserOffline;

import java.io.IOException;

// OfflineMusicService.java
public class OfflineMusicService extends Service {
    private MediaPlayer mediaPlayer;
    private UserOffline currentUser;
    private UserPreferences userPreferences;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currentUser = userPreferences.getUserFromPreferences(); // Get current user info
        return START_NOT_STICKY;
    }


    public void playSong(SongOffline song) {
        // Kiểm tra quyền premium trước khi phát
        if (!currentUser.isPremiumValid()) {
            stopSelf();
            sendPremiumRequiredBroadcast();
            return;
        }

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getLinkSong());
            mediaPlayer.prepare();
            mediaPlayer.start();
            //updateNotification(song);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPremiumRequiredBroadcast() {
        Intent intent = new Intent("PREMIUM_REQUIRED");
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}