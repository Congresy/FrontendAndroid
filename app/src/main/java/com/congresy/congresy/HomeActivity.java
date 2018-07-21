package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListOrganizatorAdapter;
import com.congresy.congresy.adapters.ConferenceListUserAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    public static String username;
    public static String role;
    public static Actor actor_;
    public static List<Post> posts_;

    private UserService userService;

    Button myComments;

    private static List<Conference> conferencesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        execute();

        loadDrawer(R.layout.activity_home);

        userService = ApiUtils.getUserService();

        myComments = findViewById(R.id.myComments);

        loadAllPosts();

        myComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ShowMyCommentsActivity.class);
                intent.putExtra("parent", "conference");
                startActivity(intent);
            }
        });

    }
    
    private void LoadMyConferences(){
        Call<List<Conference>> call = userService.getMyConferences(username);
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    ConferenceListOrganizatorAdapter adapter = null;
                    ConferenceListUserAdapter adapter1 = null;
                    conferencesList = response.body();

                    if(role.equals("Organizator")) {
                        adapter = new ConferenceListOrganizatorAdapter(getApplicationContext(), conferencesList);
                    } else {
                        adapter1 = new ConferenceListUserAdapter(getApplicationContext(), conferencesList);
                    }

                    final ListView lv = findViewById(R.id.listView);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(HomeActivity.this, ShowConferenceActivity.class);
                            intent.putExtra("idConference", conferencesList.get(position).getId());
                            startActivity(intent);
                        }
                    });

                    if(role.equals("Organizator")) {
                        lv.setAdapter(adapter);
                    } else {
                        lv.setAdapter(adapter1);
                    }

                } else {
                    Toast.makeText(HomeActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void execute(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        username = sp.getString("Username", "Not found");

        Call<Actor> call = ApiUtils.getUserService().getActorByUsername(username);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {

                    Actor actor = response.body();
                    actor_ = actor;
                    role = actor.getRole();

                    LoadMyConferences();

            }
            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllPosts(){
        Call<List<Post>> call = userService.getAllPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.isSuccessful()){

                    List<Post> toRemove = new ArrayList<>();

                    posts_ = response.body();

                    for (Post p : posts_){
                        if (p.getDraft()){
                            toRemove.add(p);
                        }
                    }

                    posts_.removeAll(toRemove);

                } else {
                    Toast.makeText(HomeActivity.this, "There are no posts!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
