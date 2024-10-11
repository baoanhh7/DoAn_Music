package com.example.doan_music.music;

import static com.example.doan_music.music.MyNoti.CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import com.example.doan_music.LoadImage.LoadImageTask;
import com.example.doan_music.R;
import com.example.doan_music.activity.MainActivity;
import com.example.doan_music.data.DbHelper;
import com.example.doan_music.database.ConnectionClass;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

public class PlayMusicActivity extends AppCompatActivity {

    ImageButton btn_home, btn_play, btn_back, btn_next, btn_pre, btn_toggle, btn_shuffle, btn_volume, btn_heart;
    SeekBar seekBar, seekbar1;
    TextView txt_time, txt_time_first, txt_view_playmusic;
    MediaPlayer myMusic;
    AudioManager audioManager;
    ArrayList<Integer> arr;
    ArrayList<Integer> arr1 = new ArrayList<>();
    ArrayList<Integer> shuffle = new ArrayList<>();
    ImageView imageView_songs;
    TextView txt_artist_song, txt_name_song;
    Integer currentPosition = -1;
    Integer Positionshuffle = -1;
    boolean Isshuffle = false;
    SQLiteDatabase database = null;
    DbHelper dbHelper;
    Animation animation;
    private boolean frag = true;
    private boolean frag_heart = false;
    // Tạo một biến Bitmap để lưu hình ảnh
    Bitmap bitmapResult;
    Drawable drawable;
    Bitmap bitmap;
    Integer IDSong;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    int userID;

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
        SeekBar sbTime;
        myMusic = new MediaPlayer();
        //myMusic = MediaPlayer.create(this, R.raw.nhung_loi_hua_bo_quen);
        loadData();
        updateHistorySong(IDSong);
        updateViewSong(IDSong);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myMusic.seekTo(0);

        myMusic.start();
        myMusic.setLooping(false);
        seekbar1.setVisibility(View.GONE);
        // tạo biến duration để lưu thời gian bài hát
        String duration = timeSeekbar(myMusic.getDuration());
        txt_time.setText(duration);
        loadNameArtist(IDSong);
        sendNotification();

        addEvents();

        updateHeartButtonUI();

        volume();
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
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_headphone)
                .setSubText("Music")
                .setContentTitle(txt_artist_song.getText().toString())
                .setContentText(txt_name_song.getText().toString())
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
                query = "select * from Artist" +
                        " JOIN Song ON Artist.ArtistID =Song.ArtistID " +
                        " WHERE Song.SongID =  " + id;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                while (resultSet.next()) {
                    String ten = resultSet.getString(2);
                    txt_artist_song.setText(ten);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void loadData() {
        loadDataSong(IDSong);
    }

    private void loadDataSong(int id) {
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
                    // Tải ảnh từ URL và hiển thị
                    new LoadImageTask(imageView_songs).execute(img);
                    txt_name_song.setText(ten);
                    txt_view_playmusic.setText(view + "");
                    try {
                        myMusic.setDataSource(linkSong);
                        myMusic.prepare();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
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
        btn_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = seekbar1.getVisibility();
                if (visibility == View.GONE) {
                    seekbar1.setVisibility(View.VISIBLE);
                } else
                    seekbar1.setVisibility(View.GONE);
            }
        });
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMusic.isPlaying()) {
                    myMusic.stop();
                    // myMusic.reset();
                }

                Intent intent = new Intent(PlayMusicActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMusic.isPlaying()) {
                    myMusic.pause();
                    btn_play.setImageResource(R.drawable.ic_play);
                    imageView_songs.clearAnimation();

                } else {
                    myMusic.start();
                    btn_play.setImageResource(R.drawable.ic_pause);

                    // Áp dụng animation vào ImageView
                    imageView_songs.startAnimation(animation);
                }
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMusic != null) {
                    btn_play.setImageResource(R.drawable.ic_pause);
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
                    loadNameArtist(idSong);
                    updateHistorySong(idSong);
                    updateViewSong(idSong);


                    sendNotification();
                    String duration = timeSeekbar(myMusic.getDuration());
                    txt_time.setText(duration);
                    seekBar.setMax(myMusic.getDuration());
                    myMusic.start();
                    updateHeartButtonUI();
                    imageView_songs.startAnimation(animation);
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
                    loadNameArtist(idSong);
                    updateHistorySong(idSong);
                    updateViewSong(idSong);
                    sendNotification();
                    String duration = timeSeekbar(myMusic.getDuration());
                    txt_time.setText(duration);
                    seekBar.setMax(myMusic.getDuration());
                    myMusic.start();

                    updateHeartButtonUI();
                    imageView_songs.startAnimation(animation);
                }
            }
        });
        btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMusic != null) {
                    btn_play.setImageResource(R.drawable.ic_pause);
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
                    loadNameArtist(idSong);
                    updateHistorySong(idSong);
                    updateViewSong(idSong);
                    updateHeartButtonUI();
                    sendNotification();
                    String duration = timeSeekbar(myMusic.getDuration());
                    txt_time.setText(duration);
                    seekBar.setMax(myMusic.getDuration());
                    myMusic.start();

                    imageView_songs.startAnimation(animation);
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
                    loadNameArtist(idSong);
                    updateHistorySong(idSong);
                    updateViewSong(idSong);
                    updateHeartButtonUI();
                    sendNotification();
                    String duration = timeSeekbar(myMusic.getDuration());
                    txt_time.setText(duration);
                    seekBar.setMax(myMusic.getDuration());
                    myMusic.start();

                    imageView_songs.startAnimation(animation);
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
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

        btn_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (frag) {
                    // Thực hiện các hành động khi nút được bật
                    btn_toggle.setImageResource(R.drawable.ic_repeatactive);
                    myMusic.setLooping(true);
                    frag = false;
                } else {
                    btn_toggle.setImageResource(R.drawable.ic_repeat);
                    myMusic.setLooping(false);
                    frag = true;
                }
            }
        });
        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Isshuffle) {
                    btn_shuffle.setImageResource(R.drawable.ic_shuffer);
                    Isshuffle = false;
//                    Collections.shuffle(arr);
                } else {
                    btn_shuffle.setImageResource(R.drawable.ic_shufferactive);
//                    arr = shuffle;
                    Isshuffle = true;
                }
            }
        });

        // set giới hạn Max cho thanh seekBar
        seekBar.setMax(myMusic.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                                    txt_time_first.setText(time);
                                    seekBar.setProgress((int) current);
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
        myMusic.reset();
        if (currentPosition < arr.size() - 1) {
            currentPosition++;
        } else {
            currentPosition = 0;
        }
        Integer idSong = arr.get(currentPosition);
        loadDataSong(idSong);
        loadNameArtist(idSong);
        updateHeartButtonUI();
        updateViewSong(idSong);
        updateHistorySong(idSong);

        String duration = timeSeekbar(myMusic.getDuration());
        txt_time.setText(duration);
        seekBar.setMax(myMusic.getDuration());
        myMusic.start();
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

        txt_view_playmusic = findViewById(R.id.txt_view_playmusic);

        // Load animation từ file xml
        animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        // Áp dụng animation vào ImageView
        imageView_songs.startAnimation(animation);
    }
}