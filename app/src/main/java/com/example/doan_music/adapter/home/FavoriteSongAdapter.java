package com.example.doan_music.adapter.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.m_interface.OnItemClickListener;
import com.example.doan_music.model.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FavoriteSongAdapter extends RecyclerView.Adapter<FavoriteSongAdapter.SongViewHolder> {

    private List<Song> songList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public FavoriteSongAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fav_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userID", -1);  // Lấy userID từ SharedPreferences

        Song song = songList.get(position);

        holder.txt_nameFav.setText(song.getSongName());
        Bitmap bitmap = BitmapFactory.decodeByteArray(song.getSongImage(), 0, song.getSongImage().length);
        holder.img_songFav.setImageBitmap(bitmap);

        // Nhấn vào tên bài hát sẽ chuyển qua phát bài hát
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(songList.get(position).getSongName());
                }
            }
        });

        // Kết nối tới SQL Server
        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();

        if (connection != null) {
            try {
                // Truy vấn SQL kiểm tra xem bài hát có trong danh sách yêu thích hay không
                String queryCheck = "SELECT * FROM User_SongLove WHERE UserID = ? AND SongID = ?";
                PreparedStatement preparedStatementCheck = connection.prepareStatement(queryCheck);
                preparedStatementCheck.setInt(1, userID);
                preparedStatementCheck.setInt(2, song.getSongID());

                ResultSet resultSet = preparedStatementCheck.executeQuery();

                if (resultSet.next()) {
                    // Nếu bài hát đã yêu thích, đặt nút tim màu đỏ
                    holder.btn_heartFav.setImageResource(R.drawable.ic_red_heart);
                } else {
                    // Nếu bài hát chưa yêu thích, đặt nút tim màu xám
                    holder.btn_heartFav.setImageResource(R.drawable.ic_heart);
                }

                resultSet.close();
                preparedStatementCheck.close();

            } catch (SQLException e) {
                Log.e("Error", e.getMessage());
            }
        } else {
            Log.e("Error", "Connection is null");
        }

        // Xử lý khi người dùng nhấn vào nút thả tim
        holder.btn_heartFav.setOnClickListener(v -> {
            if (connection != null) {
                try {
                    // Truy vấn SQL kiểm tra bài hát có trong danh sách yêu thích hay chưa
                    String queryCheck = "SELECT * FROM User_SongLove WHERE UserID = ? AND SongID = ?";
                    PreparedStatement preparedStatementCheck = connection.prepareStatement(queryCheck);
                    preparedStatementCheck.setInt(1, userID);
                    preparedStatementCheck.setInt(2, song.getSongID());

                    ResultSet resultSet = preparedStatementCheck.executeQuery();

                    if (resultSet.next()) {
                        // Nếu bài hát đã có trong danh sách yêu thích, xóa bài hát khỏi danh sách
                        String queryDelete = "DELETE FROM User_SongLove WHERE UserID = ? AND SongID = ?";
                        PreparedStatement preparedStatementDelete = connection.prepareStatement(queryDelete);
                        preparedStatementDelete.setInt(1, userID);
                        preparedStatementDelete.setInt(2, song.getSongID());

                        int rowsDeleted = preparedStatementDelete.executeUpdate();
                        if (rowsDeleted > 0) {
                            holder.btn_heartFav.setImageResource(R.drawable.ic_heart);  // Đặt màu nút thả tim thành xám
                            Log.i("Info", "Song removed from love list successfully!");
                        } else {
                            Log.e("Error", "Failed to remove song from love list.");
                        }
                        preparedStatementDelete.close();

                    } else {
                        // Nếu bài hát chưa có trong danh sách yêu thích, thêm bài hát vào danh sách
                        String queryInsert = "INSERT INTO User_SongLove (UserID, SongID) VALUES (?, ?)";
                        PreparedStatement preparedStatementInsert = connection.prepareStatement(queryInsert);
                        preparedStatementInsert.setInt(1, userID);
                        preparedStatementInsert.setInt(2, song.getSongID());

                        int rowsInserted = preparedStatementInsert.executeUpdate();
                        if (rowsInserted > 0) {
                            holder.btn_heartFav.setImageResource(R.drawable.ic_red_heart);  // Đặt màu nút thả tim thành đỏ
                            Log.i("Info", "Song added to love list successfully!");
                        } else {
                            Log.e("Error", "Failed to add song to love list.");
                        }
                        preparedStatementInsert.close();
                    }
                    resultSet.close();
                    preparedStatementCheck.close();

                    connection.close();  // Đóng kết nối

                } catch (SQLException e) {
                    Log.e("Error", e.getMessage());
                }
            } else {
                Log.e("Error", "Connection is null");
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        if (songList != null) return songList.size();
        return 0;
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView txt_nameFav, txt_artistFav;
        ImageView img_songFav;
        ImageButton btn_heartFav;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nameFav = itemView.findViewById(R.id.txt_nameFav);
            txt_artistFav = itemView.findViewById(R.id.txt_artistFav);
            img_songFav = itemView.findViewById(R.id.img_songFav);
            btn_heartFav = itemView.findViewById(R.id.btn_heartFav);
        }
    }
}

