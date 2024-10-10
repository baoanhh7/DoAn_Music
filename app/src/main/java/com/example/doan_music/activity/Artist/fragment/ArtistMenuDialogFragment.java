package com.example.doan_music.activity.Artist.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.doan_music.R;
import com.example.doan_music.activity.Artist.AddSongArtisActivity;
import com.example.doan_music.activity.Artist.UpdateAlbumArtistActivity;
import com.example.doan_music.activity.Artist.ViewRevenueArtistActivity;

public class ArtistMenuDialogFragment extends DialogFragment {
    private static final String ARG_USER_ID = "user_id";
    private int userID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getInt(ARG_USER_ID);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_artist_menu, container, false);

        TextView titleView = view.findViewById(R.id.dialog_title);
        titleView.setText("Quản lý nghệ sĩ");

        View addSongButton = view.findViewById(R.id.btn_add_song);
        View updateAlbumButton = view.findViewById(R.id.btn_update_album);
        View viewRevenueButton = view.findViewById(R.id.btn_view_revenue);

        addSongButton.setOnClickListener(v -> {
            navigateToActivity(AddSongArtisActivity.class);
        });

        updateAlbumButton.setOnClickListener(v -> {
            navigateToActivity(UpdateAlbumArtistActivity.class);
        });

        viewRevenueButton.setOnClickListener(v -> {
            navigateToActivity(ViewRevenueArtistActivity.class);
        });

        return view;
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(getActivity(), activityClass);
        intent.putExtra("UserID", userID);
        startActivity(intent);
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
    public static ArtistMenuDialogFragment newInstance(int userID) {
        ArtistMenuDialogFragment fragment = new ArtistMenuDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userID);
        fragment.setArguments(args);
        return fragment;
    }
}
