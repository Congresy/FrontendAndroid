package com.congresy.congresy.remote;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.domain.UserAccount;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @POST("login")
    Call<Void> login(@Query("username") String username, @Query("password") String password);

    @POST("actors")
    Call<Void> register(@Body JsonObject jsonObject);

    @POST("conferences")
    Call<Void> createConference(@Body JsonObject jsonObject);

    @GET("actors/userAccount/{username}")
    Call<UserAccount> getUserAccount(@Path("username") String username);

    @GET("conferences/detailed?order=date")
    Call<List<Conference>> getAllConferencesDetailedOrderByDate();

    @GET("actors/role/User")
    Call<List<Actor>> getAllUsers();

    @GET("actors/username/{username}")
    Call<Actor> getActorByUsername(@Path("username") String username);

    @GET("conferences/organizator/{username}")
    Call<List<Conference>> getMyConferences(@Path("username") String username);

    @GET("/events/talks/all/conferences/{idConference}")
    Call<List<Event>> getConferenceEvents(@Path("idConference") String idConference);

    @POST("events")
    Call<Event> createEvent(@Body JsonObject jsonObject);

    @PUT("conferences/add/{idConference}/participants/{idActor}")
    Call<Void> addParticipant(@Path("idConference") String idConference, @Path("idActor") String idActor);

    @DELETE("conferences/{idConference}")
    Call<Void> deleteConference(@Path("idConference") String idConference);

    @PUT("conferences/{idConference}")
    Call<Conference> editConference(@Path("idConference") String idConference, @Body JsonObject jsonObject);

    @GET("conferences/detailed/{idConference}")
    Call<Conference> getConference(@Path("idConference") String idConference);
}
