package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import org.joda.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends BaseActivity {

    UserService userService;

    EditText titleE;
    EditText bodyE;
    Spinner s;

    Button create;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_create_post);

        create = findViewById(R.id.create);
        save = findViewById(R.id.save);

        titleE = findViewById(R.id.titleE);
        bodyE = findViewById(R.id.bodyE);
        s = findViewById(R.id.spinner);

        userService = ApiUtils.getUserService();

        // set spinner values
        String[] arraySpinner = new String[] {
                "General", "Administration", "Stories", "Tutorial", "Information", "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleE.getText().toString();
                String body = bodyE.getText().toString();

                // adding properties to json for POST
                JsonObject json = new JsonObject();

                String category = s.getSelectedItem().toString();

                SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                String id = sp.getString("Id", "not found");

                json.addProperty("author", id);
                json.addProperty("title", title);
                json.addProperty("body", body);
                json.addProperty("category", category);
                json.addProperty("draft", true);
                json.addProperty("votes", 0);
                json.addProperty("views", 0);
                json.addProperty("posted", LocalDateTime.now().toString("dd/MM/yyyy HH:mm"));

                createPost(json);
                //savePost();
            }
        });
    }

    private void createPost(final JsonObject json){
        Call<Post> call = userService.savePost(json);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(CreatePostActivity.this, ShowAllPostsActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(CreatePostActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(CreatePostActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
