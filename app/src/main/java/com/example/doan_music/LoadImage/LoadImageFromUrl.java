package com.example.doan_music.LoadImage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadImageFromUrl {
    private String imageUrl;

    public LoadImageFromUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public byte[] getImageBytes() {
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
        } catch (Exception e) {
            Log.e("Error: ", "Failed to load image from URL: " + e.getMessage());
            return  null;
        }
    }
}
