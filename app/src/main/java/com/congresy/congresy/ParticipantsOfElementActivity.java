package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class ParticipantsOfElementActivity extends BaseActivity {

    UserService userService;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getStringExtra("comeFrom").equals("conference")) {
            setTitle("Conference participants");
            loadDrawer(R.layout.activity_participants_of_conference);
        } else if (getIntent().getStringExtra("comeFrom").equals("event")) {
            setTitle("Event participants");
            loadDrawer(R.layout.activity_participants_of_event);
        }

        userService = ApiUtils.getUserService();
        
        lv = findViewById(R.id.listView);

        if (getIntent().getStringExtra("comeFrom").equals("conference")) {
            loadConferences(getIntent().getStringExtra("idConference"));
        } else if (getIntent().getStringExtra("comeFrom").equals("event")) {
            loadEvents(getIntent().getStringExtra("idEvent"));
        }
    }

    private void loadEvents(String idEvent){
        Call<List<Actor>> call = userService.getParticipantsEvent(idEvent);
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, final Response<List<Actor>> response) {
                if(response.isSuccessful()){
                    
                    ArrayAdapter<Actor> itemsAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, response.body());
                    
                    lv.setAdapter(itemsAdapter);
                    
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                            myIntent.putExtra("goingTo", "Unknown");
                            myIntent.putExtra("idAuthor", response.body().get(position).getId());
                            getApplication().startActivity(myIntent);
                        }
                    });
                    
                } else {
                    Toast.makeText(ParticipantsOfElementActivity.this, "This conference have no participants!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(ParticipantsOfElementActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadConferences(String idConference){
        Call<List<Actor>> call = userService.getParticipantsConference(idConference);
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, final Response<List<Actor>> response) {
                if(response.isSuccessful()){

                    ArrayAdapter<Actor> itemsAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, response.body());

                    lv.setAdapter(itemsAdapter);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                            myIntent.putExtra("goingTo", "Unknown");
                            myIntent.putExtra("idAuthor", response.body().get(position).getId());
                            getApplication().startActivity(myIntent);
                        }
                    });

                } else {
                    Toast.makeText(ParticipantsOfElementActivity.this, "This conference have no participants!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(ParticipantsOfElementActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
