package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ActorListAdministratorAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdministrationActorsActivity extends BaseActivity  implements SearchView.OnQueryTextListener {

    private  ActorListAdministratorAdapter adapter;

    UserService userService;

    Button banned;

    ListView listView;
    SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_administration_actors);

        userService = ApiUtils.getUserService();
        search = findViewById(R.id.search);

        banned = findViewById(R.id.banned);

        loadActors();

        search.setOnQueryTextListener(this);

        banned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), AdministrationBannedActorsActivity.class);
                getApplication().startActivity(myIntent);
            }
        });

    }

    private void loadActors(){
        Call<List<Actor>> call = userService.getAllActors();
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {
                if(response.isSuccessful()){

                    final List<Actor> actors = response.body();
                    final List<Actor> aux = new ArrayList<>(actors);

                    ListView lv = findViewById(R.id.listView);

                    adapter = new ActorListAdministratorAdapter(getApplicationContext(), actors, aux);

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
                    Toast.makeText(AdministrationActorsActivity.this, "This event has no speakers!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(AdministrationActorsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
