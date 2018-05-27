package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListAdapter;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMyConferencesActivity extends AppCompatActivity {

    UserService userService;
    private static List<Conference> conferencesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_conferences);

        userService = ApiUtils.getUserService();

        LoadMyConferences();
    }

    private void LoadMyConferences(){
        Call<List<Conference>> call = userService.getMyConferences(LoginActivity.username);
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    conferencesList = response.body();

                    ConferenceListAdapter adapter = new ConferenceListAdapter(getApplicationContext(), conferencesList);

                    final ListView lv = findViewById(R.id.listView);
                    lv.setAdapter(adapter);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent myIntent = new Intent(ShowMyConferencesActivity.this, ShowEventsOfConferenceActivity.class);
                            myIntent.putExtra("idConference", conferencesList.get(position).getId());
                            startActivity(myIntent);
                        }
                    });

                } else {
                    Toast.makeText(ShowMyConferencesActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(ShowMyConferencesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
