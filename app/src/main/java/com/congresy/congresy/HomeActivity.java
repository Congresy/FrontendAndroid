package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private UserService userService;

    public static String role;
    public static Actor actor_;

    Button profileButton;
    Button createConferencesButton;
    Button listConferencesButton;
    Button listAllConferencesButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        userService = ApiUtils.getUserService();


        profileButton = findViewById(R.id.profileButton);
        createConferencesButton = findViewById(R.id.createConferencesButton);
        listConferencesButton = findViewById(R.id.listConferencesButton);
        listAllConferencesButton = findViewById(R.id.listAllConferencesButton);

        createConferencesButton.setVisibility(View.GONE);
        profileButton.setVisibility(View.GONE);
        listConferencesButton.setVisibility(View.GONE);
        listAllConferencesButton.setVisibility(View.GONE);

        loadActor();

    }

    private void loadActor(){
        Call<Actor> call = userService.getActorByUsername(LoginActivity.username);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if(response.isSuccessful()){

                    final Actor actor = response.body();

                    actor_ = actor;
                    role = actor.getRole();

                    profileButton.setVisibility(View.VISIBLE);

                    if (actor.getRole().equals("Organizator")) {
                        createConferencesButton.setVisibility(View.VISIBLE);
                    } else {
                        listAllConferencesButton.setVisibility(View.VISIBLE);
                    }

                    listConferencesButton.setVisibility(View.VISIBLE);

                    profileButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        }
                    });

                    createConferencesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(HomeActivity.this, CreateConferenceActivity.class);
                            startActivity(intent);
                        }
                    });

                    listConferencesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(HomeActivity.this, ShowMyConferencesActivity.class);
                            intent.putExtra("role", actor.getRole());
                            startActivity(intent);
                        }
                    });

                    listAllConferencesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(HomeActivity.this, ShowAllConferencesActivity.class);
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(HomeActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
