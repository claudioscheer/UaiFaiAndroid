package analyzer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import model.GeoJSONModel;
import model.WifiSimpleDataSQLiteModel;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityCompat.checkSelfPermission(AnalyzerHelper.getInstance().getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        AnalyzerHelper.getInstance().getLocationManager()
                .requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            Date dateNow = new Date();

                            Double[] latitudeLongitude = new Double[]{location.getLatitude(), location.getLongitude()};
                            GeoJSONModel geoJSONModel = new GeoJSONModel();
                            geoJSONModel.setCoordinates(latitudeLongitude);

                            for (ScanResult scanResult : AnalyzerHelper.getInstance().getWifiManager().getScanResults()) {
                                //if (!isOpenWifi(scanResult.capabilities)) {
                                //    continue;
                                //}

                                WifiSimpleDataSQLiteModel wifiSimpleDataSQLiteModel = new WifiSimpleDataSQLiteModel();
                                wifiSimpleDataSQLiteModel.setCreatedAt(dateNow);
                                wifiSimpleDataSQLiteModel.setKey(String.format("%s|%s", scanResult.BSSID, scanResult.SSID));
                                wifiSimpleDataSQLiteModel.setPower(WifiManager.calculateSignalLevel(scanResult.level, 10));
                                wifiSimpleDataSQLiteModel.setDistanceToAccessPoint(calculateDistanceToAccessPoint(scanResult.level, scanResult.frequency));
                                wifiSimpleDataSQLiteModel.setLocation(geoJSONModel);

                                AnalyzerHelper.getInstance().getWifiSQLiteHelper().save(wifiSimpleDataSQLiteModel);
                            }
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                }, null);
    }

    private double calculateDistanceToAccessPoint(double signalLevelInDb, double freqInMHz) {
        // https://stackoverflow.com/questions/11217674/how-to-calculate-distance-from-wifi-router-using-signal-strength
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    private boolean isOpenWifi(String capabilities) {
        if (capabilities.toUpperCase().contains("WEP")) {
            return false;
        } else if (capabilities.toUpperCase().contains("WPA") || capabilities.toUpperCase().contains("WPA2")) {
            return false;
        } else {
            return true;
        }
    }
}
