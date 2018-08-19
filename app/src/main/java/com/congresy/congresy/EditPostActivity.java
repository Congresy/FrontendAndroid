package com.congresy.congresy;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.congresy.congresy.adapters.PostListOwnAdapter;
import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import org.joda.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPostActivity extends BaseActivity {

    UserService userService;

    EditText titleE;
    EditText bodyE;
    Spinner s;

    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_edit_post);

        save = findViewById(R.id.save);

        titleE = findViewById(R.id.titleE);
        bodyE = findViewById(R.id.bodyE);
        s = findViewById(R.id.spinner);

        userService = ApiUtils.getUserService();

        getPost();

    }

    private void getPost(){
        Intent myIntent = getIntent();
        String idPost = myIntent.getExtras().get("idPost").toString();

        Call<Post> call = userService.getPost(idPost);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                Post post = response.body();

                // set spinner values
                String[] arraySpinner = new String[] {
                        "General", "Administration", "Stories", "Tutorial", "Information", "Other"
                };

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);

                Intent myIntent = getIntent();
                String nameAux = myIntent.getExtras().get("category").toString();

                int index = 0;
                for (String st : arraySpinner){
                    if(st.equals(nameAux)){
                        s.setSelection(index);
                        break;
                    }
                    index++;
                }

                titleE.setText(post.getTitle());
                bodyE.setText(post.getBody());

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        JsonObject json = new JsonObject();

                        String category = s.getSelectedItem().toString();
                        String title = titleE.getText().toString();
                        String body = bodyE.getText().toString();

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

                        showAlertDialog(json);
                    }
                });

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(EditPostActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void savePost(final JsonObject json){
        Intent myIntent = getIntent();
        String idPost = myIntent.getExtras().get("idPost").toString();

        Call<Post> call = userService.editPost(idPost, json);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(EditPostActivity.this, ShowMyPostsActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(EditPostActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(EditPostActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void publishPost(final JsonObject json){
        Intent myIntent = getIntent();
        String idPost = myIntent.getExtras().get("idPost").toString();

        Call<Post> call = userService.editPost(idPost, json);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(response.isSuccessful()){

                    executePublish(response.body().getId());

                } else {
                    Toast.makeText(EditPostActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(EditPostActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void executePublish(String idPost){
        Call<Post> call = userService.publishPost(idPost);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(EditPostActivity.this, ShowAllPostsActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(EditPostActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(EditPostActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showAlertDialog(final JsonObject json) {


        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention!");
        builder.setMessage("You can choose between make the post public or save it as a draft. Note that if you make it public you won't be able to edit it afterwards.");

        // add a button
        builder.setPositiveButton("Publish", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                publishPost(json);
            }
        });

        builder.setNegativeButton("Save as draft", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savePost(json);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
