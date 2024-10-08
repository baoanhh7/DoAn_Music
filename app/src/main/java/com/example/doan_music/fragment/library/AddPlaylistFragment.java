package com.example.doan_music.fragment.library;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.activity.MainActivity;
import com.example.doan_music.adapter.thuvien.AddNgheSiAdapter;
import com.example.doan_music.data.DatabaseManager;
import com.example.doan_music.data.DbHelper;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.fragment.main.Library_Fragment;
import com.example.doan_music.m_interface.OnItemClickListener;
import com.example.doan_music.model.AddNgheSi_ThuVien;

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
import java.util.ArrayList;


public class AddPlaylistFragment extends Fragment {

    SearchView search_thuvien_addPlaylist;
    Button btn_xong_addplaylist_thuvien, btn_xong_addplaylist_thuvien1;
    LinearLayout linear_addplayist_tv, linear_addplayist_tv1;
    RecyclerView recycler_Playlist_thuvien_add;
    AddNgheSiAdapter addNgheSiAdapter;
    EditText edt_tenPlaylist;
    ArrayList<AddNgheSi_ThuVien> arrayList;
    View view;
    SQLiteDatabase database = null;
    DbHelper dbHelper;
    SharedPreferences sharedPreferences;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_playlist, container, false);
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            userId = mainActivity.getMyVariable();
        }
        addControl();
        addEvents();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataFromSQLServer();
    }

    private void addEvents() {
        btn_xong_addplaylist_thuvien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Fragment và đặt Bundle vào Fragment
                String name = edt_tenPlaylist.getText().toString().trim();
                insertPlaylist(name,userId);
//                if (getActivity() instanceof MainActivity) {
//                    MainActivity mainActivity = (MainActivity) getActivity();
//                    Integer maU = mainActivity.getMyVariable();
//                    String name = edt_tenPlaylist.getText().toString().trim();
//                    ContentValues values = new ContentValues();
//                    values.put("UserID", maU);
//                    values.put("Name", name);
//                    dbHelper = DatabaseManager.dbHelper(requireContext());
//                    long kq = dbHelper.getReadableDatabase().insert("Playlist_User", null, values);
//                    // Lưu giá trị vào SharedPreferences
//                    database = getActivity().openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
//                    Cursor cursor = database.rawQuery("select * from Playlist_User", null);
//                    while (cursor.moveToNext()) {
//                        Integer id = cursor.getInt(0);
//                        String name1 = cursor.getString(1);
//                        if (name1.equals(name)) {
//                            sharedPreferences = getActivity().getSharedPreferences("MyID", MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putInt("ID", id);
//                            editor.apply();
//                            break;
//                        }
//                    }
//                }
                linear_addplayist_tv.setVisibility(View.GONE);
                linear_addplayist_tv1.setVisibility(View.VISIBLE);
            }
        });
        btn_xong_addplaylist_thuvien1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Fragment và đặt Bundle vào Fragment
                Library_Fragment fragment = new Library_Fragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
            }
        });
        search_thuvien_addPlaylist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                addNgheSiAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                addNgheSiAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
    private void insertPlaylist(String name, Integer maU) {
        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();

        if (connection != null) {
            try {
                // Chèn dữ liệu vào bảng Playlist_User
                String insertQuery = "INSERT INTO Playlist_User (UserID, Name) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setInt(1, maU); // Gán giá trị UserID
                preparedStatement.setString(2, name); // Gán giá trị Name
                long kq = preparedStatement.executeUpdate(); // Thực hiện truy vấn chèn dữ liệu

                if (kq > 0) {
                    // Truy vấn để lấy ID vừa chèn
                    String selectQuery = "SELECT ID FROM Playlist_User WHERE UserID = ? AND Name = ?";
                    PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                    selectStatement.setInt(1, maU);
                    selectStatement.setString(2, name);
                    ResultSet resultSet = selectStatement.executeQuery();

                    if (resultSet.next()) {
                        Integer id = resultSet.getInt("ID"); // Thay đổi "ID" thành tên cột thực tế
                        // Lưu giá trị vào SharedPreferences
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyID", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("ID", id);
                        editor.apply();
                    }

                    resultSet.close();
                    selectStatement.close();
                }
                preparedStatement.close();
            } catch (SQLException e) {
                Log.e("Error: ", e.getMessage());
            } finally {
                try {
                    connection.close(); // Đóng kết nối sau khi hoàn tất
                } catch (SQLException e) {
                    Log.e("Error: ", e.getMessage());
                }
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }


    private void loadData() {
            database = getActivity().openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT * " +
                    "FROM Songs ", null);

            arrayList.clear();
            while (cursor.moveToNext()) {
                String ten = cursor.getString(2);
                byte[] img = cursor.getBlob(3);
                AddNgheSi_ThuVien addNgheSiThuVien = new AddNgheSi_ThuVien(img, ten);
                arrayList.add(addNgheSiThuVien);
            }
            addNgheSiAdapter.notifyDataSetChanged();
            cursor.close();
    }
    private void loadDataFromSQLServer() {
        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();

        if (connection != null) {
            try {
                // Truy vấn để lấy dữ liệu từ bảng Songs
                String query = "SELECT * FROM Song";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                arrayList.clear(); // Xóa danh sách trước khi thêm dữ liệu mới
                while (resultSet.next()) {
                    String ten = resultSet.getString("SongName"); // Thay đổi "SongName" thành tên cột thực tế
                    String linkimage = resultSet.getString("SongImage");
                    byte[] img = getImageBytesFromURL(linkimage);
                    AddNgheSi_ThuVien addNgheSiThuVien = new AddNgheSi_ThuVien(img, ten);
                    arrayList.add(addNgheSiThuVien);
                }

                addNgheSiAdapter.notifyDataSetChanged(); // Cập nhật adapter để hiển thị dữ liệu
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                Log.e("Error: ", e.getMessage());
            } finally {
                try {
                    connection.close(); // Đóng kết nối sau khi hoàn tất
                } catch (SQLException e) {
                    Log.e("Error: ", e.getMessage());
                }
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }
    // Phương thức để tải ảnh từ URL và chuyển thành byte[]
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
    private void addControl() {
        search_thuvien_addPlaylist = view.findViewById(R.id.search_thuvien_addPlaylist);
        recycler_Playlist_thuvien_add = view.findViewById(R.id.recycler_Playlist_thuvien_add);
        edt_tenPlaylist = view.findViewById(R.id.edt_tenPlaylist);
        linear_addplayist_tv = view.findViewById(R.id.linear_addplayist_tv);
        btn_xong_addplaylist_thuvien1 = view.findViewById(R.id.btn_xong_addplaylist_thuvien1);
        linear_addplayist_tv1 = view.findViewById(R.id.linear_addplayist_tv1);
        btn_xong_addplaylist_thuvien = view.findViewById(R.id.btn_xong_addplaylist_thuvien);
        arrayList = new ArrayList<>();
        addNgheSiAdapter = new AddNgheSiAdapter(requireContext(), arrayList);
        addNgheSiAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                insertSongPlaylist(data,userId);
//                if (getActivity() instanceof MainActivity) {
//                    Integer ID = getActivity().getSharedPreferences("MyID", MODE_PRIVATE).getInt("ID", 0);
//                    MainActivity mainActivity = (MainActivity) getActivity();
//                    Integer maU = mainActivity.getMyVariable();
//                    database = getActivity().openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
//                    Cursor cursor = database.rawQuery("select * from Songs", null);
//                    while (cursor.moveToNext()) {
//                        String ten = cursor.getString(2);
//                        Integer idSong = Integer.valueOf(cursor.getString(0) + "");
//                        if (data.equals(ten)) {
//                            ContentValues values = new ContentValues();
//                            values.put("UserID", maU);
//                            values.put("SongID", idSong);
//                            values.put("ID_Playlist_User", ID);
//                            dbHelper = DatabaseManager.dbHelper(requireContext());
//                            long kq = dbHelper.getReadableDatabase().insert("Playlist_User_Song", null, values);
//                            if (kq > 0) {
//                                break;
//                            }
//                        }
//                    }
//                    cursor.close();
//                }

            }
        });
        recycler_Playlist_thuvien_add.setAdapter(addNgheSiAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        recycler_Playlist_thuvien_add.setLayoutManager(gridLayoutManager);
    }

    private void insertSongPlaylist(String data, Integer maU) {
        Integer ID = getActivity().getSharedPreferences("MyID", MODE_PRIVATE).getInt("ID", 0);
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                // Truy vấn tất cả bài hát từ bảng Songs
                String query = "SELECT * FROM Song";
                Statement stmt = connection.createStatement();
                ResultSet resultSet = stmt.executeQuery(query);

                while (resultSet.next()) {
                    String ten = resultSet.getString("SongName");
                    Integer idSong = resultSet.getInt("SongID");

                    if (data.equals(ten)) {
                        // Thêm bài hát vào playlist
                        String insertQuery = "INSERT INTO Playlist_User_Song (SongID, ID_Playlist_User) VALUES (?, ?)";
                        PreparedStatement pstmt = connection.prepareStatement(insertQuery);
                        pstmt.setInt(1, idSong);
                        pstmt.setInt(2, ID);

                        int kq = pstmt.executeUpdate();
                        if (kq > 0) {
                            break; // Ngừng vòng lặp khi thêm thành công
                        }
                    }
                }
                resultSet.close();
                connection.close();
            } catch (SQLException e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }

    }
}
