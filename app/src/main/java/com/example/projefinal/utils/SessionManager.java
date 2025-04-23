package com.example.projefinal.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
    private static final String PREF_NAME = "CosmeticsStoreSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_IS_ADMIN = "isAdmin";
    
    private SharedPreferences pref;
    private Editor editor;
    private Context context;
    private static SessionManager instance;

    private SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void createLoginSession(int userId, String phone, boolean isAdmin) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_PHONE, phone);
        editor.putBoolean(KEY_IS_ADMIN, isAdmin);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isAdmin() {
        return pref.getBoolean(KEY_IS_ADMIN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUserPhone() {
        return pref.getString(KEY_PHONE, null);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }

    public boolean checkLogin() {
        if (!isLoggedIn()) {
            return false;
        }
        return true;
    }
}
