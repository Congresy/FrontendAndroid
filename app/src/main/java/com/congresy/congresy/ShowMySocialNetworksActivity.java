package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.SocialNetworkAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.SocialNetwork;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMySocialNetworksActivity extends BaseActivity {

    UserService userService;
    private static List<SocialNetwork> socialNetworkList;

    Button btnCreateSN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_my_social_networks);

        userService = ApiUtils.getUserService();

        btnCreateSN = findViewById(R.id.btnCreateSN);

        LoadMySocialNetworks();
    }

    private void LoadMySocialNetworks(){
        Call<Actor> call = userService.getActorByUsername(HomeActivity.username);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if(response.isSuccessful()){

                    final Actor actor = response.body();

                    btnCreateSN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent myIntent = new Intent(ShowMySocialNetworksActivity.this, CreateSocialNetworkActivity.class);
                            myIntent.putExtra("idActor", actor.getId());
                            startActivity(myIntent);
                        }
                    });

                    LoadData(actor);

                } else {
                    Toast.makeText(ShowMySocialNetworksActivity.this, "You have no social networks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(ShowMySocialNetworksActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadData(Actor actor){
        Call<List<SocialNetwork>> call = userService.getSocialNetworksByActor(actor.getId());
        call.enqueue(new Callback<List<SocialNetwork>>() {
            @Override
            public void onResponse(Call<List<SocialNetwork>> call, Response<List<SocialNetwork>> response) {
                if(response.isSuccessful()){

                    socialNetworkList = response.body();

                    SocialNetworkAdapter adapter = new SocialNetworkAdapter(getApplicationContext(), socialNetworkList);

                    ListView lv = findViewById(R.id.listView);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ShowMySocialNetworksActivity.this, ShowSocialNetworkActivity.class);
                            intent.putExtra("idSocialNetwork", socialNetworkList.get(position).getId());
                            startActivity(intent);
                        }
                    });

                    lv.setAdapter(adapter);

                } else {
                    Toast.makeText(ShowMySocialNetworksActivity.this, "You have no social networks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SocialNetwork>> call, Throwable t) {
                Toast.makeText(ShowMySocialNetworksActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
