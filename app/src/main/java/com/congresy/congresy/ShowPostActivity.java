package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    Button voteUp;
    Button voteDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_show_post);

        Intent myIntent = getIntent();
        final String idPost = myIntent.getExtras().get("idPost").toString();

        userService = ApiUtils.getUserService();

        voteUp = findViewById(R.id.voteUp);
        voteDown = findViewById(R.id.voteDown);

        title = findViewById(R.id.title);
        author = findViewById(R.id.author);
        posted = findViewById(R.id.posted);
        body = findViewById(R.id.body);
        category = findViewById(R.id.category);
        views = findViewById(R.id.views);
        votes = findViewById(R.id.votes);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String aux = sp.getString("AlreadyVoted " + idPost, "not found");
        if(!aux.equals("not found")){
            voteUp.setVisibility(View.INVISIBLE);
            voteDown.setVisibility(View.INVISIBLE);
        }

        voteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteUp(idPost);
            }
        });

        voteDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteDown(idPost);
            }
        });

        showPost(idPost);
    }

    @SuppressLint("SetTextI18n")
    private void showPost(String idPost){
        Call<Post> call = userService.getPost(idPost);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                Post Post = response.body();

                title.setText(Post.getTitle());
                author.setText(Post.getAuthor() + ", " + Post.getPosted());
                posted.setText("\n");
                body.setText(Post.getBody());
                category.setText("\n");
                views.setText("Category:" +  Post.getCategory());
                votes.setText(Post.getVotes() + " votes and " + Post.getViews() + " views");

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(ShowPostActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void voteUp(final String idPost){
        Call<Post> call = userService.votePost(idPost, "add");
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("AlreadyVoted " + idPost, "1");
                editor.apply();

                Intent intent = new Intent(ShowPostActivity.this, ShowPostActivity.class);
                intent.putExtra("idPost", idPost);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(ShowPostActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void voteDown(final String idPost){
        Call<Post> call = userService.votePost(idPost, "delete");
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("AlreadyVoted " + idPost, "1");
                editor.apply();

                Intent intent = new Intent(ShowPostActivity.this, ShowPostActivity.class);
                intent.putExtra("idPost", idPost);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(ShowPostActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
