package com.congresy.congresy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListOrganizatorAdapter;
import com.congresy.congresy.adapters.EventListOrganizatorAdapter;
import com.congresy.congresy.adapters.SpeakersOfEventListAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowSpeakersOfEventActivity extends BaseActivity {

    UserService userService;

    Button btnAddSpeakers;

    ListView listView;

    public static String event_;

    public static List<Actor> speakers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_show_speakers_of_event);

        userService = ApiUtils.getUserService();

        btnAddSpeakers = findViewById(R.id.addSpeakers);
        listView = findViewById(R.id.listView);

        btnAddSpeakers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpeakers();
            }
        });

        loadSpeakers();
    }


    private void loadSpeakers(){
        Intent myIntent = getIntent();
        final String id = myIntent.getExtras().get("idEvent").toString();

        event_ = id;

        Call<List<Actor>> call = userService.getSpeakersOfEvent(id);
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {
                if(response.isSuccessful()){

                    List<Actor> speakers = response.body();

                    ListView lv = findViewById(R.id.listView);

                    SpeakersOfEventListAdapter adapter = new SpeakersOfEventListAdapter(getApplicationContext(), speakers);

                    lv.setAdapter(adapter);

                    //TODO show details of speakers


                } else {
                    Toast.makeText(ShowSpeakersOfEventActivity.this, "This event has no speakers!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(ShowSpeakersOfEventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSpeakers(){
        Intent myIntent = getIntent();
        final String id = myIntent.getExtras().get("idEvent").toString();


        Call<List<Actor>> call = userService.getSpeakers(id);
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {
                if(response.isSuccessful()){

                    speakers = response.body();

                    Intent myIntent = getIntent();
                    String id = myIntent.getExtras().get("idEvent").toString();

                    Intent intent = new Intent(ShowSpeakersOfEventActivity.this, SearchSpeakersActivity.class);
                    intent.putExtra("idEvent", id);
                    startActivity(intent);

                } else {
                    Toast.makeText(ShowSpeakersOfEventActivity.this, "There are no speakers in the system", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(ShowSpeakersOfEventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
