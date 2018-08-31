package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListAddSpeakerAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchSpeakersActivity extends BaseActivity{

    private ConferenceListAddSpeakerAdapter adapter;

    ListView list;
    SearchView search;
    UserService userService;

    Button create;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Speakers search");

        Intent myIntent = getIntent();
        final String idEvent = myIntent.getExtras().get("idEvent").toString();

        loadDrawer(R.layout.activity_search_speakers);

        list = findViewById(R.id.listview);
        search = findViewById(R.id.search);
        create = findViewById(R.id.btnRegister);
        next = findViewById(R.id.next);

        userService = ApiUtils.getUserService();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SearchSpeakersActivity.this, RegisterSpeakerActivity.class);
                myIntent.putExtra("idEvent", idEvent);
                startActivity(myIntent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SearchSpeakersActivity.this, SearchSpeakersActivity.class);
                myIntent.putExtra("idEvent", idEvent);
                myIntent.putExtra("comeFrom", "organizator");
                startActivity(myIntent);
            }
        });

        getSpeakers();
    }

    private void getSpeakers(){
        Intent myIntent = getIntent();
        final String id = myIntent.getExtras().get("idEvent").toString();

        Call<List<Actor>> call = userService.getSpeakersNotInEvent(id);
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {
                if(response.isSuccessful()){

                    List<Actor> aux = new ArrayList<>(response.body());

                    adapter = new ConferenceListAddSpeakerAdapter(getApplicationContext(), response.body(), aux, id);
                    list.setAdapter(adapter);

                    search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {

                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            adapter.filter(newText);
                            return false;
                        }
                    });




                } else {
                    Toast.makeText(SearchSpeakersActivity.this, "There are no speakers in the system", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(SearchSpeakersActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
