package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowEventsOfConferenceActivity extends AppCompatActivity {


    UserService userService;
    private static List<Event> eventsList;

    Button btnEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events_of_conference);

        userService = ApiUtils.getUserService();

        btnEvents = findViewById(R.id.btnCreateEvent);

        btnEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = getIntent();
                String idConference = myIntent.getExtras().get("idConference").toString();

                Intent intent = new Intent(ShowEventsOfConferenceActivity.this, CreateEventActivity.class);
                intent.putExtra("idConference", idConference);
                startActivity(intent);
            }
        });

        LoadEvents();
    }

    private void LoadEvents(){
        Intent myIntent = getIntent();
        String idConference = myIntent.getExtras().get("idConference").toString();

        Call<List<Event>> call = userService.getConferenceEvents(idConference);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if(response.isSuccessful()){

                    eventsList = response.body();

                    ArrayAdapter<Event> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, eventsList);
                    ListView lv = findViewById(R.id.listView);
                    lv.setAdapter(adapter);

                } else {
                    Toast.makeText(ShowEventsOfConferenceActivity.this, "This conference have no events!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(ShowEventsOfConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
