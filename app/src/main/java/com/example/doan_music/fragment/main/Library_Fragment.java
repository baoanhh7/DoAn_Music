package com.example.doan_music.fragment.main;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.activity.MainActivity;
import com.example.doan_music.activity.library.ArtistSongActivity;
import com.example.doan_music.activity.library.PlaylistUserLoveActivity;
import com.example.doan_music.adapter.thuvien.ThuVienAdapter;
import com.example.doan_music.data.DbHelper;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.fragment.library.AddNgheSiFragment;
import com.example.doan_music.fragment.library.AddPlaylistFragment;
import com.example.doan_music.m_interface.OnItemClickListener;
import com.example.doan_music.model.ThuVien;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class Library_Fragment extends Fragment implements OnItemClickListener {
    RecyclerView recyclerView;
    ThuVienAdapter thuVienAdapter;
    ArrayList<ThuVien> arr;
    SQLiteDatabase database = null;
    DbHelper dbHelper;
    Button btnDoi;
    View view;
    ImageButton btn_thuvien_add;
    SearchView btn_thuvien_search;
    TableRow tbr_bottom_sheet_thuvien_adddanhsachphat, tbr_bottom_sheet_thuvien_addnghesy;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    private Handler handler;
    private Runnable refreshRunnable;
    private static final long REFRESH_INTERVAL = 1000;
    private int userId;

    public static byte[] convertDrawableToByteArray(Context context, int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_library_, container, false);
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            userId = mainActivity.getMyVariable();
            Log.e("userId", String.valueOf(userId));
        }
        handler = new Handler(Looper.getMainLooper());
//        refreshRunnable = new Runnable() {
//            @Override
//            public void run() {
//
//                handler.postDelayed(this, REFRESH_INTERVAL);
//            }
//        };
        addControl();
        addEvents();
        return view;
    }

//    private void startAutoRefresh() {
//        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
//    }

    @Override
    public void onResume() {
        super.onResume();
        //startAutoRefresh();
        loadDataSQLServer();
    }

    private void addEvents() {
        btn_thuvien_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenBottomSheetDialog();
            }
        });
        btn_thuvien_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                thuVienAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                thuVienAdapter.getFilter().filter(newText);
                return false;
            }
        });


    }

    private void clickOpenBottomSheetDialog() {
        View viewdialog = getLayoutInflater().inflate(R.layout.bottom_sheet_thuvien, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(viewdialog);
        // Ánh xạ tablerow trong bottom sheet
        tbr_bottom_sheet_thuvien_adddanhsachphat = viewdialog.findViewById(R.id.tbr_bottom_sheet_thuvien_adddanhsachphat);
        tbr_bottom_sheet_thuvien_addnghesy = viewdialog.findViewById(R.id.tbr_bottom_sheet_thuvien_addnghesy);
        tbr_bottom_sheet_thuvien_addnghesy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(requireContext(), AddNgheSiFragment.class);
                //startActivity(intent);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new AddNgheSiFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                // Đóng bottom sheet dialog sau khi xử lý xong
                bottomSheetDialog.dismiss();
            }
        });
        tbr_bottom_sheet_thuvien_adddanhsachphat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new AddPlaylistFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                // Đóng bottom sheet dialog sau khi xử lý xong
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDataSQLServer() {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "SELECT * " +
                        "FROM Artist " +
                        " INNER JOIN User_Artist ON Artist.ArtistID = User_Artist.ArtistID " +
                        " WHERE User_Artist.UserID = " + userId;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                arr.clear();
                while (resultSet.next()) {
                    Integer maArtist = resultSet.getInt(1);
                    String ten = resultSet.getString(2);
                    String linkImage = resultSet.getString(3);
                    // Chuyển đổi linkImage thành byte[]
                    byte[] img = getImageBytesFromURL(linkImage);
                    ThuVien thuVien = new ThuVien(img, ten);
                    arr.add(thuVien);
                }
                thuVienAdapter.notifyDataSetChanged();
                query = "SELECT * " +
                        "FROM  Playlist_User " +
                        "WHERE Playlist_User.UserID = " + userId;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                while (resultSet.next()) {
                    Integer UserID = resultSet.getInt(3);
                    String ten = resultSet.getString(2);
                    byte[] byteArray = convertDrawableToByteArray(requireContext(), R.drawable.music_logo);
                    if (userId == UserID) {
                        ThuVien thuVien = new ThuVien(byteArray, ten);
                        arr.add(thuVien);
                    }
                }
                thuVienAdapter.notifyDataSetChanged();
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

    private void loadDataSQLite() {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            Integer maU = mainActivity.getMyVariable();
            // Bundle bundle = getArguments();

            //if (bundle != null) {
            // Trích xuất dữ liệu từ Bundle
            // String data = bundle.getString("key"); // Thay "key" bằng key bạn đã đặt trong Activity
            database = getActivity().openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("select * " +
                    "from Artists " +
                    "JOIN User_Artist ON Artists.ArtistID =User_Artist.User_Artist_ArtistID " +
                    "WHERE User_Artist.User_Artist_UserID = ? ", new String[]{String.valueOf(maU)});
            arr.clear();
            while (cursor.moveToNext()) {
                Integer maArtist = Integer.valueOf(cursor.getString(0) + "");
                String ten = cursor.getString(1);
                byte[] img = cursor.getBlob(2);
                ThuVien thuVien = new ThuVien(img, ten);
                arr.add(thuVien);

            }

            cursor.close();
            cursor = database.rawQuery("select * " +
                    "from Playlist_User " +
                    "WHERE Playlist_User.UserID = ? ", new String[]{String.valueOf(maU)});
            while (cursor.moveToNext()) {
                Integer id = cursor.getInt(2);
                String ten = cursor.getString(1);
                byte[] byteArray = convertDrawableToByteArray(requireContext(), R.drawable.music_logo);
                if (maU.equals(id)) {
                    ThuVien thuVien = new ThuVien(byteArray, ten);
                    arr.add(thuVien);
                }
            }
            cursor.close();
            thuVienAdapter.notifyDataSetChanged();
        }
    }

    private void addControl() {
        btn_thuvien_search = view.findViewById(R.id.btn_thuvien_search);
        EditText editTextSearch = btn_thuvien_search.findViewById(androidx.appcompat.R.id.search_src_text);
        editTextSearch.setTextColor(getResources().getColor(R.color.white));
        recyclerView = view.findViewById(R.id.recyclerviewTV);
        btn_thuvien_add = view.findViewById(R.id.btn_thuvien_add);
        btnDoi = view.findViewById(R.id.btnDoi);
        arr = new ArrayList<>();
        thuVienAdapter = new ThuVienAdapter(this, arr);
        thuVienAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                loadDataArtistUser(data);
                loadDataPlaylistUser(data);
//                database = getActivity().openOrCreateDatabase("doanmusic.db", MODE_PRIVATE, null);
//                Cursor cursor = database.rawQuery("select * from Artists", null);
//                while (cursor.moveToNext()) {
//                    Integer Id = cursor.getInt(0);
//                    String ten = cursor.getString(1);
//                    if (data.equals(ten)) {
//                        Intent intent = new Intent(requireContext(), ArtistSongActivity.class);
//                        intent.putExtra("MaArtist", Id);
//                        startActivity(intent);
//                        break;
//                    }
//                }
//                cursor.close();
//                cursor = database.rawQuery("select * from Playlist_User", null);
//                while (cursor.moveToNext()) {
//                    Integer Id = cursor.getInt(0);
//                    String ten = cursor.getString(1);
//                    if (data.equals(ten)) {
//                        Intent intent = new Intent(requireContext(), PlaylistUserLoveActivity.class);
//                        intent.putExtra("MaPlaylist", Id);
//                        startActivity(intent);
//                        break;
//                    }
//                }
//                cursor.close();
            }
        });
        recyclerView.setAdapter(thuVienAdapter);
        LinearLayoutManager linearLayout = new LinearLayoutManager(requireContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        // recyclerViewNV.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(linearLayout);
        btnDoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getLayoutManager() == linearLayout)
                    recyclerView.setLayoutManager(gridLayoutManager);
                else
                    recyclerView.setLayoutManager(linearLayout);
            }
        });
        //recyclerViewNV.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.HORIZONTAL));
        // Lấy Bundle từ Fragment
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // Không cần xử lý kéo thả
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Lấy vị trí item bị vuốt
                int position = viewHolder.getAdapterPosition();

                // Hiển thị hộp thoại xác nhận xóa
                new AlertDialog.Builder(getContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa mục này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            // Nếu chọn "Xóa", xóa item khỏi danh sách
                            thuVienAdapter.removeItem(position);
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> {
                            // Nếu chọn "Hủy", khôi phục item bị vuốt
                            thuVienAdapter.notifyItemChanged(position);
                        })
                        .setOnCancelListener(dialog -> {
                            // Nếu hộp thoại bị hủy, khôi phục item
                            thuVienAdapter.notifyItemChanged(position);
                        })
                        .show();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // Thay đổi màu nền khi vuốt
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;

                    Paint paint = new Paint();
                    paint.setColor(Color.RED); // Màu nền đỏ
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private void loadDataPlaylistUser(String data) {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "select * from Playlist_User ";
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                while (resultSet.next()) {
                    Integer Id = resultSet.getInt(1);
                    String ten = resultSet.getString(2);
                    if (data.equals(ten)) {
                        Intent intent = new Intent(requireContext(), PlaylistUserLoveActivity.class);
                        intent.putExtra("MaPlaylist", Id);
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

    private void loadDataArtistUser(String data) {
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


    @Override
    public void onItemClick(String data) {

    }
}