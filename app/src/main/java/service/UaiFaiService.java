package service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UaiFaiService {

    private final String API_BASE_URL = "https://c6918aa2.ngrok.io/api/";
    private static UaiFaiService _instance;

    private UserService userService;
    private WifiService wifiService;

    public UaiFaiService() {
        init();
    }

    public static UaiFaiService getInstance() {
        if (_instance == null) {
            _instance = new UaiFaiService();
        }
        return _instance;
    }

    public UserService getUserService() {
        return userService;
    }

    public WifiService getWifiService() {
        return wifiService;
    }

    private void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
        wifiService = retrofit.create(WifiService.class);
    }

}
