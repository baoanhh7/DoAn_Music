package com.example.doan_music.fragment.library;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.activity.MainActivity;
import com.example.doan_music.adapter.thuvien.AddNgheSiAdapter;
import com.example.doan_music.data.DbHelper;
import com.example.doan_music.database.ConnectionClass;
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

/**
 * A simple {@link// Fragment} subclass.
 * Use the {@link// AddNgheSiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNgheSiFragment extends Fragment implements OnItemClickListener {

    SearchView search_thuvien_addArtist;
    RecyclerView recycler_Artists_thuvien_add;
    AddNgheSiAdapter addNgheSiAdapter;
    ArrayList<AddNgheSi_ThuVien> arrayList;
    View view;
    SQLiteDatabase database = null;
    DbHelper dbHelper;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_nghe_si, container, false);
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            userId = mainActivity.getMyVariable();
        }
        addControl();
        loadData();
        addEvents();
        // Inflate the layout for this fragment
        return view;
    }

    private void addEvents() {
        search_thuvien_addArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    private void loadData() {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "SELECT * " +
                        "FROM Artist " +
                        "WHERE Artist.ArtistID NOT IN (SELECT ArtistID " +
                        "FROM User_Artist " +
                        "WHERE UserID = " + userId + ")";
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                arrayList.clear();
                while (resultSet.next()) {
                    String ten = resultSet.getString(2);
                    String linkImage = resultSet.getString(3);
                    // Chuyển đổi linkImage thành byte[]
                    byte[] img = getImageBytesFromURL(linkImage);
                    AddNgheSi_ThuVien addNgheSiThuVien = new AddNgheSi_ThuVien(img, ten);
                    arrayList.add(addNgheSiThuVien);
                }
                addNgheSiAdapter.notifyDataSetChanged();
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
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
        search_thuvien_addArtist = view.findViewById(R.id.search_thuvien_addArtist);
        recycler_Artists_thuvien_add = view.findViewById(R.id.recycler_Artists_thuvien_add);

        arrayList = new ArrayList<>();
        addNgheSiAdapter = new AddNgheSiAdapter(requireContext(), arrayList);
        addNgheSiAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                // Tạo đối tượng Bundle và đính kèm dữ liệu
                //Bundle bundle = new Bundle();
                // bundle.putString("key", data); // Thay "key" bằng key bạn muốn đặt cho dữ liệu
//                if (getActivity() instanceof MainActivity) {
//                    MainActivity mainActivity = (MainActivity) getActivity();
//                    Integer maU = mainActivity.getMyVariable();
//                    database = getActivity().openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
//                    Cursor cursor = database.rawQuery("select * from Artists", null);
//                    while (cursor.moveToNext()) {
//                        String ten = cursor.getString(1);
//                        Integer maArtist = Integer.valueOf(cursor.getString(0) + "");
//                        if (data.equals(ten)) {
//                            ContentValues values = new ContentValues();
//                            values.put("User_Artist_UserID", maU);
//                            values.put("User_Artist_ArtistID", maArtist);
//                            dbHelper = DatabaseManager.dbHelper(requireContext());
//                            long kq = dbHelper.getReadableDatabase().insert("User_Artist", null, values);
//                            if (kq > 0) {
//                                break;
//                            }
//                        }
//                    }
//                    cursor.close();
//
//                    // Tạo Fragment và đặt Bundle vào Fragment
//                    Library_Fragment fragment = new Library_Fragment();
//                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.frame_container, fragment);
//                    fragmentTransaction.commit();
//                }
                insertUserArtist(data, userId);
            }
        });
        recycler_Artists_thuvien_add.setAdapter(addNgheSiAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        recycler_Artists_thuvien_add.setLayoutManager(gridLayoutManager);
    }

    private void insertUserArtist(String data, Integer maU) {
        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();

        if (connection != null) {
            String query = "SELECT * FROM Artist"; // Truy vấn lấy dữ liệu từ bảng Artists
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    String ten = resultSet.getString("ArtistName"); // Thay đổi "ArtistName" thành tên cột thực tế
                    Integer maArtist = resultSet.getInt("ArtistID"); // Thay đổi "ArtistID" thành tên cột thực tế

                    if (data.equals(ten)) {
                        String insertQuery = "INSERT INTO User_Artist (UserID,ArtistID) VALUES (?, ?)";
                        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                        preparedStatement.setInt(1, maU); // Gán giá trị User_Artist_UserID
                        preparedStatement.setInt(2, maArtist); // Gán giá trị User_Artist_ArtistID

                        int kq = preparedStatement.executeUpdate(); // Thực hiện truy vấn chèn dữ liệu
                        if (kq > 0) {
                            break; // Thoát vòng lặp nếu chèn thành công
                        }
                    }
                }
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

    @Override
    public void onItemClick(String data) {

    }
}
