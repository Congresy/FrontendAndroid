package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.domain.SocialNetwork;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {

    UserService userService;

    TextView tName;
    TextView tSurname;
    TextView tEmail;
    TextView tPhone;
    TextView tNick;
    TextView tRole;
    TextView socialNetworks;
    ImageView image;

    TextView ePlace;
    TextView eAddress;
    TextView eDetails;

    Button btnEdit;
    Button follow;

    private String username;

    List<SocialNetwork> socialNetworkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_profile);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        username = sp.getString("Username", "not found");

        tName = findViewById(R.id.name);
        tSurname = findViewById(R.id.surname);
        tEmail = findViewById(R.id.email);
        tPhone = findViewById(R.id.phone);
        tNick = findViewById(R.id.nick);
        tRole = findViewById(R.id.role);
        image = findViewById(R.id.image);
        socialNetworks = findViewById(R.id.socialNetworks);
        btnEdit = findViewById(R.id.btnEdit);
        follow = findViewById(R.id.follow);

        ePlace = findViewById(R.id.edtPlace);
        eAddress = findViewById(R.id.edtAddress);
        eDetails = findViewById(R.id.edtDetailsT);

        userService = ApiUtils.getUserService();

        final Intent myIntent = getIntent();

        btnEdit.setVisibility(View.GONE);
        follow.setVisibility(View.GONE);

        try {
            if (myIntent.getExtras().get("idOrganizator").toString() != null){

                String aux = sp.getString("followed " + myIntent.getExtras().get("idOrganizator").toString(), "not found");

                if (!aux.equals("not found")) {
                    follow.setVisibility(View.GONE);
                } else {
                    follow.setVisibility(View.VISIBLE);

                    follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("followed " + myIntent.getExtras().get("idOrganizator").toString(), "1");
                            editor.apply();

                            follow(myIntent.getExtras().get("idOrganizator").toString());
                        }
                    });
                }

                executeRest(myIntent.getExtras().get("idOrganizator").toString());

            } else if (myIntent.getExtras().get("idSpeaker").toString() != null){

                String aux = sp.getString("followed " + myIntent.getExtras().get("idSpeaker").toString(), "not found");

                if (!aux.equals("not found")) {
                    follow.setVisibility(View.GONE);
                } else {
                    follow.setVisibility(View.VISIBLE);

                    follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("followed " + myIntent.getExtras().get("idSpeaker").toString(), "1");
                            editor.apply();

                            follow(myIntent.getExtras().get("idSpeaker").toString());
                        }
                    });
                }

                executeRest(myIntent.getExtras().get("idSpeaker").toString());
            }
        } catch (Exception e){
            btnEdit.setVisibility(View.VISIBLE);
            execute();
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void follow(String idActorToFollow){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String idActor = sp.getString("Id", "not found");

        Call<Actor> call = userService.follow(idActor, idActorToFollow, "follow");
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                Intent intent = new Intent(ProfileActivity.this, FollowingActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPlace(String idPlace){
        Call<Place> call = userService.getPlace(idPlace);
        call.enqueue(new Callback<Place>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {

                Place p = response.body();

                ePlace.setText(p.getTown() + ", " + p.getCountry());
                eAddress.setText(p.getAddress() + ", " + p.getPostalCode());
                eAddress.setText("Details: " + p.getDetails());

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void LoadProfile(final List<SocialNetwork> socialNetworksS) {
        Call<Actor> call = userService.getActorByUsername(username);
        call.enqueue(new Callback<Actor>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if (response.isSuccessful()) {

                    Actor body = response.body();

                    tName.setText("Name: "  + body.getName());
                    tSurname.setText("Surname: " + body.getSurname());
                    tEmail.setText("Email: " + body.getEmail());
                    tPhone.setText("Phone: " + body.getPhone());
                    tNick.setText("Nick: " + body.getNick());
                    tRole.setText("Role: " + body.getRole());

                    if(socialNetworksS != null){
                        String auxSN = "";
                        int index = 0;

                        for(SocialNetwork sn : socialNetworksS){
                            if (index != 0) {
                                auxSN = auxSN + "\n";
                            }
                            auxSN = auxSN + sn.getName() + ": " + sn.getUrl();
                            index++;
                        }

                        socialNetworks.setText(auxSN);
                    } else {
                        socialNetworks.setText("");
                    }

                    if(body.getPhoto() != null){
                        chargeImage(body.getPhoto());
                    }

                    loadPlace(body.getPlace());

                } else {
                    Toast.makeText(ProfileActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chargeImage(String url){
        Glide.with(getApplicationContext())
                .load(url) // Image URL
                .centerCrop() // Image scale type
                .crossFade()
                .override(800,500) // Resize image
                .into(image); // ImageView to display image
    }
    
    
    // Charge social networks

    private void execute(){
        Call<Actor> call = userService.getActorByUsername(username);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {

                final Actor actor = response.body();
                LoadData(actor);

            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadData(Actor actor){
        Call<List<SocialNetwork>> call = userService.getSocialNetworksByActor(actor.getId());
        call.enqueue(new Callback<List<SocialNetwork>>() {
            @Override
            public void onResponse(Call<List<SocialNetwork>> call, Response<List<SocialNetwork>> response) {

                socialNetworkList = response.body();
                LoadProfile(socialNetworkList);

            }

            @Override
            public void onFailure(Call<List<SocialNetwork>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void executeRest(String id){
        Call<Actor> call = userService.getActorById(id);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {

                final Actor actor = response.body();

                LoadDataRest(actor);

            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadDataRest(final Actor actor){
        Call<List<SocialNetwork>> call = userService.getSocialNetworksByActor(actor.getId());
        call.enqueue(new Callback<List<SocialNetwork>>() {
            @Override
            public void onResponse(Call<List<SocialNetwork>> call, Response<List<SocialNetwork>> response) {

                socialNetworkList = response.body();
                LoadProfileRest(socialNetworkList, actor.getId());

            }

            @Override
            public void onFailure(Call<List<SocialNetwork>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void LoadProfileRest(final List<SocialNetwork> socialNetworksS, String id) {
        Call<Actor> call = userService.getActorById(id);
        call.enqueue(new Callback<Actor>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if (response.isSuccessful()) {

                    Actor body = response.body();

                    if (body.getPrivate()){
                        tName.setText("This profile is private");
                        TextView places = findViewById(R.id.text3);
                        TextView sN = findViewById(R.id.text2);

                        places.setText("");
                        sN.setText("");

                    } else {
                        tName.setText("Name: "  + body.getName());
                        tSurname.setText("Surname: " + body.getSurname());
                        tEmail.setText("Email: " + body.getEmail());
                        tPhone.setText("Phone: " + body.getPhone());
                        tNick.setText("Nick: " + body.getNick());
                        tRole.setText("Role: " + body.getRole());

                        if(socialNetworksS != null){
                            String auxSN = "";
                            int index = 0;

                            for(SocialNetwork sn : socialNetworksS){
                                if (index != 0) {
                                    auxSN = auxSN + "\n";
                                }
                                auxSN = auxSN + sn.getName() + ": " + sn.getUrl();
                                index++;
                            }

                            socialNetworks.setText(auxSN);
                        } else {
                            socialNetworks.setText("");
                        }

                        if(body.getPhoto() != null){
                            chargeImage(body.getPhoto());
                        }

                        loadPlace(body.getPlace());
                    }

                } else {
                    Toast.makeText(ProfileActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
