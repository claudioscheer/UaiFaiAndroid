package service;

import model.UserModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("sign-in")
    Call<UserModel> sigIn(@Body UserModel userModel);

}