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

import com.congresy.congresy.adapters.EventListOrganizatorAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowEventsOfConferenceAuxActivity extends BaseActivity {

    UserService userService;
    private static List<Event> eventsList;

    Button btnEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_show_events_of_conference);

        userService = ApiUtils.getUserService();

        btnEvents = findViewById(R.id.btnCreateEvent);
        btnEvents.setVisibility(View.GONE);

        loadEventsUser();
    }

    private void loadEventsUser(){
        Call<Actor> call = userService.getActorByUsername(HomeActivity.username);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if(response.isSuccessful()){

                    final Actor actor = response.body();


                    loadData(actor.getId());

                } else {
                    Toast.makeText(ShowEventsOfConferenceAuxActivity.this, "You have no social networks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(ShowEventsOfConferenceAuxActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData(String idActor){
        Intent myIntent = getIntent();
        String idConference = myIntent.getExtras().get("idConference").toString();

        Call<List<Event>> call = userService.getConferenceEventsUser(idConference, idActor);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if(response.isSuccessful()){

                    if(HomeActivity.role.equals("Organizator")){
                        btnEvents.setVisibility(View.VISIBLE);
                    }

                    String role = HomeActivity.role;
                    EventListOrganizatorAdapter adapter = null;
                    ArrayAdapter<Event> adapter1 = null;
                    eventsList = response.body();

                    if(role.equals("Organizator")) {
                        adapter = new EventListOrganizatorAdapter(getApplicationContext(), eventsList);
                    } else {
                        adapter1 = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, eventsList);
                    }

                    final ListView lv = findViewById(R.id.listView);

                    if(role.equals("Organizator")) {
                        lv.setAdapter(adapter);
                    } else {
                        lv.setAdapter(adapter1);
                    }

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ShowEventsOfConferenceAuxActivity.this, ShowEventActivity.class);
                            intent.putExtra("idEvent", eventsList.get(position).getId());
                            startActivity(intent);
                        }
                    });

                    btnEvents.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent myIntent = getIntent();
                            String idConference = myIntent.getExtras().get("idConference").toString();

                            Intent intent = new Intent(ShowEventsOfConferenceAuxActivity.this, CreateEventActivity.class);
                            intent.putExtra("idConference", idConference);
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(ShowEventsOfConferenceAuxActivity.this, "This conference have no events!", Toast.LENGTH_SHORT).show();
                    if(HomeActivity.role.equals("Organizator")) {
                        btnEvents.setVisibility(View.VISIBLE);

                        btnEvents.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent myIntent = getIntent();
                                String idConference = myIntent.getExtras().get("idConference").toString();

                                Intent intent = new Intent(ShowEventsOfConferenceAuxActivity.this, CreateEventActivity.class);
                                intent.putExtra("idConference", idConference);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(ShowEventsOfConferenceAuxActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
}
