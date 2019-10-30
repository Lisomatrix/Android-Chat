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

    public String getToken() throws Exception {
        String encryptedToken = sharedPreferences.getString(TOKEN_KEY, "");

        if (encryptedToken.equals("")) {
            return encryptedToken;
        }

        String result = AESHelper.decrypt(encryptedToken);

        return result;
    }

    public void setToken(String token) throws Exception {
        String encryptedToken = AESHelper.encrypt(token);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, encryptedToken);
        editor.apply();
    }
}
