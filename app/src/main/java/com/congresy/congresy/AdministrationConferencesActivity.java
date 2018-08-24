package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListAllAdministratorAdapter;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdministrationConferencesActivity extends BaseActivity {

    private ConferenceListAllAdministratorAdapter adapter;

    UserService userService;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_administration_conferences);

        userService = ApiUtils.getUserService();

        lv = findViewById(R.id.listView);

        loadAllConferences("date");
    }

    private void loadAllConferences(String order){
        Call<List<Conference>> call = userService.getAllConferencesDetailedOrderBy(order);
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    final List<Conference> conferences = response.body();

                    adapter = new ConferenceListAllAdministratorAdapter(AdministrationConferencesActivity.this, conferences);

                    final ListView lv = findViewById(R.id.listView);
                    lv.setAdapter(adapter);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(AdministrationConferencesActivity.this, ShowConferenceActivity.class);
                            intent.putExtra("idConference", conferences.get(position).getId());
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(AdministrationConferencesActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(AdministrationConferencesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
