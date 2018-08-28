package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListOrganizatorAdapter;
import com.congresy.congresy.adapters.ConferenceListUserAdapter;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    private UserService userService;
    private String role;

    Button myComments;
    Button pastConferences;
    ListView lv;

    ConferenceListOrganizatorAdapter adapter;
    ConferenceListUserAdapter adapter1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("My conferences");

        loadDrawer(R.layout.activity_home);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        role = sp.getString("Role", "not found");

        userService = ApiUtils.getUserService();

        myComments = findViewById(R.id.myComments);
        lv = findViewById(R.id.listView);
        pastConferences = findViewById(R.id.pastConferences);

        myComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ShowMyCommentsActivity.class);
                intent.putExtra("parent", "conference");
                startActivity(intent);
            }
        });

        Intent myIntent = getIntent();
        String past;

        try {
            past = myIntent.getExtras().get("past").toString();
        } catch (Exception e){
            past = "not found";
        }

        if (past.equals("past")){
            pastConferences.setVisibility(View.GONE);
            loadMyConferencesPast();
        } else {
            loadMyConferences();
        }

        pastConferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                intent.putExtra("past", "past");
                startActivity(intent);
            }
        });

    }

    private void loadMyConferencesPast(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String idActor = sp.getString("Id", "not found");

        Call<List<Conference>> call = userService.getMyConferences(idActor, "past");
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    List<Conference> conferencesList = response.body();

                    if (response.body().isEmpty()){
                        Toast.makeText(HomeActivity.this, "There are no past conferences!", Toast.LENGTH_SHORT).show();
                    }

                    if(role.equals("Organizator")) {
                        adapter = new ConferenceListOrganizatorAdapter(getApplicationContext(), conferencesList);
                        lv.setAdapter(adapter);
                    } else {
                        adapter1 = new ConferenceListUserAdapter(getApplicationContext(), conferencesList);
                        lv.setAdapter(adapter1);
                    }

                } else {
                    Toast.makeText(HomeActivity.this, "There are no past conferences!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "There are no past conferences!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadMyConferences(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String idActor = sp.getString("Id", "not found");

        Call<List<Conference>> call = userService.getMyConferences(idActor, "upcoming");
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    List<Conference> conferencesList = response.body();

                    if (response.body().isEmpty()){
                        Toast.makeText(HomeActivity.this, "There are no upcoming conferences!", Toast.LENGTH_SHORT).show();
                    }

                    if(role.equals("Organizator")) {
                        adapter = new ConferenceListOrganizatorAdapter(getApplicationContext(), conferencesList);
                        lv.setAdapter(adapter);
                    } else {
                        adapter1 = new ConferenceListUserAdapter(getApplicationContext(), conferencesList);
                        lv.setAdapter(adapter1);
                    }

                } else {
                    Toast.makeText(HomeActivity.this, "There are no upcoming conferences!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "There are no upcoming conferences!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
