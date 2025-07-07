package com.blemeterkai.meterkai.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "UserSessionPref";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_LEVEL = "userLevel";
    private static final String KEY_LOGIN_TIME = "loginTime";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // Session duration in milliseconds (3 hours)
    private static final long SESSION_DURATION = 3 * 60 * 60 * 1000; // 3 hours * 60 minutes * 60 seconds * 1000 ms

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String username, String userLevel) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_LEVEL, userLevel);
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.commit(); // Use commit() for synchronous save or apply() for asynchronous
    }

    public boolean isLoggedIn() {
        if (!pref.getBoolean(KEY_IS_LOGGED_IN, false)) {
            return false; // Not logged in at all
        }

        long loginTime = pref.getLong(KEY_LOGIN_TIME, 0);
        if (loginTime == 0) {
            clearSession(); // Should not happen if logged in, but clear if inconsistent
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if ((currentTime - loginTime) > SESSION_DURATION) {
            clearSession(); // Session expired
            return false;
        }
        return true; // Session is active and not expired
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getUserLevel() {
        return pref.getString(KEY_USER_LEVEL, null);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();

        // Also clear your static fields in MainActivity if they hold session data
        // This part needs to be called from MainActivity or LoginFragment after navigating
    }
}