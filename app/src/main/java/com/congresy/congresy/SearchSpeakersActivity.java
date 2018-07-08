package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.congresy.congresy.adapters.ConferenceListAddSpeakerAdapter;
import com.congresy.congresy.adapters.EventListOrganizatorAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

public class SearchSpeakersActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private final static List<Actor> speakers = ShowSpeakersOfEventActivity.speakers;
    private ConferenceListAddSpeakerAdapter adapter;

    ListView list;
    SearchView search;
    UserService userService;

    Button create;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent();
        final String idEvent = myIntent.getExtras().get("idEvent").toString();

        setContentView(R.layout.activity_search_speakers);

        list = findViewById(R.id.listview);
        search = findViewById(R.id.search);
        create = findViewById(R.id.btnRegister);
        next = findViewById(R.id.next);

        userService = ApiUtils.getUserService();

        List<Actor> aux = new ArrayList<>(speakers);

        adapter = new ConferenceListAddSpeakerAdapter(getApplicationContext(), speakers, aux, idEvent);
        list.setAdapter(adapter);

        search.setOnQueryTextListener(this);

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
                Intent myIntent = new Intent(SearchSpeakersActivity.this, ShowSpeakersOfEventActivity.class);
                myIntent.putExtra("idEvent", idEvent);
                startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }
}
