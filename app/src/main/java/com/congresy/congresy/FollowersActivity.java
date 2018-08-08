package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class FollowersActivity extends BaseActivity {

    UserService userService;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_followers);

        lv = findViewById(R.id.listView);

        userService = ApiUtils.getUserService();

        Intent intent = getIntent();
        String idActor = intent.getExtras().getString("idActor");

        if (idActor != null){
            loadFollowersAll(idActor);
        } else {
            loadFollowers();
        }
    }

    private void loadFollowers(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String actorId = sp.getString("Id", "not found");

        Call<List<Actor>> call = userService.getFollowing(actorId);
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {

                final List<Actor> followers = response.body();

                ArrayAdapter adapter = new ArrayAdapter<>(FollowersActivity.this, android.R.layout.simple_list_item_1, followers);

                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(FollowersActivity.this, ProfileActivity.class);
                        intent.putExtra("goingTo", "Unknown");
                        intent.putExtra("idAuthor", followers.get(position).getId());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(FollowersActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFollowersAll(String actorId){
        Call<List<Actor>> call = userService.getFollowing(actorId);
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {

                final List<Actor> followers = response.body();

                ArrayAdapter adapter = new ArrayAdapter<>(FollowersActivity.this, android.R.layout.simple_list_item_1, followers);

                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(FollowersActivity.this, ProfileActivity.class);
                        intent.putExtra("goingTo", "Unknown");
                        intent.putExtra("idAuthor", followers.get(position).getId());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(FollowersActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
