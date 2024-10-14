package com.example.doan_music.fragment.tab_home;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.activity.home.PlayListActivity;
import com.example.doan_music.activity.home.SongsAlbumActivity;
import com.example.doan_music.adapter.home.CategoryAdapter;
import com.example.doan_music.adapter.home.HomeAdapter;
import com.example.doan_music.data.DatabaseManager;
import com.example.doan_music.data.DbHelper;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.m_interface.IClickItemCategory;
import com.example.doan_music.m_interface.OnItemClickListener;
import com.example.doan_music.model.Album;
import com.example.doan_music.model.Category;
import com.example.doan_music.model.Playlists;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class All_Fragment extends Fragment {
    DbHelper dbHelper;
    SQLiteDatabase database = null;
    View view;
    private RecyclerView rcv_all_header, rcv_all_bottom;
    private HomeAdapter allAdapter_header;
    private CategoryAdapter allCateAdapter_bottom;

    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_all_, container, false);

        addControls();

        // set layout của recyclerView thành 2 cột
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        rcv_all_header.setLayoutManager(gridLayoutManager);

        // set layout của recyclerView theo hướng ngang
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        rcv_all_bottom.setLayoutManager(linearLayoutManager);

        // set data cho recyclerView
        allCateAdapter_bottom.setData(getlistuserBottom());

        addEvents();

        return view;
    }

    private void addEvents() {
        allAdapter_header.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                ConnectionClass sql = new ConnectionClass();
                connection = sql.conClass();
                if (connection != null) {
                    try {
                        query = "SELECT * FROM Album";
                        smt = connection.createStatement();
                        resultSet = smt.executeQuery(query);

                        while (resultSet.next()) {
                            int id = resultSet.getInt(1);
                            String name = resultSet.getString(2);
//                            int view = resultSet.getInt(4);
                            if (name.equals(data)) {
//                                view++;
//                                ContentValues values = new ContentValues();
//                                values.put("AlbumView", view);
//                                database.update("Albums", values, "AlbumID=?", new String[]{String.valueOf(id)});

                                Intent intent = new Intent(requireContext(), SongsAlbumActivity.class);
                                intent.putExtra("albumID", id);
                                startActivity(intent);
                                break;
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
        });
    }

    @NonNull
    private List<Album> getlistuserHeader() {
        List<Album> list = new ArrayList<>();

        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "SELECT * FROM Album";
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                while (resultSet.next()) {
                    Integer AlbumID = resultSet.getInt(1);
                    String AlbumName = resultSet.getString(2);
                    String AlbumImage = resultSet.getString(3);
                    // Chuyển đổi linkImage thành byte[]
                    byte[] img = getImageBytesFromURL(AlbumImage);
                    Integer ArtistID = resultSet.getInt(4);
                    Album album = new Album(AlbumID, AlbumName, img, ArtistID);
                    list.add(album);
                }
                allAdapter_header.notifyDataSetChanged();
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }

        return list;
    }

    @NonNull
    private List<Category> getlistuserBottom() {
        List<Playlists> list = new ArrayList<>();

        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "SELECT * FROM Playlist";
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                while (resultSet.next()) {
                    Integer PlaylistID = resultSet.getInt(1);
                    String PlaylistName = resultSet.getString(2);
                    String PlaylistImage = resultSet.getString(3);
                    // Chuyển đổi linkImage thành byte[]
                    byte[] img = getImageBytesFromURL(PlaylistImage);
                    Playlists playlists = new Playlists(PlaylistID, PlaylistName, img);
                    list.add(playlists);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }

        allCateAdapter_bottom.notifyDataSetChanged();

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Danh sách nhiều người nghe", list));
        categoryList.add(new Category("Danh sách được yêu thích", list));
        categoryList.add(new Category("Lựa chọn của Spotify", list));

        return categoryList;
    }

    private void addControls() {
        rcv_all_header = view.findViewById(R.id.rcv_all_header);
        rcv_all_bottom = view.findViewById(R.id.rcv_all_bottom);

        allAdapter_header = new HomeAdapter(requireContext(), getlistuserHeader());

        allCateAdapter_bottom = new CategoryAdapter(new IClickItemCategory() {
            @Override
            public void onClickItemCategory(Category category) {
                Intent i = new Intent(requireContext(), PlayListActivity.class);
                i.putExtra("c", category.getName());
                startActivity(i);
            }
        });

        rcv_all_header.setAdapter(allAdapter_header);
        rcv_all_bottom.setAdapter(allCateAdapter_bottom);
    }

    private byte[] getImageBytesFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (IOException e) {
            Log.e("Error: ", "Failed to load image from URL: " + e.getMessage());
            return null; // Hoặc có thể trả về mảng byte rỗng nếu muốn
        }
    }

    private byte[] convertDrawableToByteArray(Context context, int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}