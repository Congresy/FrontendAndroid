package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.FriendsListAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMyFriendsActivity extends BaseActivity {

    UserService userService;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My friends");
        loadDrawer(R.layout.activity_show_my_friends);

        lv = findViewById(R.id.friends);

        userService = ApiUtils.getUserService();

        loadFriends();
    }

    private void loadFriends(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String idActor = sp.getString("Id", "not found");

        Call<List<Actor>> call = userService.getFriends(idActor);
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {

                final List<Actor> friends = response.body();

                FriendsListAdapter adapter = new FriendsListAdapter(getApplicationContext(), friends);

                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ShowMyFriendsActivity.this, ProfileActivity.class);
                        intent.putExtra("goingTo", "Unknown");
                        intent.putExtra("idAuthor", friends.get(position).getId());
                        startActivity(intent);
                    }
                });


            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(ShowMyFriendsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
