package com.congresy.congresy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.congresy.congresy.domain.Announcement;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAnnouncementActivity extends BaseActivity {

    UserService userService = ApiUtils.getUserService();

    ImageView image;

    TextView conferenceE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_show_announcement);

        image = findViewById(R.id.image);
        conferenceE = findViewById(R.id.conference);

        showAnnouncement();

    }

    private void showAnnouncement(){
        Intent intent = getIntent();
        String id = intent.getExtras().get("idAnnouncement").toString();

        Call<Announcement> call = userService.getAnnouncement(id);
        call.enqueue(new Callback<Announcement>() {
            @Override
            public void onResponse(Call<Announcement> call, Response<Announcement> response) {

                Announcement announcement = response.body();

                Glide.with(getApplicationContext())
                            .load(announcement.getPicture()) // Image URL
                            .centerCrop() // Image scale type
                            .crossFade()
                            .override(800,500)
                            .into(image); // ImageView to display image

                conferenceE.setText(announcement.getUrl());

            }

            @Override
            public void onFailure(Call<Announcement> call, Throwable t) {
                Toast.makeText(ShowAnnouncementActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* private void loadConference(String id){
        Call<Conference> call = userService.getConference(id);
        call.enqueue(new Callback<Conference>() {
            @Override
            public void onResponse(Call<Conference> call, Response<Conference> response) {

                Conference conference = response.body();

                conferenceE.setText(conference.getName());

            }

            @Override
            public void onFailure(Call<Conference> call, Throwable t) {
                Toast.makeText(ShowAnnouncementActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    } */
}
