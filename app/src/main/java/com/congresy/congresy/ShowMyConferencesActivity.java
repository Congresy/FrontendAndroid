package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ShowMyConferencesActivity extends BaseActivity {

    UserService userService;
    private static List<Conference> conferencesList;

    private String idActor;
    private String role;

    Button myComments;
    Button pastConferences;
    ListView lv;

    ConferenceListOrganizatorAdapter adapter1;
    ConferenceListUserAdapter adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My conferences");
        loadDrawer(R.layout.activity_show_my_conferences);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        idActor = sp.getString("Id", "not found");
        role = sp.getString("Role", "not found");

        myComments = findViewById(R.id.myComments);
        lv = findViewById(R.id.listView);
        pastConferences = findViewById(R.id.pastConferences);

        userService = ApiUtils.getUserService();

        myComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowMyConferencesActivity.this, ShowMyCommentsActivity.class);
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
                Intent intent = new Intent(ShowMyConferencesActivity.this, ShowMyConferencesActivity.class);
                intent.putExtra("past", "past");
                startActivity(intent);
            }
        });
    }

    private void loadMyConferences(){
        Call<List<Conference>> call = userService.getMyConferences(idActor, "upcoming");
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, final Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    conferencesList = response.body();

                    if (response.body().isEmpty()){
                        Toast.makeText(ShowMyConferencesActivity.this, "There are no past conferences!", Toast.LENGTH_SHORT).show();
                    }

                    if(role.equals("Organizator")) {
                        adapter1 = new ConferenceListOrganizatorAdapter(getApplicationContext(), response.body());
                        lv.setAdapter(adapter1);
                    } else {
                        adapter2 = new ConferenceListUserAdapter(getApplicationContext(), response.body());
                        lv.setAdapter(adapter2);
                    }

                } else {
                    Toast.makeText(ShowMyConferencesActivity.this, "There are no upcoming conferences!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(ShowMyConferencesActivity.this, "There are no upcoming conferences!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMyConferencesPast(){
        Call<List<Conference>> call = userService.getMyConferences(idActor, "past");
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, final Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    conferencesList = response.body();

                    if (response.body().isEmpty()){
                        Toast.makeText(ShowMyConferencesActivity.this, "There are no past conferences!", Toast.LENGTH_SHORT).show();
                    }

                    if(role.equals("Organizator")) {
                        adapter1 = new ConferenceListOrganizatorAdapter(getApplicationContext(), response.body());
                        lv.setAdapter(adapter1);
                    } else {
                        adapter2 = new ConferenceListUserAdapter(getApplicationContext(), response.body());
                        lv.setAdapter(adapter2);
                    }

                } else {
                    Toast.makeText(ShowMyConferencesActivity.this, "There are no past conferences!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(ShowMyConferencesActivity.this, "There are no past conferences!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
