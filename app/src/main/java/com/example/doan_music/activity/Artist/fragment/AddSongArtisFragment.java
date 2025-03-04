//package com.example.doan_music.activity.Artist.fragment;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.example.doan_music.R;
//import com.example.doan_music.database.ConnectionClass;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class AddSongArtisFragment extends Fragment {
//
//    private static final String TAG = "AddSongArtisFragment";
//    private static final int PICK_IMAGE_REQUEST = 1;
//    private static final int PICK_AUDIO_REQUEST = 2;
//    private static final int PICK_TEXT_REQUEST = 3;
//    private ImageButton imageButton;
//    private EditText songNameEditText;
//    private Spinner albumSpinner, typeSpinner;
//    private Button selectFileButton, confirmButton,selectFileTextButton;
//
//    private Uri imageUri;
//    private Uri audioUri;
//    private Uri textUri;
//    private int userID;
//
//    private StorageReference storageReference;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            userID = getArguments().getInt("UserID", -1);
//            Log.d(TAG, "onCreate: userID = " + userID);
//        } else {
//            Log.e(TAG, "onCreate: getArguments() is null");
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_add_song_artis, container, false);
//
//        storageReference = FirebaseStorage.getInstance().getReference();
//
//        imageButton = view.findViewById(R.id.imagePlaceholder);
//        songNameEditText = view.findViewById(R.id.nameSong);
//        albumSpinner = view.findViewById(R.id.album);
//        typeSpinner = view.findViewById(R.id.TheLoai);
//        selectFileButton = view.findViewById(R.id.buttonSelectFile);
//        confirmButton = view.findViewById(R.id.btn_confirm);
//        selectFileTextButton = view.findViewById(R.id.buttonSelectFiletext);
//
//        imageButton.setOnClickListener(v -> openImagePicker());
//        selectFileButton.setOnClickListener(v -> openAudioPicker());
//        selectFileTextButton.setOnClickListener(v -> openTextPicker());
//        confirmButton.setOnClickListener(v -> uploadSongData());
//
//        loadAlbums();
//        loadTypes();
//
//        return view;
//    }
//
//    private void openImagePicker() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }
//
//    private void openAudioPicker() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("audio/*");
//        startActivityForResult(intent, PICK_AUDIO_REQUEST);
//    }
//
//    private void openTextPicker() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("text/*");
//        startActivityForResult(intent, PICK_TEXT_REQUEST);
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
//            if (requestCode == PICK_IMAGE_REQUEST) {
//                imageUri = data.getData();
//                imageButton.setImageURI(imageUri);
//            } else if (requestCode == PICK_AUDIO_REQUEST) {
//                audioUri = data.getData();
//                Toast.makeText(getContext(), "Audio file selected", Toast.LENGTH_SHORT).show();
//            } else if (requestCode == PICK_TEXT_REQUEST) {
//                textUri = data.getData();
//                Toast.makeText(getContext(), "Text file selected", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    private void loadAlbums() {
//        ConnectionClass connectionClass = new ConnectionClass();
//        Connection connection = connectionClass.conClass();
//        if (connection != null) {
//            try {
//                String query = "SELECT AlbumID, AlbumName FROM Album WHERE ArtistID = ?";
//                PreparedStatement preparedStatement = connection.prepareStatement(query);
//                preparedStatement.setInt(1, userID);
//                ResultSet resultSet = preparedStatement.executeQuery();
//
//                List<String> albumNames = new ArrayList<>();
//                albumNames.add("No Album"); // Add option for no album
//
//                while (resultSet.next()) {
//                    albumNames.add(resultSet.getString("AlbumName"));
//                }
//
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, albumNames);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                albumSpinner.setAdapter(adapter);
//
//                resultSet.close();
//                preparedStatement.close();
//            } catch (SQLException e) {
//                Log.e(TAG, "Error loading albums: " + e.getMessage());
//                Toast.makeText(getContext(), "Error loading albums", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void loadTypes() {
//        ConnectionClass connectionClass = new ConnectionClass();
//        Connection connection = connectionClass.conClass();
//        if (connection != null) {
//            try {
//                String query = "SELECT TypeID, NameType FROM Type";
//                PreparedStatement preparedStatement = connection.prepareStatement(query);
//                ResultSet resultSet = preparedStatement.executeQuery();
//
//                List<String> typeNames = new ArrayList<>();
//                typeNames.add("No Type"); // Add option for no type
//
//                while (resultSet.next()) {
//                    typeNames.add(resultSet.getString("NameType"));
//                }
//
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, typeNames);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                typeSpinner.setAdapter(adapter);
//
//                resultSet.close();
//                preparedStatement.close();
//            } catch (SQLException e) {
//                Log.e(TAG, "Error loading types: " + e.getMessage());
//                Toast.makeText(getContext(), "Error loading types", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void uploadSongData() {
//        String songName = songNameEditText.getText().toString().trim();
//        String selectedAlbum = albumSpinner.getSelectedItem().toString();
//        String selectedType = typeSpinner.getSelectedItem().toString();
//
//        if (TextUtils.isEmpty(songName)) {
//            Toast.makeText(getContext(), "Please enter a song name", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (imageUri == null || audioUri == null) {
//            Toast.makeText(getContext(), "Please select both image and audio files", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Upload image to Firebase
//        String imageFileName = UUID.randomUUID().toString();
//        StorageReference imageRef = storageReference.child("songImages/" + imageFileName);
//
//        imageRef.putFile(imageUri)
//                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(imageUri -> {
//                    String imageUrl = imageUri.toString();
//
//                    // Upload audio to Firebase
//                    String audioFileName = UUID.randomUUID().toString();
//                    StorageReference audioRef = storageReference.child("songAudio/" + audioFileName);
//
//                    audioRef.putFile(audioUri)
//                            .addOnSuccessListener(audioTaskSnapshot -> audioRef.getDownloadUrl().addOnSuccessListener(audioUri -> {
//                                String audioUrl = audioUri.toString();
//
//                                // If text file is selected, upload it
//                                if (textUri != null) {
//                                    String textFileName = UUID.randomUUID().toString();
//                                    StorageReference textRef = storageReference.child("songLyrics/" + textFileName);
//
//                                    textRef.putFile(textUri)
//                                            .addOnSuccessListener(textTaskSnapshot -> textRef.getDownloadUrl().addOnSuccessListener(textUri -> {
//                                                String textUrl = textUri.toString();
//                                                // Save song data with LRC link
//                                                saveSongToDatabase(songName, imageUrl, audioUrl, selectedAlbum, selectedType, textUrl);
//                                            }))
//                                            .addOnFailureListener(e -> {
//                                                Log.e(TAG, "Failed to upload text file: " + e.getMessage());
//                                                Toast.makeText(getContext(), "Failed to upload text file", Toast.LENGTH_SHORT).show();
//                                            });
//                                } else {
//                                    // Save song data without LRC link
//                                    saveSongToDatabase(songName, imageUrl, audioUrl, selectedAlbum, selectedType, null);
//                                }
//                            }))
//                            .addOnFailureListener(e -> {
//                                Log.e(TAG, "Failed to upload audio: " + e.getMessage());
//                                Toast.makeText(getContext(), "Failed to upload audio", Toast.LENGTH_SHORT).show();
//                            });
//                }))
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "Failed to upload image: " + e.getMessage());
//                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//
//    private void saveSongToDatabase(String songName, String imageUrl, String audioUrl,
//                                    String selectedAlbum, String selectedType, String lrcUrl) {
//        ConnectionClass connectionClass = new ConnectionClass();
//        Connection connection = connectionClass.conClass();
//        if (connection != null) {
//            try {
//                String query = "INSERT INTO Song (SongName, SongImage, LinkSong, AlbumID, TypeID, ArtistID, Views, LinkLRC) " +
//                        "VALUES (?, ?, ?, ?, ?, ?, 0, ?)";
//                PreparedStatement preparedStatement = connection.prepareStatement(query);
//                preparedStatement.setString(1, songName);
//                preparedStatement.setString(2, imageUrl);
//                preparedStatement.setString(3, audioUrl);
//
//                // Set AlbumID (can be null)
//                if ("No Album".equals(selectedAlbum)) {
//                    preparedStatement.setNull(4, java.sql.Types.INTEGER);
//                } else {
//                    int albumId = getAlbumId(selectedAlbum);
//                    preparedStatement.setInt(4, albumId);
//                }
//
//                // Set TypeID (can be null)
//                if ("No Type".equals(selectedType)) {
//                    preparedStatement.setNull(5, java.sql.Types.INTEGER);
//                } else {
//                    int typeId = getTypeId(selectedType);
//                    preparedStatement.setInt(5, typeId);
//                }
//
//                preparedStatement.setInt(6, userID);
//
//                // Set LinkLRC (can be null)
//                if (lrcUrl == null) {
//                    preparedStatement.setNull(7, java.sql.Types.VARCHAR);
//                } else {
//                    preparedStatement.setString(7, lrcUrl);
//                }
//
//                int rowsAffected = preparedStatement.executeUpdate();
//                if (rowsAffected > 0) {
//                    Toast.makeText(getContext(), "Song added successfully", Toast.LENGTH_SHORT).show();
//                    clearInputFields();
//                } else {
//                    Toast.makeText(getContext(), "Failed to add song", Toast.LENGTH_SHORT).show();
//                }
//
//                preparedStatement.close();
//            } catch (SQLException e) {
//                Log.e(TAG, "Error saving song: " + e.getMessage());
//                Toast.makeText(getContext(), "Error saving song", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//
//    private int getAlbumId(String albumName) throws SQLException {
//        ConnectionClass connectionClass = new ConnectionClass();
//        Connection connection = connectionClass.conClass();
//        String query = "SELECT AlbumID FROM Album WHERE AlbumName = ? AND ArtistID = ?";
//        PreparedStatement preparedStatement = connection.prepareStatement(query);
//        preparedStatement.setString(1, albumName);
//        preparedStatement.setInt(2, userID);
//        ResultSet resultSet = preparedStatement.executeQuery();
//        if (resultSet.next()) {
//            return resultSet.getInt("AlbumID");
//        }
//        return -1; // Return -1 if album not found
//    }
//
//    private int getTypeId(String typeName) throws SQLException {
//        ConnectionClass connectionClass = new ConnectionClass();
//        Connection connection = connectionClass.conClass();
//        String query = "SELECT TypeID FROM Type WHERE NameType = ?";
//        PreparedStatement preparedStatement = connection.prepareStatement(query);
//        preparedStatement.setString(1, typeName);
//        ResultSet resultSet = preparedStatement.executeQuery();
//        if (resultSet.next()) {
//            return resultSet.getInt("TypeID");
//        }
//        return -1; // Return -1 if type not found
//    }
//
//    private void clearInputFields() {
//        songNameEditText.setText("");
//        imageButton.setImageResource(R.drawable.ic_add_picture); // Reset to default image
//        imageUri = null;
//        audioUri = null;
//        textUri = null;
//        albumSpinner.setSelection(0);
//        typeSpinner.setSelection(0);
//    }
//}
package com.example.doan_music.activity.Artist.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.doan_music.R;
import com.example.doan_music.designPattern.RepositoryPattern.IF.AlbumRepository;
import com.example.doan_music.designPattern.RepositoryPattern.IF.SongRepository;
import com.example.doan_music.designPattern.RepositoryPattern.IF.TypeRepository;

import com.example.doan_music.designPattern.RepositoryPattern.classIPL.AlbumRepositoryImpl;
import com.example.doan_music.designPattern.RepositoryPattern.classIPL.SongRepositoryImpl;
import com.example.doan_music.designPattern.RepositoryPattern.classIPL.TypeRepositoryImpl;
import com.example.doan_music.model.Song;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AddSongArtisFragment extends Fragment {

    private static final String TAG = "AddSongArtisFragment";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_AUDIO_REQUEST = 2;
    private static final int PICK_TEXT_REQUEST = 3;

    private ImageButton imageButton;
    private EditText songNameEditText;
    private Spinner albumSpinner, typeSpinner;
    private Button selectFileButton, confirmButton, selectFileTextButton;

    private Uri imageUri;
    private Uri audioUri;
    private Uri textUri;
    private int userID;

    private StorageReference storageReference;

    // Repository interfaces
    private SongRepository songRepository;
    private AlbumRepository albumRepository;
    private TypeRepository typeRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getInt("UserID", -1);
            Log.d(TAG, "onCreate: userID = " + userID);
        } else {
            Log.e(TAG, "onCreate: getArguments() is null");
        }
        // Khởi tạo các repository
        songRepository = new SongRepositoryImpl();
        albumRepository = new AlbumRepositoryImpl();
        typeRepository = new TypeRepositoryImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_song_artis, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();

        imageButton = view.findViewById(R.id.imagePlaceholder);
        songNameEditText = view.findViewById(R.id.nameSong);
        albumSpinner = view.findViewById(R.id.album);
        typeSpinner = view.findViewById(R.id.TheLoai);
        selectFileButton = view.findViewById(R.id.buttonSelectFile);
        confirmButton = view.findViewById(R.id.btn_confirm);
        selectFileTextButton = view.findViewById(R.id.buttonSelectFiletext);

        imageButton.setOnClickListener(v -> openImagePicker());
        selectFileButton.setOnClickListener(v -> openAudioPicker());
        selectFileTextButton.setOnClickListener(v -> openTextPicker());
        confirmButton.setOnClickListener(v -> uploadSongData());

        loadAlbums();
        loadTypes();

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    private void openTextPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        startActivityForResult(intent, PICK_TEXT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
                imageButton.setImageURI(imageUri);
            } else if (requestCode == PICK_AUDIO_REQUEST) {
                audioUri = data.getData();
                Toast.makeText(getContext(), "Audio file selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == PICK_TEXT_REQUEST) {
                textUri = data.getData();
                Toast.makeText(getContext(), "Text file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadAlbums() {
        try {
            List<String> albumNames = albumRepository.getAlbumNamesByArtist(userID);
            if (albumNames == null || albumNames.isEmpty()) {
                albumNames.add("No Album");
            } else {
                albumNames.add(0, "No Album"); // Thêm option "No Album" ở đầu danh sách
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, albumNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            albumSpinner.setAdapter(adapter);
        } catch (SQLException e) {
            Log.e(TAG, "Error loading albums: " + e.getMessage());
            Toast.makeText(getContext(), "Error loading albums", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTypes() {
        try {
            List<String> typeNames = typeRepository.getTypeNames();
            if (typeNames == null || typeNames.isEmpty()) {
                typeNames.add("No Type");
            } else {
                typeNames.add(0, "No Type"); // Thêm option "No Type" ở đầu danh sách
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, typeNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeSpinner.setAdapter(adapter);
        } catch (SQLException e) {
            Log.e(TAG, "Error loading types: " + e.getMessage());
            Toast.makeText(getContext(), "Error loading types", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadSongData() {
        String songName = songNameEditText.getText().toString().trim();
        String selectedAlbum = albumSpinner.getSelectedItem().toString();
        String selectedType = typeSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(songName)) {
            Toast.makeText(getContext(), "Please enter a song name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null || audioUri == null) {
            Toast.makeText(getContext(), "Please select both image and audio files", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Firebase
        String imageFileName = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("songImages/" + imageFileName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    // Upload audio to Firebase
                    String audioFileName = UUID.randomUUID().toString();
                    StorageReference audioRef = storageReference.child("songAudio/" + audioFileName);

                    audioRef.putFile(audioUri)
                            .addOnSuccessListener(audioTaskSnapshot -> audioRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                                String audioUrl = uri1.toString();

                                // Nếu có file text (lyrics) được chọn, upload nó
                                if (textUri != null) {
                                    String textFileName = UUID.randomUUID().toString();
                                    StorageReference textRef = storageReference.child("songLyrics/" + textFileName);

                                    textRef.putFile(textUri)
                                            .addOnSuccessListener(textTaskSnapshot -> textRef.getDownloadUrl().addOnSuccessListener(uri2 -> {
                                                String textUrl = uri2.toString();
                                                // Lưu dữ liệu bài hát với link lyrics
                                                saveSongToDatabase(songName, imageUrl, audioUrl, selectedAlbum, selectedType, textUrl);
                                            }))
                                            .addOnFailureListener(e -> {
                                                Log.e(TAG, "Failed to upload text file: " + e.getMessage());
                                                Toast.makeText(getContext(), "Failed to upload text file", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    // Lưu dữ liệu bài hát mà không có lyrics
                                    saveSongToDatabase(songName, imageUrl, audioUrl, selectedAlbum, selectedType, null);
                                }
                            }))
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to upload audio: " + e.getMessage());
                                Toast.makeText(getContext(), "Failed to upload audio", Toast.LENGTH_SHORT).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload image: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Vì model Song vẫn giữ SongImage dưới dạng byte[],
     * nên chúng ta cần chuyển URL (String) thành dữ liệu nhị phân (byte[]) trước khi lưu.
     * Việc này được thực hiện trên background thread bằng AsyncTask.
     */
//    private void saveSongToDatabase(String songName, String imageUrl, String audioUrl,
//                                    String selectedAlbum, String selectedType, String lrcUrl) {
//        new DownloadImageTask(songName, imageUrl, audioUrl, selectedAlbum, selectedType, lrcUrl).execute();
//    }
    private void saveSongToDatabase(String songName, String imageUrl, String audioUrl,
                                    String selectedAlbum, String selectedType, String lrcUrl) {
        Song song = new Song();
        song.setSongName(songName);
        // Chuyển URL của ảnh thành byte[] (với imageUrl.getBytes())
        song.setSongImage(imageUrl.getBytes());
        song.setLinkSong(audioUrl);

        try {
            int albumId = "No Album".equals(selectedAlbum) ? -1 : albumRepository.getAlbumIdByNameAndArtist(selectedAlbum, userID);
            int typeId = "No Type".equals(selectedType) ? -1 : typeRepository.getTypeIdByName(selectedType);
            song.setAlbumID(albumId);
            song.setTypeID(typeId);
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving IDs: " + e.getMessage());
            Toast.makeText(getContext(), "Error retrieving album or type", Toast.LENGTH_SHORT).show();
            return;
        }

        song.setArtistID(userID);
        song.setLinkLrc(lrcUrl);

        if (songRepository.addSong(song)) {
            Toast.makeText(getContext(), "Song added successfully", Toast.LENGTH_SHORT).show();
            clearInputFields();
        } else {
            Toast.makeText(getContext(), "Failed to add song", Toast.LENGTH_SHORT).show();
        }
    }

//    private class DownloadImageTask extends AsyncTask<Void, Void, byte[]> {
//        private String songName, imageUrl, audioUrl, selectedAlbum, selectedType, lrcUrl;
//
//        public DownloadImageTask(String songName, String imageUrl, String audioUrl,
//                                 String selectedAlbum, String selectedType, String lrcUrl) {
//            this.songName = songName;
//            this.imageUrl = imageUrl;
//            this.audioUrl = audioUrl;
//            this.selectedAlbum = selectedAlbum;
//            this.selectedType = selectedType;
//            this.lrcUrl = lrcUrl;
//        }

//        @Override
//        protected byte[] doInBackground(Void... voids) {
//            return getImageBytesFromUrl(imageUrl);
//        }
//
//        @Override
//        protected void onPostExecute(byte[] imageBytes) {
//            if (imageBytes == null) {
//                Toast.makeText(getContext(), "Error downloading image", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Song song = new Song();
//            song.setSongName(songName);
//            song.setSongImage(imageBytes);
//            song.setLinkSong(audioUrl);
//
//            try {
//                int albumId = "No Album".equals(selectedAlbum) ? -1 : albumRepository.getAlbumIdByNameAndArtist(selectedAlbum, userID);
//                int typeId = "No Type".equals(selectedType) ? -1 : typeRepository.getTypeIdByName(selectedType);
//                song.setAlbumID(albumId);
//                song.setTypeID(typeId);
//            } catch (SQLException e) {
//                Log.e(TAG, "Error retrieving IDs: " + e.getMessage());
//                Toast.makeText(getContext(), "Error retrieving album or type", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            song.setArtistID(userID);
//            song.setLinkLrc(lrcUrl);
//
//            if (songRepository.addSong(song)) {
//                Toast.makeText(getContext(), "Song added successfully", Toast.LENGTH_SHORT).show();
//                clearInputFields();
//            } else {
//                Toast.makeText(getContext(), "Failed to add song", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private byte[] getImageBytesFromUrl(String imageUrl) {
//        try {
//            URL url = new URL(imageUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = input.read(buffer)) != -1) {
//                output.write(buffer, 0, bytesRead);
//            }
//            input.close();
//            return output.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    private void clearInputFields() {
        songNameEditText.setText("");
        imageButton.setImageResource(R.drawable.ic_add_picture); // Reset to default image
        imageUri = null;
        audioUri = null;
        textUri = null;
        albumSpinner.setSelection(0);
        typeSpinner.setSelection(0);
    }
}

