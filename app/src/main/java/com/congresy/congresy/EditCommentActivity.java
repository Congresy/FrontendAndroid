package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.congresy.congresy.domain.Comment;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCommentActivity extends BaseActivity {

    UserService userService;

    EditText titleE;
    EditText bodyE;

    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_edit_comment);

        save = findViewById(R.id.save);

        titleE = findViewById(R.id.titleE);
        bodyE = findViewById(R.id.bodyE);

        userService = ApiUtils.getUserService();

        getComment();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JsonObject json = new JsonObject();

                String title = titleE.getText().toString();
                String body = bodyE.getText().toString();

                json.addProperty("title", title);
                json.addProperty("text", body);

                editComment(json);
            }
        });

    }

    private void getComment(){
        Intent myIntent = getIntent();
        String idComment = myIntent.getExtras().get("idComment").toString();

        Call<Comment> call = userService.getComment(idComment);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {

                Comment comment = response.body();

                titleE.setText(comment.getTitle());
                bodyE.setText(comment.getText());

            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(EditCommentActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editComment(JsonObject jsonObject){
        Intent myIntent = getIntent();
        String idComment = myIntent.getExtras().get("idComment").toString();

        Call<Comment> call = userService.editComment(idComment, jsonObject);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {

                Comment comment = response.body();

                String id_ = ShowPostActivity.id_;

                Intent intent = new Intent(EditCommentActivity.this, ShowMyCommentsActivity.class);
                intent.putExtra("idComment", comment.getId());
                intent.putExtra("idPost", id_);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(EditCommentActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
