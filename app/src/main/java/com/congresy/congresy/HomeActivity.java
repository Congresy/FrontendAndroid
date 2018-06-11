package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private UserService userService;

    public static String role;
    public static Actor actor_;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // test drawer icon

        //TODO -----------

        setContentView(R.layout.activity_home);

        userService = ApiUtils.getUserService();

        // menu
        mDrawerList = findViewById(R.id.navList);

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

                    List<String> osArray = new ArrayList<>();
                    osArray.add("Profile");
                    osArray.add("Create conference");
                    osArray.add("My conferences");
                    osArray.add("All conferences");

                    if (actor.getRole().equals("Organizator")) {
                        osArray.remove("All conferences");
                    }

                    mAdapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_list_item_1, osArray);
                    mDrawerList.setAdapter(mAdapter);

                    mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        }
                    });

                    mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(position == 0){
                                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                                startActivity(intent);
                            } else if(position == 1){
                                Intent intent = new Intent(HomeActivity.this, CreateConferenceActivity.class);
                                startActivity(intent);
                            } else if(position == 2){
                                Intent intent = new Intent(HomeActivity.this, ShowMyConferencesActivity.class);
                                intent.putExtra("role", actor.getRole());
                                startActivity(intent);
                            } else if(position == 3){
                                Intent intent = new Intent(HomeActivity.this, ShowAllConferencesActivity.class);
                                startActivity(intent);
                            }
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
