package com.congresy.congresy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListAllAdapter;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAllConferencesActivity extends BaseActivity {

    private ConferenceListAllAdapter adapter;

    UserService userService;

    SearchView search;

    Button filter;
    Button order;

    private String actorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent();

        try {
            myIntent.getExtras().get("comeFrom").toString();
            setTitle("Upcoming conferences");
        } catch (Exception e){
            setTitle("Conferences");
        }

        loadDrawer(R.layout.activity_show_all_conferences);

        search = findViewById(R.id.search);
        filter = findViewById(R.id.filter);
        order = findViewById(R.id.order);


        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        actorId = sp.getString("Id", "not found");

        userService = ApiUtils.getUserService();

        try {
            myIntent.getExtras().get("comeFrom").toString();
            loadUpcomingConferences();
            setTitle("Upcoming conferences");
        } catch (Exception e){
            try {
                String order = myIntent.getExtras().get("order").toString();
                loadAllConferences(order);
            } catch (Exception e1){
                loadAllConferences("date");
            }
            setTitle("Conferences");
        }

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ShowAllConferencesActivity.this);
                final LayoutInflater inflater = LayoutInflater.from(ShowAllConferencesActivity.this);
                final View view1 = inflater.inflate(R.layout.conference_filter, null);
                final ListView listView = view1.findViewById(R.id.listView);

                final String[] arraySpinner = new String[] {
                        "None", "Place", "Theme"
                };

                final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(ShowAllConferencesActivity.this, android.R.layout.simple_list_item_1, arraySpinner);
                listView.setAdapter(stringArrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (arraySpinner[position].equals("Theme")){

                            dialog.cancel();

                            final Dialog dialog1 = new Dialog(ShowAllConferencesActivity.this);
                            LayoutInflater inflater1 = LayoutInflater.from(ShowAllConferencesActivity.this);
                            View view11 = inflater1.inflate(R.layout.conference_filter, null);
                            ListView listView1 = view11.findViewById(R.id.listView);

                            final String[] arraySpinner1 = new String[] {
                                    "General", "Another", "New one"
                            };

                            final ArrayAdapter<String> stringArrayAdapter1 = new ArrayAdapter<>(ShowAllConferencesActivity.this, android.R.layout.simple_list_item_1, arraySpinner1);
                            listView1.setAdapter(stringArrayAdapter1);

                            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                                    search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                        @Override
                                        public boolean onQueryTextSubmit(String query) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onQueryTextChange(String newText) {
                                            adapter.filterTheme(newText, arraySpinner1[position]);
                                            return false;
                                        }
                                    });

                                    dialog1.cancel();
                                }
                            });

                            dialog1.setContentView(view11);
                            dialog1.show();

                        } else if (arraySpinner[position].equals("None")){

                            dialog.cancel();

                        } else if (arraySpinner[position].equals("Place")){

                            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    adapter.filterPlace(newText);
                                    return false;
                                }
                            });

                            dialog.cancel();

                        }

                    }
                });


                dialog.setContentView(view1);
                dialog.show();
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ShowAllConferencesActivity.this);
                final LayoutInflater inflater = LayoutInflater.from(ShowAllConferencesActivity.this);
                final View view1 = inflater.inflate(R.layout.conference_filter, null);
                final ListView listView = view1.findViewById(R.id.listView);

                final String[] arraySpinner = new String[] {
                        "Price", "Date"
                };

                final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(ShowAllConferencesActivity.this, android.R.layout.simple_list_item_1, arraySpinner);
                listView.setAdapter(stringArrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (arraySpinner[position].equals("Price")){

                            Intent intent = new Intent(ShowAllConferencesActivity.this, ShowAllConferencesActivity.class);
                            intent.putExtra("order", "price");
                            startActivity(intent);

                        } else if (arraySpinner[position].equals("Date")){

                            Intent intent = new Intent(ShowAllConferencesActivity.this, ShowAllConferencesActivity.class);
                            intent.putExtra("order", "date");
                            startActivity(intent);

                        }

                    }
                });

                dialog.setContentView(view1);
                dialog.show();
            }
        });
    }

    private void loadAllConferences(String order){
        Call<List<Conference>> call = userService.getAllConferencesDetailedOrderBy(order);
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    final List<Conference> conferencesListAll_ = response.body();
                    List<Conference> aux = new ArrayList<>();

                    for(Conference c : conferencesListAll_){
                        if(c.getParticipants() == null){
                            aux.add(c);
                        } else {
                            if (!c.getParticipants().contains(actorId)){
                                aux.add(c);
                            }
                        }
                    }

                    List<Conference> aux1 = new ArrayList<>(aux);

                    adapter = new ConferenceListAllAdapter(ShowAllConferencesActivity.this, aux, aux1);

                    final ListView lv = findViewById(R.id.listView);
                    lv.setAdapter(adapter);

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

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ShowAllConferencesActivity.this, ShowConferenceActivity.class);
                            intent.putExtra("idConference", conferencesListAll_.get(position).getId());
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(ShowAllConferencesActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(ShowAllConferencesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadUpcomingConferences(){
        Intent myIntent = getIntent();
        String idActor = myIntent.getExtras().get("idActor").toString();

        Call<List<Conference>> call = userService.getUpcomingConference(idActor);
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    final List<Conference> conferencesListAll_ = response.body();
                    List<Conference> aux = new ArrayList<>();

                    for(Conference c : conferencesListAll_){
                        if(c.getParticipants() == null){
                            aux.add(c);
                        } else {
                            if (!c.getParticipants().contains(actorId)){
                                aux.add(c);
                            }
                        }
                    }

                    List<Conference> aux1 = new ArrayList<>(aux);

                    adapter = new ConferenceListAllAdapter(ShowAllConferencesActivity.this, aux, aux1);

                    final ListView lv = findViewById(R.id.listView);
                    lv.setAdapter(adapter);

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
                    Toast.makeText(ShowAllConferencesActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(ShowAllConferencesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
