package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Comment;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import org.joda.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCommentActivity extends BaseActivity {

    UserService userService;

    EditText titleE;
    EditText bodyE;

    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        loadDrawer(R.layout.activity_create_comment);
        save = findViewById(R.id.save);

        titleE = findViewById(R.id.titleE);
        bodyE = findViewById(R.id.bodyE);

        userService = ApiUtils.getUserService();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JsonObject json = new JsonObject();

                String title = titleE.getText().toString();
                String body = bodyE.getText().toString();

                json.addProperty("title", title);
                json.addProperty("text", body);
                json.addProperty("thumbsUp", 0);
                json.addProperty("thumbsDown", 0);
                json.addProperty("sentMoment", LocalDateTime.now().toString("dd/MM/yyyy HH:mm"));

                createComment(json);

            }
        });

    }

    private void createComment(JsonObject json){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not_found");

        Intent myIntent = getIntent();
        String idCommentable = myIntent.getExtras().get("idCommentable").toString();

        Call<Comment> call = userService.createComment(json, idCommentable, id);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {

                Intent intent = new Intent(CreateCommentActivity.this, ShowPostActivity.class);
                intent.putExtra("idPost", response.body().getCommentable());
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(CreateCommentActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
