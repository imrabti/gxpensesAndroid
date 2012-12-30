package com.nuvola.gxpenses.security;

import android.content.SharedPreferences;

import javax.inject.Inject;

public class SecurityUtils {
    private final SharedPreferences sharedPreferences;

    @Inject
    public SecurityUtils(final SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setCredentials(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Credentials.USERNAME.name(), username);
        editor.putString(Credentials.PASSWORD.name(), password);
        editor.commit();
    }

    public void clearCredentials() {
        if (isLoggedIn()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Credentials.USERNAME.name());
            editor.remove(Credentials.PASSWORD.name());
            editor.commit();
        }
    }

    public void updateUsername(String username) {
        if (sharedPreferences.contains(Credentials.USERNAME.name())) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Credentials.USERNAME.name(), username);
            editor.commit();
        }
    }

    public void updatePassword(String password) {
        if (sharedPreferences.contains(Credentials.USERNAME.name())) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Credentials.PASSWORD.name(), password);
            editor.commit();
        }
    }

    public String getUsername() {
        return sharedPreferences.getString(Credentials.USERNAME.name(), null);
    }

    public String getPassword() {
        return sharedPreferences.getString(Credentials.PASSWORD.name(), null);
    }

    public Boolean isLoggedIn() {
        return sharedPreferences.contains(Credentials.USERNAME.name()) &&
                sharedPreferences.contains(Credentials.PASSWORD.name());
    }
}
