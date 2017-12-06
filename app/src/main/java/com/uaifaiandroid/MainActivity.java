package com.uaifaiandroid;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.GeoJSONModel;
import model.WifiModel;
import parse.PreferencesHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.UaiFaiService;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int REQUEST_SETTINGS_ACTIVITY = 0;

    private Handler handler = new Handler();

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Call<List<WifiModel>> getWifisCall;
    private TileOverlay mOverlayHeatMap;
    private Toast toastZoom;

    // Maybe is needed a semaphore.
    private int refreshMapDelay;
    private int timeLimitNetworks;
    private boolean showWifiInformations;
    private List<Marker> listWifiInformationsMakers = new ArrayList<>();

    Runnable updateMapRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                updateMap(true);
            } catch (Exception ex) {
                Log.e("UAI_FAI", "Error on get datas.", ex);
            } finally {
                handler.postDelayed(this, refreshMapDelay);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        toastZoom = Toast.makeText(this, R.string.increase_zoom, Toast.LENGTH_SHORT);
        setTimeRefreshMap();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        initMap();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.gps_needed, Toast.LENGTH_LONG).show();
            return;
        }
        mMap.setMyLocationEnabled(true);

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
                mMap.animateCamera(cameraUpdate);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemSettings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, REQUEST_SETTINGS_ACTIVITY);
                return true;

            case R.id.itemRefreshMap:
                updateMap(false);
                return true;

            case R.id.itemHelp:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(R.string.main_menu_help);
                alertDialog.setView(R.layout.activity_help);
                alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == REQUEST_SETTINGS_ACTIVITY) {
            setTimeRefreshMap();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(updateMapRunnable);
        if (getWifisCall != null) {
            getWifisCall.cancel();
        }
        super.onDestroy();
    }

    private void initMap() {
        handler.postDelayed(updateMapRunnable, 100);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                updateMap(true);
            }
        });
    }

    private void setTimeRefreshMap() {
        refreshMapDelay = PreferencesHelper.getInstance().getRefreshDelay();
        refreshMapDelay *= 60000;
        timeLimitNetworks = PreferencesHelper.getInstance().getTimeLimit();
        showWifiInformations = PreferencesHelper.getInstance().getShowWifiInformations();
        if (!showWifiInformations && listWifiInformationsMakers.size() > 0) {
            removeWifiInformationMarkers();
        }
    }

    private void updateMap(boolean testZoom) {
        float zoom = mMap.getCameraPosition().zoom;
        if (testZoom && zoom < 14f) {
            toastZoom.show();
            return;
        }
        toastZoom.cancel();

        if (getWifisCall != null) {
            getWifisCall.cancel();
        }

        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        getWifisCall = UaiFaiService.getInstance().getWifiService()
                .getWifis(
                        PreferencesHelper.getInstance().getUser(),
                        timeLimitNetworks,
                        String.format("%s,%s", bounds.northeast.latitude, bounds.northeast.longitude),
                        String.format("%s,%s", bounds.southwest.latitude, bounds.southwest.longitude)
                );

        getWifisCall.enqueue(new Callback<List<WifiModel>>() {
            @Override
            public void onResponse(Call<List<WifiModel>> call, Response<List<WifiModel>> response) {
                if (response.code() == 200) {
                    showWifis(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<WifiModel>> call, Throwable t) {
                Log.e("UAI_FAI", "Error on get WiFi datas.", t);
            }
        });
    }

    private void showWifis(List<WifiModel> wifis) {
        List<GeoJSONModel> locations = new ArrayList<>();
        for (WifiModel wifi : wifis) {
            locations.add(wifi.getLocation());
        }
        buildHeatMap(locations);
        if (showWifiInformations) {
            removeWifiInformationMarkers();
            showWifiInformationMarkers(wifis);
        }
    }

    private void removeWifiInformationMarkers() {
        for (Marker listWifiInformationsMaker : listWifiInformationsMakers) {
            listWifiInformationsMaker.remove();
        }
        listWifiInformationsMakers.clear();
    }

    private void showWifiInformationMarkers(List<WifiModel> wifiModels) {
        for (WifiModel wifiModel : wifiModels) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(geoJSONModelToLatLng(wifiModel.getLocation()))
                    .title(wifiModel.get_id())
            );
            marker.setTag(0);
            listWifiInformationsMakers.add(marker);
        }
    }

    private void buildHeatMap(List<GeoJSONModel> locations) {
        List<LatLng> listOfPoints = getCoordinatesToMap(locations);
        if (listOfPoints.size() > 0) {
            if (mOverlayHeatMap != null) {
                mOverlayHeatMap.remove();
            }
            HeatmapTileProvider heatmapTileProvider = new HeatmapTileProvider.Builder().data(listOfPoints).build();
            mOverlayHeatMap = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
        } else {
            if (mOverlayHeatMap != null) {
                mOverlayHeatMap.remove();
            }
        }
    }

    private List<LatLng> getCoordinatesToMap(List<GeoJSONModel> locations) {
        List<LatLng> list = new ArrayList<>();
        for (GeoJSONModel location : locations) {
            list.add(geoJSONModelToLatLng(location));
        }
        return list;
    }

    private LatLng geoJSONModelToLatLng(GeoJSONModel geoJSONModel) {
        return new LatLng(geoJSONModel.getCoordinates()[0], geoJSONModel.getCoordinates()[1]);
    }
}