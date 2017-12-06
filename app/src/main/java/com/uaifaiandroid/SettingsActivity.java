package com.uaifaiandroid;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parse.PreferencesHelper;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final Map<Integer, String> REFRESH_MAP_DELAY = new HashMap<Integer, String>() {
        {
            put(1, "1 minuto");
            put(2, "2 minutos");
            put(10, "10 minutos");
            put(20, "20 minutos");
        }
    };

    private final Map<Integer, String> TIME_LIMIT_NETWORKS = new HashMap<Integer, String>() {
        {
            put(30, "30 minutos");
            put(60, "60 minutos");
            put(120, "120 minutos");
            put(240, "240 minutos");
            put(360, "360 minutos");
        }
    };

    private class Setting {

        private String settingName;
        private String settingValue;

        public Setting(String settingName, String settingValue) {
            this.settingName = settingName;
            this.settingValue = settingValue;
        }

        public String getSettingName() {
            return settingName;
        }

        public void setSettingName(String settingName) {
            this.settingName = settingName;
        }

        public String getSettingValue() {
            return settingValue;
        }

        public void setSettingValue(String settingValue) {
            this.settingValue = settingValue;
        }
    }

    private ListView listSettings;
    private List<Setting> allSettingsAvailable;
    private ArrayAdapter<Setting> settingsArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        listSettings = (ListView) findViewById(R.id.listSettings);

        allSettingsAvailable = getSettings();
        settingsArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, allSettingsAvailable) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                text1.setText(allSettingsAvailable.get(position).getSettingName());
                text2.setText(allSettingsAvailable.get(position).getSettingValue());
                return view;
            }
        };
        listSettings.setAdapter(settingsArrayAdapter);
        listSettings.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        if (index == 0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            String[] items = REFRESH_MAP_DELAY.values().toArray(new String[0]);
            alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int indexSelected) {
                    PreferencesHelper.getInstance().setRefreshDelay((int) REFRESH_MAP_DELAY.keySet().toArray()[indexSelected]);
                    allSettingsAvailable = getSettings();
                    settingsArrayAdapter.notifyDataSetChanged();
                }
            });
            alertDialog.show();
        } else if (index == 1) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            String[] items = TIME_LIMIT_NETWORKS.values().toArray(new String[0]);
            alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int indexSelected) {
                    PreferencesHelper.getInstance().setTimeLimit((int) TIME_LIMIT_NETWORKS.keySet().toArray()[indexSelected]);
                    allSettingsAvailable = getSettings();
                    settingsArrayAdapter.notifyDataSetChanged();
                }
            });
            alertDialog.show();
        } else if (index == 2) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.show_wifi_informations);
            alertDialog.setNegativeButton(R.string.show_wifi_informations_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    PreferencesHelper.getInstance().setShowWifiInformations(false);
                    allSettingsAvailable = getSettings();
                    settingsArrayAdapter.notifyDataSetChanged();
                }
            });
            alertDialog.setPositiveButton(R.string.show_wifi_informations_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    PreferencesHelper.getInstance().setShowWifiInformations(true);
                    allSettingsAvailable = getSettings();
                    settingsArrayAdapter.notifyDataSetChanged();
                }
            });
            alertDialog.show();
        }
    }

    private List<Setting> getSettings() {
        List<Setting> settings = new ArrayList<>();
        int refreshMapDelayMinutes = PreferencesHelper.getInstance().getRefreshDelay();
        settings.add(new Setting(getString(R.string.refresh_map_delay), String.format("%s minutos", refreshMapDelayMinutes)));
        int timeLimitNetworks = PreferencesHelper.getInstance().getTimeLimit();
        settings.add(new Setting(getString(R.string.time_limit_networks), String.format("%s minutos", timeLimitNetworks)));
        settings.add(new Setting(getString(R.string.show_wifi_informations),
                PreferencesHelper.getInstance().getShowWifiInformations() ?
                        getString(R.string.show_wifi_informations_yes) :
                        getString(R.string.show_wifi_informations_no)
        ));
        return settings;
    }
}
