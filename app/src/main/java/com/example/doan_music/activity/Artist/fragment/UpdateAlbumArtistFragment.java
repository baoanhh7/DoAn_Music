package com.example.doan_music.activity.Artist.fragment;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.doan_music.R;
import com.example.doan_music.database.ConnectionClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class UpdateAlbumArtistFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageButton imageButton;
    private EditText albumNameEditText;
    private Button confirmButton, backButton;

    private Uri imageUri; // Lưu trữ URI của ảnh đã chọn
    private int userID;  // Nhận từ Intent hoặc phương thức truyền vào

    // Firebase Storage reference
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getInt("UserID", -1);  // Sử dụng getInt thay vì getString, với giá trị mặc định là -1
            Log.d(TAG, "onCreate: userID = " + userID);
        } else {
            Log.e(TAG, "onCreate: getArguments() is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_album_artist, container, false);
        mAuth = FirebaseAuth.getInstance();

        // Đăng nhập với email và mật khẩu


        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference("imageAlbums");


        // Ánh xạ các view
        imageButton = view.findViewById(R.id.imagePlaceholder);
        albumNameEditText = view.findViewById(R.id.namealbum);
        confirmButton = view.findViewById(R.id.btn_confirm);
        backButton = view.findViewById(R.id.btn_back);

        // Chức năng chọn ảnh
        imageButton.setOnClickListener(v -> openImagePicker());

        // Chức năng nút xác nhận
        confirmButton.setOnClickListener(v -> {
            String albumName = albumNameEditText.getText().toString().trim();

            if (TextUtils.isEmpty(albumName)) {
                Toast.makeText(getContext(), "Vui lòng nhập tên album", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri == null) {
                Toast.makeText(getContext(), "Vui lòng chọn ảnh album", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userID == -1) {  // Kiểm tra giá trị mặc định
                Log.e(TAG, "onCreateView: userID is invalid");
                Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                // Có thể thêm code để quay lại màn hình đăng nhập ở đây
                return;
            }

            // Tải ảnh lên Firebase
            uploadImageToFirebase(albumName);
        });

        // Xử lý nút back
        backButton.setOnClickListener(v -> {
            // Quay về màn hình trước hoặc đóng fragment
            requireActivity().onBackPressed();
        });

        return view;
    }

    private void signInWithEmailAndPassword() {
        String email = "baofcvcl@gmail.com"; // Email của bạn
        String password = "baodung09"; // Mật khẩu của bạn

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                // Đăng nhập thành công
                currentUser = mAuth.getCurrentUser();
                Toast.makeText(getContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                // Tiếp tục tải ảnh lên hoặc các thao tác khác
            } else {
                // Xử lý lỗi đăng nhập
                Log.e("FirebaseAuth", "Authentication failed", task.getException());
                Toast.makeText(getContext(), "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Lưu trữ URI của ảnh đã chọn
            imageButton.setImageURI(imageUri); // Hiển thị ảnh đã chọn
        }
    }

    private void uploadImageToFirebase(String albumName) {
        // Tạo một tên duy nhất cho ảnh
        String fileName = UUID.randomUUID().toString();

        // Tạo tham chiếu đến Firebase Storage
        StorageReference fileReference = storageReference.child(fileName);

        // Tải ảnh lên Firebase Storage
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Log.d(TAG, "uploadImageToFirebase: Image URL = " + imageUrl);
                    Log.d(TAG, "uploadImageToFirebase: userID = " + userID);
                    updateAlbumInDatabase(albumName, imageUrl, userID);
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "uploadImageToFirebase: Failed to upload image", e);
                    Toast.makeText(getContext(), "Tải ảnh lên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateAlbumInDatabase(String albumName, String imageUrl, int userID) {
        if (albumName == null || imageUrl == null || userID == -1) {
            Log.e(TAG, "updateAlbumInDatabase: Some parameters are invalid. albumName: " + albumName + ", imageUrl: " + imageUrl + ", userID: " + userID);
            Toast.makeText(getContext(), "Thông tin album không đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.conClass(); // Kết nối SQL Server

        if (connection == null) {
            Toast.makeText(getContext(), "Không thể kết nối cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Tạo câu lệnh SQL để thêm album mới
            String query = "INSERT INTO Album (AlbumName, AlbumImage, ArtistID) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, albumName);    // Gán giá trị AlbumName
            preparedStatement.setString(2, imageUrl);     // Gán giá trị AlbumImage (URL ảnh)
            preparedStatement.setInt(3, userID);          // Gán giá trị ArtistID (không cần chuyển đổi)

            // Thực hiện câu lệnh SQL
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Toast.makeText(getContext(), "Album đã được thêm thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Thêm album thất bại", Toast.LENGTH_SHORT).show();
            }

            preparedStatement.close(); // Đóng PreparedStatement sau khi sử dụng
        } catch (SQLException e) {
            Log.e(TAG, "SQL Error: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Đã xảy ra lỗi SQL khi thêm album: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Đã xảy ra lỗi khi thêm album: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close(); // Đóng kết nối sau khi sử dụng
                }
            } catch (SQLException e) {
                Log.e(TAG, "Failed to close connection: " + e.getMessage(), e);
            }
        }
    }
}
