package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListOrganizatorAdapter;
import com.congresy.congresy.adapters.ConferenceListUserAdapter;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.security.BasicPermission;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMyConferencesActivity extends BaseActivity {

    UserService userService;
    private static List<Conference> conferencesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_show_my_conferences);

        userService = ApiUtils.getUserService();

        LoadMyConferences();
    }

    private void LoadMyConferences(){
        Call<List<Conference>> call = userService.getMyConferences(HomeActivity.username);
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    String role = HomeActivity.role;
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
