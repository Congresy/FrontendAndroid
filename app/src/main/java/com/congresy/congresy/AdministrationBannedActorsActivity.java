package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ActorListBannedAdministratorAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdministrationBannedActorsActivity extends BaseActivity {

    UserService userService;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_administration_banned_actors);

        userService = ApiUtils.getUserService();

        loadActors();

    }

    private void loadActors(){
        Call<List<Actor>> call = userService.getBannedActors();
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {
                if(response.isSuccessful()){

                    final List<Actor> actors = response.body();

                    ListView lv = findViewById(R.id.listView);

                    ActorListBannedAdministratorAdapter adapter = new ActorListBannedAdministratorAdapter(getApplicationContext(), actors);
                    lv.setAdapter(adapter);


                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                            myIntent.putExtra("goingTo", "Unknown");
                            myIntent.putExtra("idAuthor", actors.get(position).getId());
                            getApplication().startActivity(myIntent);
                        }
                    });


                } else {
                    Toast.makeText(AdministrationBannedActorsActivity.this, "This event has no speakers!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(AdministrationBannedActorsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
