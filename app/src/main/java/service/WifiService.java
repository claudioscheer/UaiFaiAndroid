package service;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import model.WifiModel;
import model.WifiSimpleDataSQLiteModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WifiService {

    @POST("auth/add-wifi")
    Call<Void> addWifi(@Header("Authorization") String authorization, @Body List<WifiSimpleDataSQLiteModel> wifis);

    @GET("auth/get-wifi-networks/{timeLimit}/{northeast}/{southwest}")
    Call<List<WifiModel>> getWifis(@Header("Authorization") String authorization,
                                   @Path("timeLimit") int timeLimit,
                                   @Path("northeast") String northeast,
                                   @Path("southwest") String southwest);
}