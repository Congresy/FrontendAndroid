package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

public class SocialActivity extends BaseActivity {

    UserService userService;

    Button following;
    Button followed;
    Button friends;
    Button socialNetworks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_social);

        userService = ApiUtils.getUserService();

        following = findViewById(R.id.following);
        followed = findViewById(R.id.followers);
        friends = findViewById(R.id.friends);
        socialNetworks = findViewById(R.id.socialNetworks);

        following.setVisibility(View.GONE);
        followed.setVisibility(View.GONE);
        friends.setVisibility(View.GONE);
        socialNetworks.setVisibility(View.GONE);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String role = sp.getString("Role", "not found");

        switch (role) {
            case "User":
                friends.setVisibility(View.VISIBLE);
                following.setVisibility(View.VISIBLE);
                socialNetworks.setVisibility(View.VISIBLE);
                break;
            case "Speaker":
                friends.setVisibility(View.VISIBLE);
                following.setVisibility(View.VISIBLE);
                followed.setVisibility(View.VISIBLE);
                socialNetworks.setVisibility(View.VISIBLE);
                break;
            case "Organizator":
                followed.setVisibility(View.VISIBLE);
                socialNetworks.setVisibility(View.VISIBLE);
                break;
        }

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SocialActivity.this, FollowingActivity.class);
                startActivity(intent);
            }
        });

        followed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SocialActivity.this, FollowersActivity.class);
                startActivity(intent);
            }
        });

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SocialActivity.this, ShowMyFriendsActivity.class);
                startActivity(intent);
            }
        });

        socialNetworks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SocialActivity.this, ShowMySocialNetworksActivity.class);
                startActivity(intent);
            }
        });

    }
}
