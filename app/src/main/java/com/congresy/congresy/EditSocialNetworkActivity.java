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

import com.congresy.congresy.adapters.SocialNetworkAdapter;
import com.congresy.congresy.domain.SocialNetwork;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditSocialNetworkActivity extends BaseActivity {
    
    UserService userService;
    
    Spinner name;
    EditText edtUrl;

    Button btnEdit;

    private Integer aux = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_edit_social_network);

        btnEdit = findViewById(R.id.btnEdit);
        
        name = findViewById(R.id.spinner);
        edtUrl = findViewById(R.id.edtUrl);

        userService = ApiUtils.getUserService();

        // set spinner values
        String[] arraySpinner = new String[] {
                "Twitter", "Instagram", "Facebook", "Google", "LinkedIn"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        name.setAdapter(adapter);

        Intent myIntent = getIntent();
        String nameAux = myIntent.getExtras().get("name").toString();
        
        int index = 0;
        for (String s : arraySpinner){
            if(s.equals(nameAux)){
                name.setSelection(index);
                break;
            }
            index++;
        }
        
        edtUrl.setText(SocialNetworkAdapter.socialNetwork_.getUrl());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameS = name.getSelectedItem().toString();
                String url = edtUrl.getText().toString();
                
                // adding properties to json for POST
                JsonObject json = new JsonObject();

                json.addProperty("name", nameS);
                json.addProperty("url", url);

                if (validate(url))
                    editSocialNetwork(json);
            }
        });
    }

    private boolean validate(String url){
        if(checkString("blank", url, edtUrl, null) || checkUrl(url, edtUrl))
            aux++;

        if (aux != 0)
            edtUrl.requestFocus();

        return aux == 0;
    }

    private void editSocialNetwork(final JsonObject json){
        Call<SocialNetwork> call = userService.editSocialNetwork(SocialNetworkAdapter.socialNetwork_.getId(), json);
        call.enqueue(new Callback<SocialNetwork>() {
            @Override
            public void onResponse(Call<SocialNetwork> call, Response<SocialNetwork> response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(EditSocialNetworkActivity.this, ShowMySocialNetworksActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(EditSocialNetworkActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SocialNetwork> call, Throwable t) {
                Toast.makeText(EditSocialNetworkActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
