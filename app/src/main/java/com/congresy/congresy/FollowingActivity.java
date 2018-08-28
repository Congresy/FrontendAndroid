package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListAllAdapter;
import com.congresy.congresy.adapters.FollowingListAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingActivity extends BaseActivity{

    UserService userService;

    ListView lv1;
    ListView lv2;

    private String actorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Following");

        loadDrawer(R.layout.activity_following);

        lv1 = findViewById(R.id.organizators);
        lv2 = findViewById(R.id.speakers);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        actorId = sp.getString("Id", "not found");

        userService = ApiUtils.getUserService();

        loadAllFollowingActors();
    }

    private void loadAllFollowingActors(){
        Call<List<Actor>> call = userService.getFollowing(actorId);
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {

                final List<Actor> followingActors = response.body();

                final List<Actor> organizators = new ArrayList<>();
                final List<Actor> speakers = new ArrayList<>();

                for (Actor a : followingActors){
                    if (a.getRole().equals("Organizator")){
                        organizators.add(a);
                    } else if (a.getRole().equals("Speaker")){
                        speakers.add(a);
                    }
                }

                FollowingListAdapter adapter1 = new FollowingListAdapter(getApplicationContext(), organizators);
                FollowingListAdapter adapter2 = new FollowingListAdapter(getApplicationContext(), speakers);

                lv1.setAdapter(adapter1);
                lv2.setAdapter(adapter2);

                lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(FollowingActivity.this, ProfileActivity.class);
                        intent.putExtra("goingTo", "Organizator");
                        intent.putExtra("idOrganizator", organizators.get(position).getId());
                        startActivity(intent);
                    }
                });

                lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(FollowingActivity.this, ProfileActivity.class);
                        intent.putExtra("goingTo", "Speaker");
                        intent.putExtra("idSpeaker", speakers.get(position).getId());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(FollowingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
