package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

    private String username;
    private String role;

    Button myComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_show_my_conferences);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        username = sp.getString("Username", "not found");
        role = sp.getString("Role", "not found");

        myComments = findViewById(R.id.myComments);

        userService = ApiUtils.getUserService();

        LoadMyConferences();

        myComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowMyConferencesActivity.this, ShowMyCommentsActivity.class);
                intent.putExtra("parent", "conference");
                startActivity(intent);
            }
        });
    }

    private void LoadMyConferences(){
        Call<List<Conference>> call = userService.getMyConferences(username);
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    ConferenceListOrganizatorAdapter adapter = null;
                    ConferenceListUserAdapter adapter1 = null;
                    conferencesList = response.body();

                    if(role.equals("Organizator")) {
                        adapter = new ConferenceListOrganizatorAdapter(getApplicationContext(), conferencesList);
                    } else {
                        adapter1 = new ConferenceListUserAdapter(getApplicationContext(), conferencesList);
                    }

                    final ListView lv = findViewById(R.id.listView);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ShowMyConferencesActivity.this, ShowConferenceActivity.class);
                            intent.putExtra("idConference", conferencesList.get(position).getId());
                            startActivity(intent);
                        }
                    });

                    if(role.equals("Organizator")) {
                        lv.setAdapter(adapter);
                    } else {
                        lv.setAdapter(adapter1);
                    }


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
