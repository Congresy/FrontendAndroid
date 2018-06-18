package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.domain.SocialNetwork;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowSocialNetworkActivity extends BaseActivity {

    private UserService userService;

    TextView edtName;
    TextView edtUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_show_social_network);

        Intent myIntent = getIntent();
        String idSocialNetwork = myIntent.getExtras().get("idSocialNetwork").toString();

        userService = ApiUtils.getUserService();

        edtName = findViewById(R.id.edtName);
        edtUrl = findViewById(R.id.edtUrl);

        showSocialNetwork(idSocialNetwork);

    }

    @SuppressLint("SetTextI18n")
    private void showSocialNetwork(String idSocialNetwork){
        Call<SocialNetwork> call = userService.getSocialNetwork(idSocialNetwork);
        call.enqueue(new Callback<SocialNetwork>() {
            @Override
            public void onResponse(Call<SocialNetwork> call, Response<SocialNetwork> response) {

                SocialNetwork socialNetwork = response.body();

                edtName.setText("Name: " + socialNetwork.getName());
                edtUrl.setText("URL: " + socialNetwork.getUrl());

            }

            @Override
            public void onFailure(Call<SocialNetwork> call, Throwable t) {
                Toast.makeText(ShowSocialNetworkActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
