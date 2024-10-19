package com.example.doan_music.registerpremium;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan_music.R;
import com.example.doan_music.database.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

//import vn.zalopay.sdk.Environment;
//import vn.zalopay.sdk.ZaloPayError;
//import vn.zalopay.sdk.ZaloPaySDK;
//import vn.zalopay.sdk.listeners.PayOrderListener;

public class RegisterPremiumActivity extends AppCompatActivity {

    TextView total;
    Button zpay;
    Intent intent = null;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_premium);
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", -1);  // Lấy userID
        Log.e("UserID", String.valueOf(userID));
        addControls();
        //zaloPay();
        //addEvents();
    }

//    private void zaloPay() {
//        StrictMode.ThreadPolicy policy = new
//                StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        // ZaloPay SDK Init
//        ZaloPaySDK.init(2553, Environment.SANDBOX);
//    }

//    private void addEvents() {
//        String totalText = total.getText().toString().replaceAll("[^0-9]", ""); // Loại bỏ tất cả ký tự không phải số
//        zpay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                    CreateOrder orderApi = new CreateOrder();
//                    try {
//                        JSONObject data = orderApi.createOrder(totalText);
//                        Log.d("Amount",totalText);
//                        String code = data.getString("return_code");
//                        Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();
//
//                        if (code.equals("1")) {
//                            String token = data.getString("zp_trans_token");
//                            ZaloPaySDK.getInstance().payOrder(RegisterPremiumActivity.this, token, "demozpdk://app", new PayOrderListener() {
//                                @Override
//                                public void onPaymentSucceeded(String s, String s1, String s2) {
//                                    updatedataUser(userID);
//                                    insertDataHoaDonAdmin(userID);
//                                    intent =new Intent(RegisterPremiumActivity.this,SuccessfulPremiumActivity.class);
//                                    intent.putExtra("resultPremium","Thanh toan thanh cong");
//                                    startActivity(intent);
//                                }
//
//                                @Override
//                                public void onPaymentCanceled(String s, String s1) {
//                                    intent =new Intent(RegisterPremiumActivity.this,SuccessfulPremiumActivity.class);
//                                    intent.putExtra("resultPremium","Huy thanh toan");
//                                    startActivity(intent);
//                                }
//
//                                @Override
//                                public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
//                                    intent =new Intent(RegisterPremiumActivity.this,SuccessfulPremiumActivity.class);
//                                    intent.putExtra("resultPremium","Thanh toan that bai");
//                                    startActivity(intent);
//                                }
//                            });
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//        });
//    }

    // Phương thức chèn dữ liệu vào bảng HoaDonAdmin
    public void insertDataHoaDonAdmin(int userID) {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();  // Tạo kết nối SQL Server
        if (connection != null) {
            String query = "INSERT INTO HoaDon_Admin (UserID, Date, Total) VALUES (?, ?, ?)";
            try {
                // Sử dụng PreparedStatement để chèn dữ liệu
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userID);
                preparedStatement.setString(2, getCurrentDateTime()); // Lấy ngày giờ hiện tại
                preparedStatement.setDouble(3, 100000);

                // Thực thi câu lệnh chèn
                preparedStatement.executeUpdate();

                // Đóng kết nối sau khi hoàn thành
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Hàm lấy ngày giờ hiện tại dưới dạng chuỗi
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new Date());
    }


    //    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        ZaloPaySDK.getInstance().onResult(intent);
//    }
    private void updatedataUser(int id) {
        if (userID != -1) {
            ConnectionClass sql = new ConnectionClass();
            connection = sql.conClass();  // Tạo kết nối SQL Server

            if (connection != null) {
                try {
                    // Cập nhật thứ tự cho tất cả các bài hát nếu bài hát đã tồn tại
                    String updateQuery = "UPDATE Users SET Role = 'premium' WHERE UserID = ?";
                    PreparedStatement updateOrderStatement = connection.prepareStatement(updateQuery);
                    updateOrderStatement.setInt(1, id);
                    updateOrderStatement.executeUpdate();
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

    private void addControls() {
        total = findViewById(R.id.total);
        zpay = findViewById(R.id.zalo_pay_button);
    }
}