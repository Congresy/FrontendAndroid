package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ActorListBannedAdministratorAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdministrationBannedActorsActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private  ActorListBannedAdministratorAdapter adapter;

    UserService userService;

    ListView listView;
    SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Banned actors");

        loadDrawer(R.layout.activity_administration_banned_actors);

        userService = ApiUtils.getUserService();

        search = findViewById(R.id.search);

        loadActors();

        search.setOnQueryTextListener(this);

    }

    private void loadActors(){
        Call<List<Actor>> call = userService.getBannedActors();
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {
                if(response.isSuccessful()){

                    final List<Actor> actors = response.body();
                    final List<Actor> aux = new ArrayList<>(actors);

                    ListView lv = findViewById(R.id.listView);

                    adapter = new ActorListBannedAdministratorAdapter(getApplicationContext(), actors, aux);
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
