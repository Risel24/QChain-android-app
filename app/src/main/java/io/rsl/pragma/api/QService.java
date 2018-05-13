package io.rsl.pragma.api;

import java.util.HashMap;


import io.rsl.pragma.api.models.AuthResponse;
import io.rsl.pragma.api.models.ContractInit;
import io.rsl.pragma.api.models.ContractsListResponse;
import io.rsl.pragma.api.models.SignupData;
import io.rsl.pragma.api.models.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface QService {

    /*@GET("/api/users/")
    Call<User> authenticate(@Query("login") String login, @Query("pass") String password);

    @POST("/api/users/")
    Call<User> register(@Query("login") String login, @Query("name") String nmae, @Query("pass") String password);*/

    /*@GET("/api/contracts")
    Call<ContractsListResponse> listContracts(@Query("id") long userID, @Query("count") int count, @Query("offset") int offset);*/

    @POST("/user/login/")
    @Headers("No-Authentication: true")
    Call<AuthResponse> auth(@Body User user);

    @POST("/user/signup/")
    @Headers("No-Authentication: true")
    Call<ResponseBody> signup(@Body SignupData signupData);

    @POST("/contracts/_idholder_/")
    @Headers("User-Required: true")
    Call<ResponseBody> postContract(@Body ContractInit contractInit);

    @PATCH("/contracts/{id}/")
    Call<ResponseBody> patchContract(@Path("id") String remoteID, @FieldMap HashMap<String, Object> data);

    @DELETE("/contracts/{id}/")
    Call<ResponseBody> deleteContract(@Path("id") String remoteID);

    @GET("/contracts/_idholder_/")
    @Headers("User-Required: true")
    Call<ContractsListResponse> getContracts();

}
