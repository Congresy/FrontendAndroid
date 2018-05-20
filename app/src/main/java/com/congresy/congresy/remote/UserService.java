package com.congresy.congresy.remote;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {

    @POST("login")
    Call<Void> login(@Query("username") String username, @Query("password") String password);
}
