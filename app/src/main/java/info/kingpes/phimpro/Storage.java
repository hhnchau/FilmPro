package info.kingpes.phimpro;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Chau Huynh on 09/02/02017.
 */

public class Storage {
    private static Storage instance;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static Storage getInstance(Context context) {
        if (instance == null) {
            instance = new Storage();
            preferences = context.getSharedPreferences("Storage", MODE_PRIVATE);
            editor = preferences.edit();
            editor.apply();
        }
        return instance;
    }

    public void setApiKey(String s) {
        editor.putString("api-key", s);
        editor.commit();
    }

    public String getApiKey() {
        return preferences.getString("api-key", "AIzaSyBnSYRaGovmp9dAwNpy1oQCy_hM0ynXRBU");
    }

    public void setPlaylistId(String s) {
        editor.putString("playlist-id", s);
        editor.commit();
    }

    public String getPlaylistId() {
        return preferences.getString("playlist-id", "");
    }

    public void setCategory(String s) {
        editor.putString("category", s);
        editor.commit();
    }

    public String getCategory() {
        return preferences.getString("category", "");
    }

    public void setHost(String s) {
        editor.putString("host", s);
        editor.commit();
    }

    public String getHost() {
        return preferences.getString("host", "http://phimpro.info");
    }


}
