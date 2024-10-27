package com.example.doan_music.offline;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.doan_music.offline.model.UserOffline;

// UserPreferences.java
public class UserPreferences {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_PREMIUM = "is_premium";
    private static final String KEY_PREMIUM_EXPIRE_DATE = "premium_expire_date";
    private static final String KEY_AUTH_TOKEN = "auth_token";

    private SharedPreferences preferences;

    public UserPreferences(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Lưu thông tin user
    public void saveUser(UserOffline user) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(KEY_USER_ID, user.getUserID());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putBoolean(KEY_IS_PREMIUM, user.isPremium());
        editor.putLong(KEY_PREMIUM_EXPIRE_DATE, user.getPremiumExpireDate());
        editor.apply();
    }

    // Lưu token xác thực
    public void saveAuthToken(String token) {
        preferences.edit().putString(KEY_AUTH_TOKEN, token).apply();
    }

    // Lấy thông tin user
    public UserOffline getUserFromPreferences() {
        UserOffline user = new UserOffline();
        user.setUserID(preferences.getInt(KEY_USER_ID, -1));
        user.setUsername(preferences.getString(KEY_USERNAME, ""));
        user.setPremium(preferences.getBoolean(KEY_IS_PREMIUM, false));
        user.setPremiumExpireDate(preferences.getLong(KEY_PREMIUM_EXPIRE_DATE, 0));
        return user;
    }

    // Kiểm tra user đã login chưa
    public boolean isUserLoggedIn() {
        return preferences.getLong(KEY_USER_ID, -1) != -1;
    }

    // Lấy token xác thực
    public String getAuthToken() {
        return preferences.getString(KEY_AUTH_TOKEN, "");
    }

    // Xóa toàn bộ thông tin user (logout)
    public void clearUserData() {
        preferences.edit().clear().apply();
    }

    // Update trạng thái premium
    public void updatePremiumStatus(boolean isPremium, long expireDate) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_IS_PREMIUM, isPremium);
        editor.putLong(KEY_PREMIUM_EXPIRE_DATE, expireDate);
        editor.apply();
    }
}
