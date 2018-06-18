package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.congresy.congresy.domain.SocialNetwork;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateSocialNetworkActivity extends BaseActivity {

    UserService userService;

    EditText edtUrl;

    Spinner s;

    Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_create_social_network);

        btnCreate = findViewById(R.id.btnCreate);

        edtUrl = findViewById(R.id.edtUrl);
        s = findViewById(R.id.spinner);

        userService = ApiUtils.getUserService();

        // set spinner values
        String[] arraySpinner = new String[] {
                "Twitter", "Instagram", "Facebook", "Google", "LinkedIn"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = s.getSelectedItem().toString();
                String url = edtUrl.getText().toString();

                // adding properties to json for POST
                JsonObject json = new JsonObject();


                json.addProperty("name", name);
                json.addProperty("url", url);

                Intent myIntent = getIntent();
                String idActor = myIntent.getExtras().get("idActor").toString();

                json.addProperty("actor", idActor);


                createSocialNetwork(json);
            }
        });
    }

    private void createSocialNetwork(final JsonObject json){
        Intent myIntent = getIntent();
        String idActor = myIntent.getExtras().get("idActor").toString();

        Call<SocialNetwork> call = userService.createSocialNetwork(idActor, json);
        call.enqueue(new Callback<SocialNetwork>() {
            @Override
            public void onResponse(Call<SocialNetwork> call, Response<SocialNetwork> response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(CreateSocialNetworkActivity.this, ShowMySocialNetworksActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(CreateSocialNetworkActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SocialNetwork> call, Throwable t) {
                Toast.makeText(CreateSocialNetworkActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
