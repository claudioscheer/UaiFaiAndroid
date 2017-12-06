package parse;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private final String UAI_FAI_PREFERENCES = "UAI_FAI_PREFERENCES";

    private static PreferencesHelper _instance;

    private Context context;
    private SharedPreferences sharedPreferences;
    private String user;

    public PreferencesHelper(Context context) {
        this.context = context;
    }

    public static PreferencesHelper getInstance(Context context) {
        if (_instance == null) {
            _instance = new PreferencesHelper(context);
        }
        return _instance;
    }

    public static PreferencesHelper getInstance() {
        return getInstance(null);
    }

    private SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(UAI_FAI_PREFERENCES, 0);
        }
        return sharedPreferences;
    }

    private void setData(String key, boolean value, boolean block) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        if (block) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    private void setData(String key, Integer value, boolean block) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        if (block) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    private void setData(String key, String value, boolean block) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        if (block) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    public void setUser(String value) {
        user = value;
        setData("USER_ID", value, true);
    }

    public String getUser() {
        if (user == null) {
            user = getSharedPreferences().getString("USER_ID", "null");
        }
        return user;
    }

    public void setRefreshDelay(int value) {
        setData("REFRESH_DELAY", value, false);
    }

    public int getRefreshDelay() {
        return getSharedPreferences().getInt("REFRESH_DELAY", 1);
    }

    public void setTimeLimit(int value) {
        setData("TIME_LIMIT", value, false);
    }

    public int getTimeLimit() {
        return getSharedPreferences().getInt("TIME_LIMIT", 30);
    }

    public void setShowWifiInformations(boolean value) {
        setData("SHOW_WIFI_INFORMATIONS", value, false);
    }

    public boolean getShowWifiInformations() {
        return getSharedPreferences().getBoolean("SHOW_WIFI_INFORMATIONS", false);
    }
}
