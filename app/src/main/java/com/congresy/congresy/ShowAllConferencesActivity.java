package com.congresy.congresy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
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

public class ShowAllConferencesActivity extends AppCompatActivity {

    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_all_conferences);

        userService = ApiUtils.getUserService();

        LoadAllConferences();
    }

    private void LoadAllConferences(){
        Call<List<Conference>> call = userService.getAllConferencesDetailedOrderByDate();
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    List<Conference> conferencesListAll_ = response.body();
                    List<Conference> aux = new ArrayList<>();

                    for(Conference c : conferencesListAll_){
                        if(c.getParticipants() == null){
                            aux.add(c);
                        } else {
                            if (!c.getParticipants().contains(HomeActivity.actor_.getId())){
                                aux.add(c);
                            }
                        }
                    }

                    ConferenceListAllAdapter adapter = new ConferenceListAllAdapter(ShowAllConferencesActivity.this, aux);

                    final ListView lv = findViewById(R.id.listView);
                    lv.setAdapter(adapter);

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
