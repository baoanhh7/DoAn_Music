package com.example.doan_music.fragment.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.adapter.spotify.SpotifyAdapter;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.model.Spotifyitem;
import com.example.doan_music.registerpremium.RegisterPremiumActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Spotify_Fragment extends Fragment {
    RecyclerView recyclerViewSpot;
    SpotifyAdapter spotifyAdapter;
    ArrayList<Spotifyitem> arr_spot;
    Button btn_registerpremium;
    LinearLayout LNL_member, LNL_premium;
    View view;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    int userID;
    private RecyclerView premiumscrollview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_spotify_, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", -1);  // Lấy userID
        Log.e("UserID", String.valueOf(userID));
        addControls();
        loadData();
        addEvents();
        return view;
    }

    private void addEvents() {
        btn_registerpremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterPremiumActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addControls() {
        btn_registerpremium = view.findViewById(R.id.btn_registerpremium);
        LNL_member = view.findViewById(R.id.LNL_member);
        LNL_premium = view.findViewById(R.id.LNL_premium);
    }

    private void loadData() {
        LNL_premium.setVisibility(View.GONE);
        LNL_member.setVisibility(View.GONE);
        int visibilityLNL_member = LNL_member.getVisibility();
        int visibilityLNL_premium = LNL_premium.getVisibility();
        // Kiểm tra nếu userID hợp lệ
        if (userID != -1) {
            ConnectionClass sql = new ConnectionClass();
            connection = sql.conClass();  // Tạo kết nối SQL Server

            if (connection != null) {
                try {
                    // Cập nhật thứ tự cho tất cả các bài hát nếu bài hát đã tồn tại
                    String updateQuery = "Select Role from Users WHERE UserID = " + userID;
                    smt = connection.createStatement();
                    resultSet = smt.executeQuery(updateQuery);
                    while (resultSet.next()) {
                        String role = resultSet.getString(1);
                        if (role.equals("member") && visibilityLNL_member == View.GONE) {
                            LNL_member.setVisibility(View.VISIBLE);
                            LNL_premium.setVisibility(View.GONE);
                        } else if (role.equals("premium") && visibilityLNL_premium == View.GONE) {
                            LNL_member.setVisibility(View.GONE);
                            LNL_premium.setVisibility(View.VISIBLE);
                        }
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

}