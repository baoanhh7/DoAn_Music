package com.example.doan_music.music;

import static com.example.doan_music.music.MyNoti.CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.doan_music.LoadImage.LoadImageTask;
import com.example.doan_music.Lyric.LRCParser;
import com.example.doan_music.Lyric.LyricsManager;
import com.example.doan_music.Lyric.LyricsSyncManager;
import com.example.doan_music.R;
import com.example.doan_music.activity.MainActivity;
import com.example.doan_music.data.DbHelper;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.offline.MusicDownloadManager;
import com.example.doan_music.offline.model.SongOffline;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PlayMusicActivity extends AppCompatActivity {
    private static final long REFRESH_INTERVAL = 1000;
    ImageButton btn_home, btn_play, btn_back, btn_next, btn_pre, btn_toggle, btn_shuffle, btn_volume, btn_heart, btn_download;
    SeekBar seekBar, seekbar1;
    TextView txt_time, txt_time_first, txt_view_playmusic, txt_lyric;
    //MediaPlayer myMusic;
    AudioManager audioManager;
    ArrayList<Integer> arr;
    ImageView imageView_songs;
    TextView txt_artist_song, txt_name_song;
    Integer currentPosition = -1;
    Integer Positionshuffle = -1;
    boolean Isshuffle = false;
    SQLiteDatabase database = null;
    DbHelper dbHelper;
    Animation animation;
    // Tạo một biến Bitmap để lưu hình ảnh
//    Bitmap bitmapResult;
//    Drawable drawable;
    Bitmap bitmap;
    Integer IDSong;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    int userID;
    String Role;
    String linkLRC;
    private boolean frag = true;
    private boolean frag_heart = false;
    private InterstitialAd mInterstitialAd;
    private Map<Integer, String> lyricsMap = new HashMap<>();
    private Handler handler = new Handler();
    private Runnable refreshRunnable;
    private LyricsManager lyricsManager;
    SongOffline songOffline, songOffline2;
    MusicDownloadManager musicDownloadManager;
    private MediaPlayerManager playerManager;
    private Playback playBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", -1);  // Lấy userID
        Log.e("UserID", String.valueOf(userID));

        IDSong = getIntent().getIntExtra("SongID", -1);
        addControls();
        arr = (ArrayList<Integer>) getIntent().getSerializableExtra("arrIDSongs");
        if (Isshuffle) {
            currentPosition = getRandom(arr.size() - 1);
        } else {
            currentPosition = arr.indexOf(IDSong);
        }
//        SeekBar sbTime;
//        myMusic = new MediaPlayer();
        // Singleton
//        myMusic = MediaPlayerManager.getInstance().getMediaPlayer();
        //myMusic = MediaPlayer.create(this, R.raw.nhung_loi_hua_bo_quen);

        loadRoleUser(userID);

//        updateHistorySong(IDSong);
//        updateViewSong(IDSong);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Lấy instance đã được cấu hình trước đó
        playerManager = MediaPlayerManager.getInstance();
        // Hoặc cấu hình lại nếu cần
        new MediaPlayerManager.Builder().setVolume(60).apply();
//        new MediaPlayerManager.Builder()
//                .setLooping(false) // Phát lặp lại
//                .setSpeed(1.0f) // Tăng tốc độ phát
//                .setVolume(80) // Âm lượng 80%
//                .apply(); // Cập nhật cấu hình cho Singleton
//        MediaPlayerManager playerManager = MediaPlayerManager.getInstance(); // Lấy Singleton
        //myMusic = playerManager.getMediaPlayer(); // Lấy MediaPlayer
//        playerManager.setDataSource(loadDataSong(IDSong));
//        playerManager.play();
//        myMusic.seekTo(0);
//        myMusic.start();
//        myMusic.setLooping(false);
        lyricsManager = new LyricsManager(playerManager.getMediaPlayer(), txt_lyric);
        playNewSong(IDSong);
        seekbar1.setVisibility(View.GONE);
        // tạo biến duration để lưu thời gian bài hát
//        String duration = timeSeekbar(playerManager.getMediaPlayer().getDuration());
//        txt_time.setText(duration);
//        loadNameArtist(IDSong);
//        sendNotification();
//        loadLRC(IDSong);
        txt_lyric.setText("");
//        updateHeartButtonUI();

//        addSongToHistory(userID, IDSong);
//        updateSongHistory(userID, IDSong);

        volume();
        // Đọc file LRC và lưu trữ lời bài hát

//        readLRCFile();

//        refreshRunnable = new Runnable() {
//            @Override
//            public void run() {
//
//                updateLyrics();
//                handler.postDelayed(this, REFRESH_INTERVAL);
//            }
//        };
//        if (Role.equalsIgnoreCase("member")) {
//            createADS();
//            txt_lyric.setText("premium de mo tinh nang");
//        }
//        if (Role.equalsIgnoreCase("premium")) {
//            lyricsManager.loadLyrics(linkLRC);
//        }
        // Bắt đầu cập nhật lời bài hát
        if (frag) {
            playerManager.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNextSong(arr);
                }
            });
        }

        addEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Giải phóng resources
        if (lyricsManager != null) {
            lyricsManager.release();
        }
        playerManager.release();
    }

    private void addSongToHistory(int userID, Integer songID) {
        ConnectionClass sql = new ConnectionClass();

        String query = "INSERT INTO SongHistory (UserID, SongID) VALUES (?, ?)";
        try (Connection connection = sql.conClass(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, songID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Log.e("Error", "Không thể thêm bài hát vào lịch sử: " + e.getMessage());
        }
    }

    private void updateSongHistory(int userID, int songID) {
        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();

        if (connection != null) {
            try {
                // Kiểm tra trùng lặp
                String checkQuery = "SELECT COUNT(*) FROM SongHistory WHERE UserID = ? AND SongID = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setInt(1, userID);
                checkStatement.setInt(2, songID);
                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // Xóa bản ghi cũ
                    String deleteQuery = "DELETE FROM SongHistory WHERE UserID = ? AND SongID = ?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                    deleteStatement.setInt(1, userID);
                    deleteStatement.setInt(2, songID);
                    deleteStatement.executeUpdate();
                }

                // Thêm bản ghi mới
                String insertQuery = "INSERT INTO SongHistory (UserID, SongID, PlayTime) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setInt(1, userID);
                insertStatement.setInt(2, songID);
                insertStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                insertStatement.executeUpdate();

                resultSet.close();
                checkStatement.close();
                connection.close();

            } catch (SQLException e) {
                Log.e("Error", e.getMessage());
            }
        } else {
            Log.e("Error", "Connection is null");
        }
    }

    private void loadLRC(Integer idSong) {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "SELECT LinkLRC FROM Song WHERE SongID = " + idSong;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                if (resultSet.next()) {
                    linkLRC = resultSet.getString(1);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        startAutoRefresh();
//    }
//
//    private void startAutoRefresh() {
//        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
//    }

    private void loadRoleUser(int userID) {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "SELECT * FROM Users WHERE UserID = " + userID;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                if (resultSet.next()) {
                    Role = resultSet.getString(5);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }

    }

    private void createADS() {

        new Thread(() -> {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this, initializationStatus -> {
            });
        }).start();
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i("ADS", "onAdLoaded");
                if (mInterstitialAd != null) {
//                            myMusic.pause();
                    playerManager.pause();
                    mInterstitialAd.show(PlayMusicActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdClicked() {
                            // Called when a click is recorded for an ad.
                            Log.d("ADS", "Ad was clicked.");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            playerManager.play();
                            Log.d("ADS", "Ad dismissed fullscreen content.");
                            mInterstitialAd = null;
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.e("ADS", "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                        }

                        @Override
                        public void onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            Log.d("ADS", "Ad recorded an impression.");
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            Log.d("ADS", "Ad showed fullscreen content.");
                        }
                    });
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.d("ADS", loadAdError.toString());
                mInterstitialAd = null;
            }
        });
    }


    private void updateViewSong(Integer idSong) {
        // Kiểm tra nếu userID hợp lệ
        if (userID != -1) {
            ConnectionClass sql = new ConnectionClass();
            connection = sql.conClass();  // Tạo kết nối SQL Server

            if (connection != null) {
                try {
                    // Cập nhật thứ tự cho tất cả các bài hát nếu bài hát đã tồn tại
                    String updateQuery = "UPDATE Song SET Views = Views + 1 WHERE SongID = ?";
                    PreparedStatement updateOrderStatement = connection.prepareStatement(updateQuery);
                    updateOrderStatement.setInt(1, idSong);
                    updateOrderStatement.executeUpdate();
                    connection.close();  // Đóng kết nối
                } catch (SQLException e) {
                    Log.e("Error", e.getMessage());
                }
            } else {
                Log.e("Error", "Connection is null");
            }
        } else {
            Log.e("Error", "Invalid userID");
        }
    }

    private void updateHistorySong(int id) {
        // Kiểm tra nếu userID hợp lệ
        if (userID != -1) {
            ConnectionClass sql = new ConnectionClass();
            connection = sql.conClass();  // Tạo kết nối SQL Server

            if (connection != null) {
                try {
                    // Kiểm tra xem bài hát đã tồn tại trong lịch sử chưa
                    String checkQuery = "SELECT COUNT(*) FROM HistorySong WHERE UserID = ? AND SongID = ?";
                    PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                    checkStatement.setInt(1, userID);
                    checkStatement.setInt(2, id);

                    ResultSet resultSet = checkStatement.executeQuery();
                    boolean songExists = false;

                    if (resultSet.next()) {
                        songExists = true;
                    }

                    // Cập nhật thứ tự cho tất cả các bài hát nếu bài hát đã tồn tại
                    String updateOrderQuery = "UPDATE HistorySong SET OrderIndex = OrderIndex + 1 WHERE UserID = ?";
                    PreparedStatement updateOrderStatement = connection.prepareStatement(updateOrderQuery);
                    updateOrderStatement.setInt(1, userID);
                    updateOrderStatement.executeUpdate();

                    if (songExists) {
                        // Cập nhật lại thứ tự cho bài hát đã nghe
                        String updateSongOrderQuery = "UPDATE HistorySong SET OrderIndex = 0 WHERE UserID = ? AND SongID = ?";
                        PreparedStatement updateSongOrderStatement = connection.prepareStatement(updateSongOrderQuery);
                        updateSongOrderStatement.setInt(1, userID);
                        updateSongOrderStatement.setInt(2, id);
                        updateSongOrderStatement.executeUpdate();
                        Log.i("Info", "Song order updated successfully!");
                    } else {
                        // Nếu bài hát chưa tồn tại, thêm vào lịch sử
                        String insertQuery = "INSERT INTO HistorySong (UserID, SongID, OrderIndex) VALUES (?, ?, 0)";
                        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                        insertStatement.setInt(1, userID);
                        insertStatement.setInt(2, id);
                        insertStatement.executeUpdate();
                        Log.i("Info", "Song added to history successfully!");
                    }

                    connection.close();  // Đóng kết nối
                } catch (SQLException e) {
                    Log.e("Error", e.getMessage());
                }
            } else {
                Log.e("Error", "Connection is null");
            }
        } else {
            Log.e("Error", "Invalid userID");
        }
    }

    private Integer getRandom(int i) {
        Random random = new Random();

        return random.nextInt(i + 1);
    }

    private void sendNotification() {
        // Tiếp tục xử lý với bitmap

        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "tag");
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_headphone).setSubText("Music").setContentTitle(txt_artist_song.getText().toString()).setContentText(txt_name_song.getText().toString()).setLargeIcon(bitmap)
                // Add media control buttons that invoke intents in your media service
                .addAction(R.drawable.ic_pre, "Previous", null) // #0
                .addAction(R.drawable.ic_pause, "Pause", null)  // #1
                .addAction(R.drawable.ic_next, "Next", null)     // #2
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2 /* #1: pause button */)).build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.notify(1, notification);
    }

    private void volume() {
        int maxV = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curV = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekbar1.setMax(maxV);
        seekbar1.setProgress(curV);
        seekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void loadNameArtist(int id) {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "select * from Artist" + " JOIN Song ON Artist.ArtistID = Song.ArtistID " + " WHERE Song.SongID =  " + id;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                while (resultSet.next()) {
                    String ten = resultSet.getString(2);
                    txt_artist_song.setText(ten);
                    SharedPreferences sharedPreferences = getSharedPreferences("musicData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("ArtistName", ten); // Lưu tên nghệ sĩ
                    editor.apply(); // Lưu thay đổi
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

//    private void loadData() {
//        loadDataSong(IDSong);
//    }

    private String loadDataSong(int id) {
        String source = "";
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "SELECT * FROM Song WHERE SongID = " + id;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                while (resultSet.next()) {
                    Integer Id = resultSet.getInt(1);
                    String ten = resultSet.getString(2);
                    String img = resultSet.getString(3);
                    int view = resultSet.getInt(8);
                    String linkSong = resultSet.getString(6);

                    //bitmap = ImagetoBitmap.getBitmapFromURL(linkSong);
                    // Tải ảnh từ URL và hiển thị
                    new LoadImageTask(imageView_songs).execute(img);
                    Drawable drawable = imageView_songs.getDrawable();
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                    txt_name_song.setText(ten);

                    SharedPreferences sharedPreferences = getSharedPreferences("musicData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("SongName", ten); // Lưu tên bài hát
                    editor.putInt("SongID", id);
                    editor.apply(); // Lưu thay đổi

                    txt_view_playmusic.setText(view + "");
                    source = linkSong;
//                    try {
//                        myMusic.setDataSource(linkSong);
//                        myMusic.prepare();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
        return source;
    }

    public String timeSeekbar(int time) {
        String mTime = "";
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        mTime = minutes + ":";
        if (seconds < 10) {
            mTime += "0";
        }
        mTime += seconds;
        return mTime;

    }

    private void addEvents() {
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Role.equalsIgnoreCase("premium")) {
                    saveDataSongOff(IDSong);
                    musicDownloadManager = new MusicDownloadManager(PlayMusicActivity.this, userID);
                    try {
                        songOffline2 = songOffline;
                        if (musicDownloadManager.downloadSong(songOffline2)) {
                            Log.e("Namenghesi", songOffline2.getSongName());
                            Toast.makeText(PlayMusicActivity.this, "Tải nhạc thành công", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else
                    Toast.makeText(PlayMusicActivity.this, "Tài khoản premium mới có thể tải nhạc", Toast.LENGTH_LONG).show();
            }
        });
        btn_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Integer IDSong = getIntent().getIntExtra("SongID", -1);
                Integer IDSong = arr.get(currentPosition);

                // Đảo ngược trạng thái yêu thích (nếu đang yêu thích -> không yêu thích và ngược lại)
                frag_heart = !frag_heart;

                if (frag_heart) {
                    btn_heart.setImageResource(R.drawable.ic_red_heart);

                    addSongToLoveList(IDSong);
                } else {
                    btn_heart.setImageResource(R.drawable.ic_heart);
                    removeSongFromLoveList(IDSong);
                }
            }
        });
        setupVolumeControl();
        setupPlaybackControls();
        setupNavigationControls();
        setupProgressBar();
        // set giới hạn Max cho thanh seekBar
//        seekBar.setMax(playerManager.getDuration());
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    playerManager.seekTo(progress);
//                    seekBar.setProgress(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (playerManager != null) {
//                    if (playerManager.isPlaying()) {
//                        try {
//                            final double current = playerManager.getCurrentPosition();
//                            final String time = timeSeekbar((int) current);
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    txt_time_first.setText(time);
//                                    seekBar.setProgress((int) current);
//                                }
//                            });
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                        }
//                    }
//                }
//            }
//        }).start();
//        btn_toggle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (frag) {
//                    // Thực hiện các hành động khi nút được bật
//                    btn_toggle.setImageResource(R.drawable.ic_repeatactive);
//                    MediaPlayerManager.Builder.getInstance()
//                            .setLooping(true)
//                            .apply();
////                    myMusic.setLooping(true);
//                    frag = false;
//                } else {
//                    btn_toggle.setImageResource(R.drawable.ic_repeat);
//                    MediaPlayerManager.Builder.getInstance()
//                            .setLooping(false)
//                            .apply();
//                    //myMusic.setLooping(false);
//                    frag = true;
//                }
//            }
//        });
//        btn_shuffle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Isshuffle) {
//                    btn_shuffle.setImageResource(R.drawable.ic_shuffer);
//                    Isshuffle = false;
////                    Collections.shuffle(arr);
//                } else {
//                    btn_shuffle.setImageResource(R.drawable.ic_shufferactive);
////                    arr = shuffle;
//                    Isshuffle = true;
//                }
//            }
//        });
//        btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (playerManager.getMediaPlayer() != null) {  // Kiểm tra myMusic có null không
//                    if (playerManager.isPlaying()) {
//                        try {
//                            playerManager.stop();
//                            //myMus
//                            //myMusic.reset();
//                        } catch (IllegalStateException e) {
//                            Log.e("MediaPlayer", "Error stopping or resetting", e);
//                        }
//                    }
//                } else {
//                    Log.e("MediaPlayer", "myMusic is null");
//                }
//                finish();
//            }
//        });
//        btn_volume.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int visibility = seekbar1.getVisibility();
//                if (visibility == View.GONE) {
//                    seekbar1.setVisibility(View.VISIBLE);
//                } else
//                    seekbar1.setVisibility(View.GONE);
//            }
//        });
//        btn_home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (playerManager.getMediaPlayer().isPlaying()) {
//                    playerManager.stop();
//                    // myMusic.reset();
//                }
//
//                Intent intent = new Intent(PlayMusicActivity.this, MainActivity.class);
//                startActivity(intent);
//
//                finish();
//            }
//        });
//        btn_play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (playerManager.getMediaPlayer().isPlaying()) {
//                    playerManager.pause();
//                    btn_play.setImageResource(R.drawable.ic_play);
//                    imageView_songs.clearAnimation();
//
//                } else {
//                    playerManager.play();
//                    btn_play.setImageResource(R.drawable.ic_pause);
//
//                    // Áp dụng animation vào ImageView
//                    imageView_songs.startAnimation(animation);
//                }
//            }
//        });
//        btn_next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (playerManager.getMediaPlayer() != null) {
//                    btn_play.setImageResource(R.drawable.ic_pause);
//                }
//                if (frag) {
//                    if (Isshuffle) {
//                        Positionshuffle = currentPosition;
//                        currentPosition = getRandom(arr.size() - 1);
//                    } else if (currentPosition < arr.size() - 1) {
//                        currentPosition++;
//                    } else {
//                        currentPosition = 0;
//                    }
//
//
//                    if (playerManager.getMediaPlayer().isPlaying()) {
//                        playerManager.stop();
//                    }
//                    if (!playerManager.getMediaPlayer().isPlaying()) {
//                        playerManager.stop();
//                    }
//                    Integer idSong = arr.get(currentPosition);
//                    playerManager.setDataSource(loadDataSong(idSong));
//                    loadNameArtist(idSong);
//                    //updateHistorySong(idSong);
//                    updateSongHistory(userID, IDSong);
//                    updateViewSong(idSong);
//                    txt_lyric.setText("");
//                    sendNotification();
//                    String duration = timeSeekbar(playerManager.getDuration());
//                    txt_time.setText(duration);
//                    seekBar.setMax(playerManager.getDuration());
//                    playerManager.play();
//                    updateHeartButtonUI();
//                    loadLRC(idSong);
//                    //readLRCFile();
//                    if (Role.equalsIgnoreCase("premium")) {
//                        lyricsManager.loadLyrics(linkLRC);
//                    }
//                    imageView_songs.startAnimation(animation);
//                    if (Role.equalsIgnoreCase("member")) {
//                        createADS();
//                    }
//                } else {
//                    if (playerManager.isPlaying()) {
//                        playerManager.stop();
//                    }
//                    if (!playerManager.isPlaying()) {
//                        playerManager.stop();
//                    }
//                    Integer idSong = arr.get(currentPosition);
//                    playerManager.setDataSource(loadDataSong(idSong));
//                    loadNameArtist(idSong);
//                    //updateHistorySong(idSong);
//                    updateSongHistory(userID, IDSong);
//                    updateViewSong(idSong);
//                    txt_lyric.setText("");
//                    loadLRC(idSong);
//                    sendNotification();
//                    //readLRCFile();
//                    if (Role.equalsIgnoreCase("premium")) {
//                        lyricsManager.loadLyrics(linkLRC);
//                    }
//                    String duration = timeSeekbar(playerManager.getDuration());
//                    txt_time.setText(duration);
//                    seekBar.setMax(playerManager.getDuration());
//                    playerManager.play();
//
//                    updateHeartButtonUI();
//                    imageView_songs.startAnimation(animation);
//                    if (Role.equalsIgnoreCase("member")) {
//                        createADS();
//                    }
//                }
//            }
//        });
//        btn_pre.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (playerManager.getMediaPlayer() != null) {
//                    btn_play.setImageResource(R.drawable.ic_pause);
//                }
//                if (frag) {
//                    if (Isshuffle) {
//                        currentPosition = Positionshuffle;
//                    } else if (currentPosition > 0) {
//                        currentPosition--;
//                    } else {
//                        currentPosition = arr.size() - 1;
//                    }
//
//                    if (playerManager.isPlaying()) {
//                        playerManager.stop();
//                    }
//                    if (!playerManager.isPlaying()) {
//                        playerManager.stop();
//                    }
//                    Integer idSong = arr.get(currentPosition);
//                    playerManager.setDataSource(loadDataSong(idSong));
//                    loadNameArtist(idSong);
//                    //updateHistorySong(idSong);
//                    updateSongHistory(userID, IDSong);
//                    updateViewSong(idSong);
//                    txt_lyric.setText("");
//                    loadLRC(idSong);
//                    //readLRCFile();
//                    if (Role.equalsIgnoreCase("premium")) {
//                        lyricsManager.loadLyrics(linkLRC);
//                    }
//                    updateHeartButtonUI();
//                    sendNotification();
//                    String duration = timeSeekbar(playerManager.getDuration());
//                    txt_time.setText(duration);
//                    seekBar.setMax(playerManager.getDuration());
//                    playerManager.play();
//                    imageView_songs.startAnimation(animation);
//                    if (Role.equalsIgnoreCase("member")) {
//                        createADS();
//                    }
//                } else {
//                    if (playerManager.isPlaying()) {
//                        playerManager.stop();
//                    }
//
//                    if (!playerManager.isPlaying()) {
//                        playerManager.stop();
//                    }
//                    Integer idSong = arr.get(currentPosition);
//                    playerManager.setDataSource(loadDataSong(idSong));
//                    loadNameArtist(idSong);
//                    //updateHistorySong(idSong);
//                    updateSongHistory(userID, IDSong);
//                    txt_lyric.setText("");
//                    loadLRC(idSong);
//                    //readLRCFile();
//                    if (Role.equalsIgnoreCase("premium")) {
//                        lyricsManager.loadLyrics(linkLRC);
//                    }
//                    updateViewSong(idSong);
//                    updateHeartButtonUI();
//                    sendNotification();
//                    String duration = timeSeekbar(playerManager.getDuration());
//                    txt_time.setText(duration);
//                    seekBar.setMax(playerManager.getDuration());
//                    playerManager.play();
//                    imageView_songs.startAnimation(animation);
//                    if (Role.equalsIgnoreCase("member")) {
//                        createADS();
//                    }
//                }
//            }
//        });
    }

    private void saveDataSongOff(Integer idSong) {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "SELECT SongName,LinkSong,LinkLRC FROM Song WHERE SongID = " + idSong;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                if (resultSet.next()) {
                    String ten = resultSet.getString(1);
                    String linkSong = resultSet.getString(2);
                    String linkLRC = resultSet.getString(3);
                    //bitmap = ImagetoBitmap.getBitmapFromURL(linkSong);
                    // Tải ảnh từ URL và hiển thị
                    imageView_songs.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(imageView_songs.getDrawingCache());
                    imageView_songs.setDrawingCacheEnabled(false);

                    // Bước 2: Chuyển đổi Bitmap thành byte[]
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    songOffline = new SongOffline(idSong, linkLRC, linkSong, txt_artist_song.getText().toString().trim(), byteArray, ten);
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void updateHeartButtonUI() {
        Integer IDSong = arr.get(currentPosition);
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "SELECT * FROM User_SongLove WHERE SongID = " + IDSong + " AND UserID = " + userID;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                if (resultSet.next()) {
                    btn_heart.setImageResource(R.drawable.ic_red_heart);
                } else {
                    btn_heart.setImageResource(R.drawable.ic_heart);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
//        while (cursor.moveToNext()) {
//            int fav = cursor.getInt(6);
//            if (fav == 1) {
//                btn_heart.setImageResource(R.drawable.ic_red_heart);
//            } else {
//                btn_heart.setImageResource(R.drawable.ic_heart);
//            }
//            break;
//        }
    }

    private void removeSongFromLoveList(Integer songid) {
        // Kiểm tra nếu userID hợp lệ
        if (userID != -1) {
            ConnectionClass sql = new ConnectionClass();
            Connection connection = sql.conClass();  // Tạo kết nối SQL Server

            if (connection != null) {
                try {
                    // Truy vấn SQL để xóa dữ liệu khỏi bảng User_SongLove
                    String query = "DELETE FROM User_SongLove WHERE UserID = ? AND SongID = ?";

                    // Sử dụng PreparedStatement để bảo vệ chống SQL Injection
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, userID);  // Gán giá trị userID vào truy vấn
                    preparedStatement.setInt(2, songid);  // Gán giá trị songID vào truy vấn

                    // Thực thi truy vấn
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        Log.i("Info", "Song removed from love list successfully!");
                    } else {
                        Log.e("Error", "Failed to remove song from love list or song not found.");
                    }

                    connection.close();  // Đóng kết nối
                } catch (SQLException e) {
                    Log.e("Error", e.getMessage());
                }
            } else {
                Log.e("Error", "Connection is null");
            }
        } else {
            Log.e("Error", "Invalid userID");
        }
    }


    private void addSongToLoveList(Integer songid) {
        // Kiểm tra nếu userID hợp lệ
        if (userID != -1) {
            ConnectionClass sql = new ConnectionClass();
            connection = sql.conClass();  // Tạo kết nối SQL Server

            if (connection != null) {
                try {
                    // Truy vấn SQL để thêm dữ liệu vào bảng User_SongLove
                    String query = "INSERT INTO User_SongLove (UserID, SongID) VALUES (?, ?)";

                    // Sử dụng PreparedStatement để bảo vệ chống SQL Injection
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, userID);  // Gán giá trị userID vào truy vấn
                    preparedStatement.setInt(2, songid);  // Gán giá trị songID vào truy vấn

                    // Thực thi truy vấn
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        Log.i("Info", "Song added to love list successfully!");
                    } else {
                        Log.e("Error", "Failed to add song to love list.");
                    }

                    connection.close();  // Đóng kết nối
                } catch (SQLException e) {
                    Log.e("Error", e.getMessage());
                }
            } else {
                Log.e("Error", "Connection is null");
            }
        } else {
            Log.e("Error", "Invalid userID");
        }
    }


//    private void setFavorite(int farovite) {
//        if (farovite == 1) {
//            btn_heart.setImageResource(R.drawable.ic_red_heart);
//        } else {
//            btn_heart.setImageResource(R.drawable.ic_heart);
//        }
//    }

    private void playNextSong(@NonNull ArrayList<Integer> arr) {
        if (currentPosition < arr.size() - 1) {
            currentPosition++;
        } else {
            currentPosition = 0;
        }
        Integer idSong = arr.get(currentPosition);
        playNewSong(idSong);
//        playerManager.setDataSource(loadDataSong(idSong));
//        loadNameArtist(idSong);
//        updateHeartButtonUI();
//        updateViewSong(idSong);
//        //updateHistorySong(idSong);
//        updateSongHistory(userID, IDSong);
//        txt_lyric.setText("");
//        loadLRC(idSong);
//        //readLRCFile();
//        if (Role.equalsIgnoreCase("premium")) {
//            lyricsManager.loadLyrics(linkLRC);
//        }
//        String duration = timeSeekbar(playerManager.getDuration());
//        txt_time.setText(duration);
//        seekBar.setMax(playerManager.getDuration());
//        playerManager.play();
//        if (Role.equalsIgnoreCase("member")) {
//            createADS();
//        }
    }

    private void addControls() {
        btn_home = findViewById(R.id.btn_home);
        btn_play = findViewById(R.id.btn_play);
        btn_back = findViewById(R.id.btn_back);
        txt_artist_song = findViewById(R.id.txt_artist_song);
        txt_name_song = findViewById(R.id.txt_name_song);
        seekBar = findViewById(R.id.seekBar);
        imageView_songs = findViewById(R.id.imageView_songs);
        txt_time = findViewById(R.id.txt_time);
        txt_time_first = findViewById(R.id.txt_time_first);
        btn_pre = findViewById(R.id.btn_pre);
        btn_next = findViewById(R.id.btn_next);
        btn_volume = findViewById(R.id.btn_volume);
        seekbar1 = findViewById(R.id.seekBar_volume);
        btn_toggle = findViewById(R.id.btn_toggle);
        btn_shuffle = findViewById(R.id.btn_shuffle);
        btn_heart = findViewById(R.id.btn_heart);
        txt_lyric = findViewById(R.id.txt_lyric);
        txt_view_playmusic = findViewById(R.id.txt_view_playmusic);
        btn_download = findViewById(R.id.btn_download);
        // Load animation từ file xml
        animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        // Áp dụng animation vào ImageView
        imageView_songs.startAnimation(animation);
    }

    private void playNewSong(Integer songId) {
        if (playerManager.isPlaying()) {
            playerManager.stop();
        }
        lyricsManager.resetLyrics();
        playerManager.setDataSource(loadDataSong(songId));
        loadNameArtist(songId);
        updateViewSong(songId);
        updateHeartButtonUI();
        updateLyrics(songId);
        updateUIElements();
        updateSongHistory(userID, IDSong);
        playerManager.play();
        imageView_songs.startAnimation(animation);

        if (Role.equalsIgnoreCase("member")) {
            createADS();
        }
    }

    private void updateLyrics(Integer songId) {
        txt_lyric.setText("");
        loadLRC(songId);
        if (Role.equalsIgnoreCase("premium")) {
            lyricsManager.loadLyrics(linkLRC);
        }
    }

    private void updateUIElements() {
        sendNotification();
        String duration = timeSeekbar(playerManager.getDuration());
        txt_time.setText(duration);
        seekBar.setMax(playerManager.getDuration());
        btn_play.setImageResource(R.drawable.ic_pause);
    }

    private void handleNextSong() {
        if (frag) {
            if (Isshuffle) {
                playBack = new ShufflePlayback();
            } else {
                playBack = new NormalPlayback();
            }
        } else {
            playBack = new RepeatPlayback();
        }

        // Lấy chỉ số bài hát tiếp theo
        int nextIndex = playBack.getNextPosition(currentPosition, arr);

        // Lấy ID bài hát từ danh sách
        Integer songId = arr.get(nextIndex);

        // Cập nhật vị trí hiện tại
        currentPosition = nextIndex;

        // Phát bài hát mới
        playNewSong(songId);
    }

    private void handlePreviousSong() {
        if (frag) {
            if (Isshuffle) {
                playBack = new ShufflePlayback();
            } else {
                playBack = new NormalPlayback();
            }
        } else {
            playBack = new RepeatPlayback();
        }

        // Lấy chỉ số bài hát tiếp theo
        int preIndex = playBack.getPrePosition(currentPosition, arr);

        // Lấy ID bài hát từ danh sách
        Integer songId = arr.get(preIndex);

        // Cập nhật vị trí hiện tại
        currentPosition = preIndex;

        // Phát bài hát mới
        playNewSong(songId);
//        if(frag) {
//            if (Isshuffle) {
//                //currentPosition = Positionshuffle;
//                playBack = new ShufflePlayback();
//                songId = playBack.getPrePosition(currentPosition,arr);
//            } else{
//                playBack = new NormalPlayback();
//                songId = playBack.getPrePosition(currentPosition,arr);
//            }
//            else if (currentPosition > 0) {
//                currentPosition--;
//            } else {
//                currentPosition = arr.size() - 1;
//            }
//            Integer songId = arr.get(currentPosition);
//            playNewSong(songId);
//        }else {
//            //Integer songId = arr.get(currentPosition);
//            playBack = new RepeatPlayback();
//            songId = playBack.getPrePosition(currentPosition,arr);
//            playNewSong(songId);
//        }
    }

    private void setupVolumeControl() {
        btn_volume.setOnClickListener(v -> {
            int visibility = seekbar1.getVisibility();
            seekbar1.setVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE);
        });
    }

    private void setupPlaybackControls() {
        // Xử lý nút play/pause
        btn_play.setOnClickListener(v -> {
            if (playerManager.getMediaPlayer().isPlaying()) {
                playerManager.pause();
                btn_play.setImageResource(R.drawable.ic_play);
                imageView_songs.clearAnimation();
            } else {
                playerManager.play();
                btn_play.setImageResource(R.drawable.ic_pause);
                imageView_songs.startAnimation(animation);
            }
        });

        // Xử lý nút lặp lại
        btn_toggle.setOnClickListener(v -> {
            frag = !frag;
            btn_toggle.setImageResource(frag ? R.drawable.ic_repeat : R.drawable.ic_repeatactive);
            new MediaPlayerManager.Builder().setLooping(!frag).apply();
        });

        // Xử lý nút phát ngẫu nhiên
        btn_shuffle.setOnClickListener(v -> {
            Isshuffle = !Isshuffle;
            btn_shuffle.setImageResource(Isshuffle ? R.drawable.ic_shufferactive : R.drawable.ic_shuffer);
        });
    }

    private void setupNavigationControls() {
        // Xử lý các nút điều hướng
        btn_next.setOnClickListener(v -> handleNextSong());
        btn_pre.setOnClickListener(v -> handlePreviousSong());

        btn_back.setOnClickListener(v -> {
            if (playerManager.getMediaPlayer() != null && playerManager.isPlaying()) {
                try {
                    playerManager.stop();
                } catch (IllegalStateException e) {
                    Log.e("MediaPlayer", "Lỗi khi dừng phát", e);
                }
            }
            finish();
        });

        btn_home.setOnClickListener(v -> {
            if (playerManager.getMediaPlayer().isPlaying()) {
                playerManager.stop();
            }
            startActivity(new Intent(PlayMusicActivity.this, MainActivity.class));
            finish();
        });
    }

    private void setupProgressBar() {
        seekBar.setMax(playerManager.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    playerManager.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        startProgressUpdateThread();
    }

    private void startProgressUpdateThread() {
        new Thread(() -> {
            while (playerManager != null) {
                if (playerManager.isPlaying()) {
                    try {
                        final double current = playerManager.getCurrentPosition();
                        final String time = timeSeekbar((int) current);

                        runOnUiThread(() -> {
                            txt_time_first.setText(time);
                            seekBar.setProgress((int) current);
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }).start();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (lyricsManager != null) {
            lyricsManager.release();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (playerManager.isPlaying()) {
            playerManager.pause();
        }
        if (lyricsManager != null) {
            lyricsManager.resetLyrics(); // Hoặc release() nếu muốn giải phóng hoàn toàn
        }
    }
}