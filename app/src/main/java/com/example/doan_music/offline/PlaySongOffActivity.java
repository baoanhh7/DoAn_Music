package com.example.doan_music.offline;

import static com.example.doan_music.music.MyNoti.CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.doan_music.R;
import com.example.doan_music.data.DbHelper;
import com.example.doan_music.database.ConnectionClass;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PlaySongOffActivity extends AppCompatActivity {

    ImageButton btn_play_songoffline, btn_back_songoffline, btn_next_songoffline, btn_pre_songoffline, btn_toggle_songoffline, btn_shuffle_songoffline, btn_volume_songoffline;
    SeekBar seekBar_songoffline, seekBar_volume_songoffline;
    TextView txt_time_songoffline, txt_time_first_songoffline, txt_view_playmusic_songoffline, txt_lyric_songoffline;
    MediaPlayer myMusic;
    AudioManager audioManager;
    ArrayList<Integer> arr;
    ImageView imageView_songoffline;
    TextView txt_artist_songoffline, txt_name_songoffline;
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
    private lyricManagerOffline lyricsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song_off);
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
        SeekBar sbTime;
        myMusic = new MediaPlayer();
        //myMusic = MediaPlayer.create(this, R.raw.nhung_loi_hua_bo_quen);
        loadData();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myMusic.seekTo(0);
        myMusic.start();
        myMusic.setLooping(false);
        seekBar_volume_songoffline.setVisibility(View.GONE);
        // tạo biến duration để lưu thời gian bài hát
        String duration = timeSeekbar(myMusic.getDuration());
        txt_time_songoffline.setText(duration);
        //loadNameArtist(IDSong);
        sendNotification();
//        loadLRC(IDSong);
        txt_lyric_songoffline.setText("");
        volume();
        // Đọc file LRC và lưu trữ lời bài hát
        lyricsManager = new lyricManagerOffline(myMusic, txt_lyric_songoffline);
        lyricsManager.loadLyricsFromFile(linkLRC);
//        readLRCFile();

//        refreshRunnable = new Runnable() {
//            @Override
//            public void run() {
//
//                updateLyrics();
//                handler.postDelayed(this, REFRESH_INTERVAL);
//            }
//        };
        // Bắt đầu cập nhật lời bài hát
        if (frag) {
            myMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNextSong(arr);
                    sendNotification();
                }
            });
        }

        addEvents();
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


    private Integer getRandom(int i) {
        Random random = new Random();

        return random.nextInt(i + 1);
    }

    private void sendNotification() {
        // Tiếp tục xử lý với bitmap

        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "tag");
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_headphone)
                .setSubText("Music")
                .setContentTitle(txt_artist_songoffline.getText().toString())
                .setContentText(txt_name_songoffline.getText().toString())
                .setLargeIcon(bitmap)
                // Add media control buttons that invoke intents in your media service
                .addAction(R.drawable.ic_pre, "Previous", null) // #0
                .addAction(R.drawable.ic_pause, "Pause", null)  // #1
                .addAction(R.drawable.ic_next, "Next", null)     // #2
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2 /* #1: pause button */))
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.notify(1, notification);
    }

    private void volume() {
        int maxV = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curV = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar_volume_songoffline.setMax(maxV);
        seekBar_volume_songoffline.setProgress(curV);
        seekBar_volume_songoffline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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


    private void loadData() {
        loadDataSong(IDSong);
    }

    private void loadDataSong(int id) {
        database = openOrCreateDatabase("music_db", MODE_PRIVATE, null);
        String query = "SELECT * FROM downloaded_songs WHERE song_id = ?";
        // Thực hiện truy vấn
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            do {

                byte[] songImage = cursor.getBlob(3);
                String songName = cursor.getString(4);
                String artistName = cursor.getString(5);
                String local_path_song = cursor.getString(6);
                linkLRC = cursor.getString(8);
                // Chuyển đổi byte[] thành Bitmap
                Bitmap songImageBitmap = BitmapFactory.decodeByteArray(songImage, 0, songImage.length);

                // Thiết lập Bitmap vào ImageView
                imageView_songoffline.setImageBitmap(songImageBitmap);
                txt_artist_songoffline.setText(artistName);
                txt_name_songoffline.setText(songName);
                // Khởi tạo MediaPlayer
                if (myMusic == null) {
                    myMusic = new MediaPlayer();
                } else {
                    myMusic.reset(); // Đặt lại để chuẩn bị cho bài hát mới
                }
                try {
                    myMusic.setDataSource(local_path_song);
                    myMusic.prepare();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
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
        btn_volume_songoffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = seekBar_volume_songoffline.getVisibility();
                if (visibility == View.GONE) {
                    seekBar_volume_songoffline.setVisibility(View.VISIBLE);
                } else
                    seekBar_volume_songoffline.setVisibility(View.GONE);
            }
        });
        btn_play_songoffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMusic.isPlaying()) {
                    myMusic.pause();
                    btn_play_songoffline.setImageResource(R.drawable.ic_play);
                    imageView_songoffline.clearAnimation();

                } else {
                    myMusic.start();
                    btn_play_songoffline.setImageResource(R.drawable.ic_pause);

                    // Áp dụng animation vào ImageView
                    imageView_songoffline.startAnimation(animation);
                }
            }
        });
        btn_next_songoffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMusic != null) {
                    btn_play_songoffline.setImageResource(R.drawable.ic_pause);
                }
                if (frag) {
                    if (Isshuffle) {
                        Positionshuffle = currentPosition;
                        currentPosition = getRandom(arr.size() - 1);
                    } else if (currentPosition < arr.size() - 1) {
                        currentPosition++;
                    } else {
                        currentPosition = 0;
                    }


                    if (myMusic.isPlaying()) {
                        myMusic.stop();
                        myMusic.reset();
                    }
                    if (!myMusic.isPlaying()) {
                        myMusic.stop();
                        myMusic.reset();
                    }
                    Integer idSong = arr.get(currentPosition);
                    loadDataSong(idSong);
                    ///loadNameArtist(idSong);
                    txt_lyric_songoffline.setText("");
                    sendNotification();
                    String duration = timeSeekbar(myMusic.getDuration());
                    txt_time_songoffline.setText(duration);
                    seekBar_songoffline.setMax(myMusic.getDuration());
                    myMusic.start();
                    //loadLRC(idSong);
                    //readLRCFile();
                    lyricsManager.loadLyricsFromFile(linkLRC);
                    imageView_songoffline.startAnimation(animation);
                } else {
                    if (myMusic.isPlaying()) {
                        myMusic.stop();
                        myMusic.reset();
                    }
                    if (!myMusic.isPlaying()) {
                        myMusic.stop();
                        myMusic.reset();
                    }
                    Integer idSong = arr.get(currentPosition);
                    loadDataSong(idSong);
                    //loadNameArtist(idSong);
                    txt_lyric_songoffline.setText("");
                    //loadLRC(idSong);
                    sendNotification();
                    //readLRCFile();
                    lyricsManager.loadLyricsFromFile(linkLRC);
                    String duration = timeSeekbar(myMusic.getDuration());
                    txt_time_songoffline.setText(duration);
                    seekBar_songoffline.setMax(myMusic.getDuration());
                    myMusic.start();

                    imageView_songoffline.startAnimation(animation);
                }
            }
        });
        btn_pre_songoffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMusic != null) {
                    btn_play_songoffline.setImageResource(R.drawable.ic_pause);
                }
                if (frag) {
                    if (Isshuffle) {
                        currentPosition = Positionshuffle;
                    } else if (currentPosition > 0) {
                        currentPosition--;
                    } else {
                        currentPosition = arr.size() - 1;
                    }

                    if (myMusic.isPlaying()) {
                        myMusic.stop();
                        myMusic.reset();
                    }
                    if (!myMusic.isPlaying()) {
                        myMusic.stop();
                        myMusic.reset();
                    }
                    Integer idSong = arr.get(currentPosition);
                    loadDataSong(idSong);
                    //loadNameArtist(idSong);
                    txt_lyric_songoffline.setText("");
                    //loadLRC(idSong);
                    //readLRCFile();
                    lyricsManager.loadLyricsFromFile(linkLRC);
                    sendNotification();
                    String duration = timeSeekbar(myMusic.getDuration());
                    txt_time_songoffline.setText(duration);
                    seekBar_songoffline.setMax(myMusic.getDuration());
                    myMusic.start();
                    imageView_songoffline.startAnimation(animation);
                } else {
                    if (myMusic.isPlaying()) {
                        myMusic.stop();
                        myMusic.reset();
                    }

                    if (!myMusic.isPlaying()) {
                        myMusic.stop();
                        myMusic.reset();
                    }
                    Integer idSong = arr.get(currentPosition);
                    loadDataSong(idSong);
                    //loadNameArtist(idSong);
                    txt_lyric_songoffline.setText("");
                    //loadLRC(idSong);
                    //readLRCFile();
                    lyricsManager.loadLyricsFromFile(linkLRC);
                    sendNotification();
                    String duration = timeSeekbar(myMusic.getDuration());
                    txt_time_songoffline.setText(duration);
                    seekBar_songoffline.setMax(myMusic.getDuration());
                    myMusic.start();
                    imageView_songoffline.startAnimation(animation);
                }
            }
        });

        btn_back_songoffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMusic != null) {  // Kiểm tra myMusic có null không
                    if (myMusic.isPlaying()) {
                        try {
                            myMusic.stop();
                            //myMus
                            //myMusic.reset();
                        } catch (IllegalStateException e) {
                            Log.e("MediaPlayer", "Error stopping or resetting", e);
                        }
                    }
                } else {
                    Log.e("MediaPlayer", "myMusic is null");
                }
                finish();
            }
        });


        btn_toggle_songoffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (frag) {
                    // Thực hiện các hành động khi nút được bật
                    btn_toggle_songoffline.setImageResource(R.drawable.ic_repeatactive);
                    myMusic.setLooping(true);
                    frag = false;
                } else {
                    btn_toggle_songoffline.setImageResource(R.drawable.ic_repeat);
                    myMusic.setLooping(false);
                    frag = true;
                }
            }
        });
        btn_shuffle_songoffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Isshuffle) {
                    btn_shuffle_songoffline.setImageResource(R.drawable.ic_shuffer);
                    Isshuffle = false;
//                    Collections.shuffle(arr);
                } else {
                    btn_shuffle_songoffline.setImageResource(R.drawable.ic_shufferactive);
//                    arr = shuffle;
                    Isshuffle = true;
                }
            }
        });

        // set giới hạn Max cho thanh seekBar
        seekBar_songoffline.setMax(myMusic.getDuration());
        seekBar_songoffline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    myMusic.seekTo(progress);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (myMusic != null) {
                    if (myMusic.isPlaying()) {
                        try {
                            final double current = myMusic.getCurrentPosition();
                            final String time = timeSeekbar((int) current);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txt_time_first_songoffline.setText(time);
                                    seekBar_songoffline.setProgress((int) current);
                                }
                            });
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }).start();
    }


    private void playNextSong(@NonNull ArrayList<Integer> arr) {
        myMusic.reset();
        if (currentPosition < arr.size() - 1) {
            currentPosition++;
        } else {
            currentPosition = 0;
        }
        Integer idSong = arr.get(currentPosition);
        loadDataSong(idSong);
        //loadNameArtist(idSong);
        txt_lyric_songoffline.setText("");
        //loadLRC(idSong);
        //readLRCFile();
        lyricsManager.loadLyricsFromFile(linkLRC);
        String duration = timeSeekbar(myMusic.getDuration());
        txt_time_songoffline.setText(duration);
        seekBar_songoffline.setMax(myMusic.getDuration());
        myMusic.start();
    }

    private void addControls() {
        btn_play_songoffline = findViewById(R.id.btn_play_songoffline);
        btn_back_songoffline = findViewById(R.id.btn_back_songoffline);
        txt_artist_songoffline = findViewById(R.id.txt_artist_songoffline);
        txt_name_songoffline = findViewById(R.id.txt_name_songoffline);
        seekBar_songoffline = findViewById(R.id.seekBar_songoffline);
        imageView_songoffline = findViewById(R.id.imageView_songoffline);
        txt_time_songoffline = findViewById(R.id.txt_time_songoffline);
        txt_time_first_songoffline = findViewById(R.id.txt_time_first_songoffline);
        btn_pre_songoffline = findViewById(R.id.btn_pre_songoffline);
        btn_next_songoffline = findViewById(R.id.btn_next_songoffline);
        btn_volume_songoffline = findViewById(R.id.btn_volume_songoffline);
        seekBar_volume_songoffline = findViewById(R.id.seekBar_volume_songoffline);
        btn_toggle_songoffline = findViewById(R.id.btn_toggle_songoffline);
        btn_shuffle_songoffline = findViewById(R.id.btn_shuffle_songoffline);
        txt_lyric_songoffline = findViewById(R.id.txt_lyric_songoffline);
        // Load animation từ file xml
        animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        // Áp dụng animation vào ImageView
        imageView_songoffline.startAnimation(animation);
    }
}