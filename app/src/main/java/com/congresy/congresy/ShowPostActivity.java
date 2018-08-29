package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.adapters.CommentListAdapter;
import com.congresy.congresy.domain.Comment;
import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowPostActivity extends BaseActivity {

    private UserService userService;

    TextView title;
    TextView author;
    TextView body;
    TextView category;
    TextView votes;

    ImageButton voteUp;
    ImageButton voteDown;
    ImageButton create;

    ListView comments;

    public static String id_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Post");
        loadDrawer(R.layout.activity_show_post);

        Intent myIntent = getIntent();
        final String idPost = myIntent.getExtras().get("idPost").toString();

        id_ = idPost;

        userService = ApiUtils.getUserService();

        voteUp = findViewById(R.id.voteUp);
        voteDown = findViewById(R.id.down);
        create = findViewById(R.id.create);

        title = findViewById(R.id.title);
        author = findViewById(R.id.author);
        body = findViewById(R.id.body);
        category = findViewById(R.id.category);
        votes = findViewById(R.id.votes);

        comments = findViewById(R.id.listView);

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

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowPostActivity.this, CreateCommentActivity.class);
                intent.putExtra("idCommentable", idPost);
                intent.putExtra("parent", "post");
                intent.putExtra("comeFrom", "commentable");
                startActivity(intent);

            }
        });

        showPost(idPost);
    }

    @SuppressLint("SetTextI18n")
    private void showPost(final String idPost){
        Call<Post> call = userService.getPost(idPost);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                Post post = response.body();

                title.setText(post.getTitle());
                author.setText("Posted on " + post.getPosted() + ", by " + post.getAuthorName());
                body.setText(post.getBody());
                category.setText("Post categorized as " + post.getCategory().toLowerCase());

                votes.setText(String.valueOf(post.getVotes()) + " points" + ", " + post.getViews() + " views");

                loadComments(idPost);

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(ShowPostActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadComments(String idPost){
        Call<List<Comment>> call = userService.getAllCommentsOfCommentableItem(idPost);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                List<Comment> commentsOfCommentable = response.body();

                CommentListAdapter adapter = new CommentListAdapter(getApplicationContext(), commentsOfCommentable);

                comments.setAdapter(adapter);

                justifyListViewHeightBasedOnChildren(comments);

                comments.setScrollContainer(false);

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
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

    public void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }


}
