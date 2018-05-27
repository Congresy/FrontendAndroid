package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListAllAdapter;
import com.congresy.congresy.adapters.ConferenceListOrganizatorAdapter;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowConferencesActivity extends AppCompatActivity {

    UserService userService;
    private static List<Conference> conferencesList;
    private static List<Conference> conferencesListAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent();
        String role = myIntent.getExtras().get("role").toString();

        setContentView(R.layout.activity_show_my_conferences);

        userService = ApiUtils.getUserService();

        if(!role.equals("Organizator")){
            myIntent.putExtra("role", role);
            LoadAllConferences();
        } else {
            LoadMyConferences();
        }
    }

    private void LoadMyConferences(){
        Call<List<Conference>> call = userService.getMyConferences(LoginActivity.username);
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    conferencesList = response.body();

                    ConferenceListOrganizatorAdapter adapter = new ConferenceListOrganizatorAdapter(getApplicationContext(), conferencesList);

                    final ListView lv = findViewById(R.id.listView);
                    lv.setAdapter(adapter);

                } else {
                    Toast.makeText(ShowConferencesActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(ShowConferencesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadAllConferences(){
        Call<List<Conference>> call = userService.getAllConferencesDetailedOrderByDate();
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    conferencesListAll = response.body();

                    ConferenceListAllAdapter adapter = new ConferenceListAllAdapter(getApplicationContext(), conferencesListAll);

                    final ListView lv = findViewById(R.id.listView);
                    lv.setAdapter(adapter);

                } else {
                    Toast.makeText(ShowConferencesActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(ShowConferencesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
