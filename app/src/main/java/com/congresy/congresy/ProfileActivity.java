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
import com.congresy.congresy.adapters.FriendsListAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.domain.SocialNetwork;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.text.MessageFormat;
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
    Button friend;
    Button followers;

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
        friend = findViewById(R.id.friend);
        followers = findViewById(R.id.followers);

        ePlace = findViewById(R.id.edtPlace);
        eAddress = findViewById(R.id.edtAddress);
        eDetails = findViewById(R.id.edtDetailsT);

        userService = ApiUtils.getUserService();

        final Intent myIntent = getIntent();

        btnEdit.setVisibility(View.GONE);
        follow.setVisibility(View.GONE);
        friend.setVisibility(View.GONE);
        followers.setVisibility(View.GONE);

        try {
            if (myIntent.getExtras().get("goingTo").equals("Organizator")){

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
                    aux(myIntent.getExtras().get("idOrganizator").toString());
                }
            } else if (myIntent.getExtras().get("goingTo").equals("Speaker")){

                if (myIntent.getExtras().get("idSpeaker").toString() != null){

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
                    aux(myIntent.getExtras().get("Speaker").toString());
                }
            }  else if (myIntent.getExtras().get("goingTo").equals("Unknown")){
                loadActor();
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

    private void aux(String idAuthor){
        Call<Actor> call = userService.getActorById(idAuthor);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {

                final Actor actor = response.body();

                try {
                    followers.setText(MessageFormat.format("Followers ({0})", actor.getFollowers().size()));

                    followers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, FollowersActivity.class);
                            intent.putExtra("idActor", actor.getId());
                            startActivity(intent);
                        }
                    });
                } catch (Exception e){
                    followers.setText("Followers (0)");
                }

                if (!actor.getRole().equals("User")){
                    followers.setVisibility(View.VISIBLE);
                } else {
                    followers.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadActor(){
        final SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        final String role = sp.getString("Role", "not found");
        final Intent myIntent = getIntent();

        Call<Actor> call = userService.getActorById(getIntent().getExtras().getString("idAuthor"));
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {

                final Actor actor = response.body();

                try {
                    followers.setText(MessageFormat.format("Followers ({0})", actor.getFollowers().size()));

                    followers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, FollowersActivity.class);
                            intent.putExtra("idActor", actor.getId());
                            startActivity(intent);
                        }
                    });
                } catch (Exception e){
                    followers.setText("Followers (0)");
                }

                followers.setVisibility(View.VISIBLE);

                if (actor.getRole().equals("User")){

                    String aux = sp.getString("friend " + myIntent.getExtras().get("idAuthor").toString(), "not found");

                    if (!aux.equals("not found")) {
                        friend.setVisibility(View.GONE);
                    } else {
                        if (role.equals("Organizator") || role.equals("Administrator")) {
                            friend.setVisibility(View.GONE);
                        } else {
                            friend.setVisibility(View.VISIBLE);
                        }

                        friend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("friend " + myIntent.getExtras().get("idAuthor").toString(), "1");
                                editor.apply();

                                friend(myIntent.getExtras().get("idAuthor").toString());
                            }
                        });
                    }

                    executeRest(myIntent.getExtras().get("idAuthor").toString());

                } else if (actor.getRole().equals("Organizator") || actor.getRole().equals("Administrator")) {

                    follow.setVisibility(View.GONE);
                    friend.setVisibility(View.GONE);

                    executeRest(myIntent.getExtras().get("idAuthor").toString());

                } else if (actor.getRole().equals("Speaker")){

                    String aux = sp.getString("followed " + myIntent.getExtras().get("idAuthor").toString(), "not found");

                    if (!aux.equals("not found")) {
                        follow.setVisibility(View.GONE);
                    } else {
                        follow.setVisibility(View.VISIBLE);

                        follow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("followed " + myIntent.getExtras().get("idAuthor").toString(), "1");
                                editor.apply();

                                follow(myIntent.getExtras().get("idAuthor").toString());
                            }
                        });
                    }

                    executeRest(myIntent.getExtras().get("idAuthor").toString());

                }

            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
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

    private void friend(String idActorToFriend){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String idActor = sp.getString("Id", "not found");

        Call<Actor> call = userService.friend(idActor, idActorToFriend, "friend");
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                Intent intent = new Intent(ProfileActivity.this, ShowMyFriendsActivity.class);
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

                        follow.setVisibility(View.GONE);

                    } else {
                        tName.setText("Name: "  + body.getName());
                        tSurname.setText("Surname: " + body.getSurname());
                        tEmail.setText("Email: " + body.getEmail());
                        tPhone.setText("Phone: " + body.getPhone());
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
