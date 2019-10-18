package pt.lisomatrix.chatapplication.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String SHARED_PREFERENCES = "messenger";
    private static final String TOKEN_KEY = "messenger_key";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        this.sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES, context.getApplicationContext().MODE_PRIVATE);
    }

    public String getToken() {
        String result = sharedPreferences.getString(TOKEN_KEY, "");

        return result;
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }
}
