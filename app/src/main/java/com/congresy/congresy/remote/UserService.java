package com.congresy.congresy.remote;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @POST("login")
    Call<Void> login(@Query("username") String username, @Query("password") String password);

    @POST("actors")
    Call<Void> register(@Body JsonObject jsonObject);

    @POST("./")
    Call<Void> createConference(@Body JSONObject jsonObject);

    @GET("userAccount/{username}")
    Call<Void> getUserAccount(@Path("username") String username);

    @GET("conferences/detailed?order=date")
    Call<Void> getAllConferencesDetailed();
}
