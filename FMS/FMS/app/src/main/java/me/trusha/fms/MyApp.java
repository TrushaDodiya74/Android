package me.trusha.fms;

import android.app.Application;

public class MyApp extends Application {
    private String userEmail;
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
        if (email != null) {
            getSharedPreferences("AppSettings", MODE_PRIVATE)
                    .edit()
                    .putString("user_email", email)
                    .apply();
        }
    }
}