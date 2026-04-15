package com.gymbrut.app.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "gymbrut_session";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_ONBOARDING_DONE = "onboarding_done";
    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() { return prefs.getBoolean(KEY_LOGGED_IN, false); }
    public void setLoggedIn(boolean value) { prefs.edit().putBoolean(KEY_LOGGED_IN, value).apply(); }
    public boolean isOnboardingDone() { return prefs.getBoolean(KEY_ONBOARDING_DONE, false); }
    public void setOnboardingDone(boolean value) { prefs.edit().putBoolean(KEY_ONBOARDING_DONE, value).apply(); }
    public void logout() { prefs.edit().putBoolean(KEY_LOGGED_IN, false).apply(); }
}
