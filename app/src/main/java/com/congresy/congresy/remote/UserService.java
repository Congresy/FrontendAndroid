package com.congresy.congresy.remote;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.domain.Post;
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
    Call<Actor> register(@Body JsonObject jsonObject);

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

    @GET("actors/{id}")
    Call<Actor> getActorById(@Path("id") String id);

    @GET("conferences/organizator/{username}")
    Call<List<Conference>> getMyConferences(@Path("username") String username);

    @GET("events/all/conferences/{idConference}")
    Call<List<Event>> getConferenceEventsAllAndOrganizator(@Path("idConference") String idConference);

    @PUT("events/add/{idEvent}/participants/{idActor}")
    Call<Event> addParticipantToEvent(@Path("idEvent") String idEvent, @Path("idActor") String idActor);

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

    @PUT("events/delete/{idEvent}/participants/{idActor}")
    Call<Event> deleteParticipant(@Path("idEvent") String idEvent, @Path("idActor") String idActor);

    @GET("actors/role/{role}")
    Call<List<Actor>> getAllActorsByRole(@Path("role") String role);

    @GET("actors/speakers/event/{idEvent}")
    Call<List<Actor>> getSpeakers(@Path("idEvent") String idEvent);

    @PUT("events/add/{idEvent}/speakers/{idSpeaker}")
    Call<Event> addSpeaker(@Path("idEvent") String idEvent, @Path("idSpeaker") String idActor);

    @PUT("events/delete/{idEvent}/speakers/{idSpeaker}")
    Call<Event> deleteSpeaker(@Path("idEvent") String idEvent, @Path("idSpeaker") String idActor);

    @GET("events/speakers/{idEvent}")
    Call<List<Actor>> getSpeakersOfEvent(@Path("idEvent") String idEvent);

    @GET("posts/votes")
    Call<List<Post>> getMostVoted();

    @GET("posts")
    Call<List<Post>> getAllPosts();

    @GET("posts/{idPost}")
    Call<Post> getPost(@Path("idPost") String idPost);

    @POST("posts")
    Call<Post> savePost(@Body JsonObject jsonObject);

    @PUT("posts/public/{idPost}")
    Call<Post> publicPost(@Path("idPost") String idPost);

}
