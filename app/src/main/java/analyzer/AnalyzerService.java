package analyzer;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import model.WifiSimpleDataSQLiteModel;
import retrofit2.Response;
import service.UaiFaiService;

public class AnalyzerService extends Service {

    private AnalyzerHelper analyzerHelper;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        analyzerHelper = AnalyzerHelper.getInstance(this);

        registerReceiver(new WifiBroadcastReceiver(), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        analyzerHelper.getWifiManager().startScan();

        new SendWifiDataToServer().start();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class SendWifiDataToServer extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    sendDatas();
                    Thread.sleep(2000);
                } catch (Exception ignored) {
                }
            }
        }

        private void sendDatas() {
            try {
                if (analyzerHelper.isUserConnected()) {
                    final List<WifiSimpleDataSQLiteModel> wifiSimpleDataSQLiteModels = analyzerHelper.getWifiSQLiteHelper().get();
                    if (wifiSimpleDataSQLiteModels.size() > 0) {
                        Response response = UaiFaiService.getInstance().getWifiService().addWifi(analyzerHelper.getUserId(), wifiSimpleDataSQLiteModels).execute();
                        if (response.code() == 200) {
                            analyzerHelper.getWifiSQLiteHelper().delete(wifiSimpleDataSQLiteModels);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("UAI_FAI_ANALYZER", "Erro ao enviar dados ao servidor.", e);
            }
        }
    }
}
