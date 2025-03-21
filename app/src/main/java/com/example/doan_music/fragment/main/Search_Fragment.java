package com.example.doan_music.fragment.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.LoadImage.LoadImageFromUrl;
import com.example.doan_music.R;
import com.example.doan_music.activity.library.ArtistSongActivity;
import com.example.doan_music.adapter.search.SearchAdapter;
import com.example.doan_music.adapter.search.SearchItemAdapter;
import com.example.doan_music.adapter.thuvien.ThuVienAdapter;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.m_interface.OnItemClickListener;
import com.example.doan_music.model.ThuVien;
import com.example.doan_music.model.User;
import com.example.doan_music.music.PlayMusicActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Search_Fragment extends Fragment {
    SQLiteDatabase database = null;
    TextView txt_searchlibrary2, txt_searchlibrary1;
    ThuVienAdapter thuVienAdapter;
    ArrayList<ThuVien> arr;
    ArrayList<Integer> arr1 = new ArrayList<>();
    View view;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    private RecyclerView rcv_search_header, rcv_search_bottom, rcv_searchItem;
    private SearchAdapter searchAdapter;
    private RecyclerView rcvSearchItem;
    private SearchItemAdapter searchItemAdapter;
    private SearchView searchView;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_, container, false);

        addControls();

        //set layout của recyclerView theo hướng ngang
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false);
        rcv_search_header.setLayoutManager(linearLayoutManager);

        // set layout của recyclerView thành 2 cột
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        rcv_search_bottom.setLayoutManager(gridLayoutManager);

//        // set data cho recyclerView
//        searchAdapter_header.setData(getlistuserHeader());
//        searchAdapter_bottom.setData(getlistuserBottom());
        addEvents();
        //loadData();
        loadDataArtist();
        loadDataSong();
        return view;
    }

    private void addEvents() {
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcv_searchItem.setVisibility(View.VISIBLE);
                txt_searchlibrary2.setVisibility(View.GONE);
                txt_searchlibrary1.setVisibility(View.GONE);
                rcv_search_header.setVisibility(View.GONE);
                rcv_search_bottom.setVisibility(View.GONE);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
                LinearLayoutManager linearLayout = new LinearLayoutManager(requireContext());
                rcv_searchItem.setLayoutManager(gridLayoutManager);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                txt_searchlibrary2.setVisibility(View.VISIBLE);
                txt_searchlibrary1.setVisibility(View.VISIBLE);
                rcv_search_header.setVisibility(View.VISIBLE);
                rcv_search_bottom.setVisibility(View.VISIBLE);
                rcv_searchItem.setVisibility(View.GONE);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void loadData() {
        database = getActivity().openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select * from Artists", null);
        arr.clear();
        while (cursor.moveToNext()) {
            Integer maArtist = Integer.valueOf(cursor.getString(0) + "");
            String ten = cursor.getString(1);
            byte[] img = cursor.getBlob(2);
            ThuVien thuVien = new ThuVien(img, ten);
            arr.add(thuVien);

        }
        searchAdapter.notifyDataSetChanged();
        cursor.close();
        cursor = database.rawQuery("select * from Songs", null);
        while (cursor.moveToNext()) {
            Integer maArtist = Integer.valueOf(cursor.getString(0) + "");
            String ten = cursor.getString(2);
            byte[] img = cursor.getBlob(3);
            ThuVien thuVien = new ThuVien(img, ten);
            arr.add(thuVien);

        }
        searchAdapter.notifyDataSetChanged();
        cursor.close();
    }

    private void loadDataSong() {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "SELECT * FROM Song  ";
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                arr1.clear();
                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String ten = resultSet.getString(2);
                    String linkimg = resultSet.getString(3);
                    // Tải ảnh từ URL và chuyển thành byte[]
                    byte[] img = new LoadImageFromUrl(linkimg).getImageBytes();
                    ThuVien thuVien = new ThuVien(img, ten);
                    arr.add(thuVien);
                    arr1.add(id);
                }
                searchAdapter.notifyDataSetChanged();
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void loadDataArtist() {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "SELECT * FROM Artist ";
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                while (resultSet.next()) {
                    String ten = resultSet.getString(2);
                    String linkimg = resultSet.getString(3);
                    // Tải ảnh từ URL và chuyển thành byte[]
                    byte[] img = new LoadImageFromUrl(linkimg).getImageBytes();
                    ThuVien thuVien = new ThuVien(img, ten);
                    arr.add(thuVien);
                }
                searchAdapter.notifyDataSetChanged();
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }


//    private List<SearchItem> getListSearchItem() {
//        List<SearchItem> list = new ArrayList<>();
//        list.add(new SearchItem(R.drawable.avt_lowg, "LowG", "Nghe Si"));
//        list.add(new SearchItem(R.drawable.avt_dalab, "DaLab", "Nghe Si"));
//        list.add(new SearchItem(R.drawable.avt_mck, "MCK", "Nghe Si"));
//        list.add(new SearchItem(R.drawable.avt_obito, "Obito", "Nghe Si"));
//        list.add(new SearchItem(R.drawable.avt_gducky, "Gducky", "Nghe Si"));
//
//        return list;
//    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.search_menu, menu);
//
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_search){
//            Toast.makeText(getActivity(),"Search",Toast.LENGTH_SHORT).show();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    private List<User> getlistuserHeader() {
//        List<User> list = new ArrayList<>();
//
//        list.add(new User(R.drawable.nhac, "Nhạc", true));
//        list.add(new User(R.drawable.sukientructiep, "Sự kiện trực tiếp", true));
//        list.add(new User(R.drawable.podcast, "Podcast", true));
//        list.add(new User(R.drawable.danhchoban, "Dành cho bạn", true));
//
//        list.add(new User(R.drawable.nhac, "Nhạc", true));
//        list.add(new User(R.drawable.sukientructiep, "Sự kiện trực tiếp", true));
//        list.add(new User(R.drawable.podcast, "Podcast", true));
//        list.add(new User(R.drawable.danhchoban, "Dành cho bạn", true));
//
//        list.add(new User(R.drawable.nhac, "Nhạc", true));
//        list.add(new User(R.drawable.sukientructiep, "Sự kiện trực tiếp", true));
//        list.add(new User(R.drawable.podcast, "Podcast", true));
//        list.add(new User(R.drawable.danhchoban, "Dành cho bạn", true));
//
//        list.add(new User(R.drawable.nhac, "Nhạc", true));
//        list.add(new User(R.drawable.sukientructiep, "Sự kiện trực tiếp", true));
//        list.add(new User(R.drawable.podcast, "Podcast", true));
//        list.add(new User(R.drawable.danhchoban, "Dành cho bạn", true));
//
//        return list;
//    }

//    private List<User> getlistuserBottom() {
//        List<User> list = new ArrayList<>();
//
//        list.add(new User(R.drawable.nhac, "Nhạc", false));
//        list.add(new User(R.drawable.sukientructiep, "Sự kiện trực tiếp", false));
//        list.add(new User(R.drawable.podcast, "Podcast", false));
//        list.add(new User(R.drawable.danhchoban, "Dành cho bạn", false));
//
//        list.add(new User(R.drawable.nhac, "Nhạc", false));
//        list.add(new User(R.drawable.sukientructiep, "Sự kiện trực tiếp", false));
//        list.add(new User(R.drawable.podcast, "Podcast", false));
//        list.add(new User(R.drawable.danhchoban, "Dành cho bạn", false));
//
//        list.add(new User(R.drawable.nhac, "Nhạc", false));
//        list.add(new User(R.drawable.sukientructiep, "Sự kiện trực tiếp", false));
//        list.add(new User(R.drawable.podcast, "Podcast", false));
//        list.add(new User(R.drawable.danhchoban, "Dành cho bạn", false));
//
//        list.add(new User(R.drawable.nhac, "Nhạc", false));
//        list.add(new User(R.drawable.sukientructiep, "Sự kiện trực tiếp", false));
//        list.add(new User(R.drawable.podcast, "Podcast", false));
//        list.add(new User(R.drawable.danhchoban, "Dành cho bạn", false));
//
//        return list;
//    }

    private void addControls() {
        rcv_search_header = view.findViewById(R.id.rcv_search_header);
        rcv_search_bottom = view.findViewById(R.id.rcv_search_bottom);

        // Đối với fragment thì Context sẽ là requireContext()
        searchView = view.findViewById(R.id.searchView);
        txt_searchlibrary2 = view.findViewById(R.id.txt_searchlibrary2);
        txt_searchlibrary1 = view.findViewById(R.id.txt_searchlibrary1);
        rcv_searchItem = view.findViewById(R.id.rcv_searchItem);
        arr = new ArrayList<>();
        searchAdapter = new SearchAdapter(this, arr);
        searchAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                Intent intent = null;
                loadDataArtist(data);
                loadDataSong(data);
//                database = getActivity().openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
//                Cursor cursor = database.rawQuery("select * from Artists", null);
//                Cursor cursor1 = database.rawQuery("select * from Songs", null);
//                while (cursor.moveToNext()) {
//                    Integer Id = cursor.getInt(0);
//                    String ten = cursor.getString(1);
//
//                    if (data.equals(ten)) {
//                        intent = new Intent(requireContext(), ArtistSongActivity.class);
//                        intent.putExtra("MaArtist", Id);
//
//                        break;
//                    }
//                }
//                cursor.close();
//                while (cursor1.moveToNext()) {
//                    Integer Id = cursor1.getInt(0);
//                    String ten = cursor1.getString(2);
//                    arr1.add(Id);
//                    if (data.equals(ten)) {
//                        intent = new Intent(requireContext(), PlayMusicActivity.class);
//                        intent.putExtra("SongID", Id);
//                    }
//                }
//                intent.putExtra("arrIDSongs", arr1);
//                cursor1.close();
//                startActivity(intent);
//
            }
        });
        rcv_searchItem.setAdapter(searchAdapter);

    }

    private void loadDataSong(String data) {
        Intent intent;
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn SQL Server để lấy dữ liệu
                query = "SELECT * FROM Song WHERE SongName = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, data);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Integer Id = resultSet.getInt(1);
                    intent = new Intent(requireContext(), PlayMusicActivity.class);
                    intent.putExtra("SongID", Id);
                    intent.putExtra("arrIDSongs", arr1);
                    startActivity(intent);
                    break;
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void loadDataArtist(String data) {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "select * from Artist ";
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                while (resultSet.next()) {
                    Integer Id = resultSet.getInt(1);
                    String ten = resultSet.getString(2);
                    if (data.equals(ten)) {
                        Intent intent = new Intent(requireContext(), ArtistSongActivity.class);
                        intent.putExtra("MaArtist", Id);
                        startActivity(intent);
                        break;
                    }
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }
    }
}