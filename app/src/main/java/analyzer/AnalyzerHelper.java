package analyzer;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import parse.PreferencesHelper;
import sqlite.WifiSQLiteHelper;

public class AnalyzerHelper {

    private static AnalyzerHelper _instance;

    private Context context;
    private WifiManager wifiManager;
    private WifiSQLiteHelper wifiSQLiteHelper;
    private String userId;
    private ConnectivityManager connectivityManager;
    private LocationManager locationManager;

    public AnalyzerHelper(Context context) {
        this.context = context;
    }

    public static AnalyzerHelper getInstance(Context context) {
        if (_instance == null) {
            _instance = new AnalyzerHelper(context);
        }
        return _instance;
    }

    public static AnalyzerHelper getInstance() {
        return getInstance(null);
    }

    public Context getContext() {
        return context;
    }

    public WifiManager getWifiManager() {
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        return wifiManager;
    }

    public WifiSQLiteHelper getWifiSQLiteHelper() {
        if (wifiSQLiteHelper == null) {
            wifiSQLiteHelper = new WifiSQLiteHelper(context);
        }
        return wifiSQLiteHelper;
    }

    public String getUserId() {
        if (userId == null) {
            userId = PreferencesHelper.getInstance(context).getUser();
        }
        return userId;
    }

    public ConnectivityManager getConnectivityManager() {
        if (connectivityManager == null) {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        return connectivityManager;
    }

    public boolean isUserConnected() {
        NetworkInfo networkInfo = getConnectivityManager().getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public LocationManager getLocationManager() {
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        return locationManager;
    }
}
