package com.congresy.congresy.remote;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.domain.SocialNetwork;
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

    @POST("logout")
    Call<Void> logout();

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

    @GET("events/all/conferences/{idConference}")
    Call<List<Event>> getConferenceEventsAllAndOrganizator(@Path("idConference") String idConference);

    @GET("events/all/conferences/{idConference}/actor/{idActor}")
    Call<List<Event>> getConferenceEventsUser(@Path("idConference") String idConference, @Path("idActor") String idActor);

    @GET("events/own/{idActor}")
    Call<List<Event>> getOwnEvents(@Path("idActor") String idActor);

    @POST("events")
    Call<Event> createEvent(@Body JsonObject jsonObject);

    @PUT("conferences/add/{idConference}/participants/{idActor}")
    Call<Void> addParticipant(@Path("idConference") String idConference, @Path("idActor") String idActor);

    @DELETE("conferences/{idConference}")
    Call<Void> deleteConference(@Path("idConference") String idConference);

    @DELETE("events/{idEvent}")
    Call<Void> deleteEvent(@Path("idEvent") String idEvent);

    @PUT("conferences/{idConference}")
    Call<Conference> editConference(@Path("idConference") String idConference, @Body JsonObject jsonObject);

    @PUT("events/{idEvent}")
    Call<Event> editEvent(@Path("idEvent") String idEvent, @Body JsonObject jsonObject);

    @GET("conferences/detailed/{idConference}")
    Call<Conference> getConference(@Path("idConference") String idConference);

    @GET("events/{idEvent}")
    Call<Event> getEvent(@Path("idEvent") String idEvent);

    @GET("socialNetworks/actor/{idActor}")
    Call<List<SocialNetwork>> getSocialNetworksByActor(@Path("idActor") String idActor);

    @GET("socialNetworks/{idSocialNetwork}")
    Call<SocialNetwork> getSocialNetwork(@Path("idSocialNetwork") String idSocialNetwork);

    @POST("socialNetworks/{idActor}")
    Call<SocialNetwork> createSocialNetwork(@Path("idActor") String idActor, @Body JsonObject jsonObject);

    @PUT("socialNetworks/{idSocialNetwork}")
    Call<SocialNetwork> editSocialNetwork(@Path("idSocialNetwork") String idSocialNetwork, @Body JsonObject jsonObject);

    @DELETE("socialNetworks/{idSocialNetwork}")
    Call<Void> deleteSocialNetwork(@Path("idSocialNetwork") String idSocialNetwork);

}
