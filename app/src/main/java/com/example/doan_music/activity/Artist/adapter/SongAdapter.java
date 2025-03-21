package com.example.doan_music.activity.Artist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.designPattern.CommandPK.Class.CommandManager;
import com.example.doan_music.model.Song;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songs;
    private Context context;
    static Button undoButton;

    public SongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        if (holder.txtNameSong != null) {
            holder.txtNameSong.setText(song.getSongName());
        }
        if (holder.imgSong != null && context != null) {
            Picasso.get()
                    .load(Arrays.toString(song.getSongImage()))
                    .placeholder(R.drawable.nhungloihuaboquen)
                    .into(holder.imgSong);
        }

    }

    @Override
    public int getItemCount() {
        return songs != null ? songs.size() : 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong;
        TextView txtNameSong;
        Button undoButton;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.img_song);
            txtNameSong = itemView.findViewById(R.id.txt_nameSong);
            undoButton = itemView.findViewById(R.id.btn_heart);

            undoButton.setOnClickListener(v -> {
                CommandManager.getInstance().undoLastCommand();
                Toast.makeText(itemView.getContext(), "Hoàn tác cập nhật album!", Toast.LENGTH_SHORT).show();
            });
        }
    }

}
