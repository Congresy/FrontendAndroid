package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowPostActivity extends BaseActivity {

    private UserService userService;

    TextView title;
    TextView author;
    TextView posted;
    TextView body;
    TextView category;
    TextView views;
    TextView votes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_show_post);

        Intent myIntent = getIntent();
        String idPost = myIntent.getExtras().get("idPost").toString();

        userService = ApiUtils.getUserService();

        title = findViewById(R.id.title);
        title = findViewById(R.id.author);
        title = findViewById(R.id.posted);
        title = findViewById(R.id.body);
        title = findViewById(R.id.category);
        title = findViewById(R.id.views);
        title = findViewById(R.id.votes);

        showPost(idPost);

    }

    @SuppressLint("SetTextI18n")
    private void showPost(String idPost){
        Call<Post> call = userService.getPost(idPost);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                Post Post = response.body();

                title.setText("Title: " + Post.getTitle());
                author.setText("Title: " + Post.getAuthor());
                posted.setText("Title: " + Post.getPosted());
                body.setText("Title: " + Post.getBody());
                category.setText("Title: " + Post.getCategory());
                views.setText("Title: " + Post.getViews());
                votes.setText("Title: " + Post.getVotes());

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(ShowPostActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
