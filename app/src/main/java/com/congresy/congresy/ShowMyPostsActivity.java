package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.PostListOwnAdapter;
import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMyPostsActivity extends BaseActivity {

    UserService userService;

    ListView list;

    Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My posts");
        loadDrawer(R.layout.activity_show_my_posts);

        userService = ApiUtils.getUserService();

        create = findViewById(R.id.create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowMyPostsActivity.this, CreatePostActivity.class);
                startActivity(intent);
            }
        });

        loadMyPosts();
    }

    private void loadMyPosts(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Call<List<Post>> call = userService.getOwnPosts(id);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.isSuccessful()){

                    final List<Post> posts = response.body();

                    List<Post> drafts = new ArrayList<>();
                    List<Post> published = new ArrayList<>();

                    for (Post p : posts){
                        if (p.getDraft()){
                            drafts.add(p);
                         } else {
                            published.add(p);
                        }
                    }

                    ListView lv1 = findViewById(R.id.drafts);
                    ListView lv2 = findViewById(R.id.published);

                    PostListOwnAdapter adapter1 = new PostListOwnAdapter(getApplicationContext(), drafts);
                    PostListOwnAdapter adapter2 = new PostListOwnAdapter(getApplicationContext(), published);

                    lv1.setAdapter(adapter1);
                    lv2.setAdapter(adapter2);

                    lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ShowMyPostsActivity.this, ShowPostActivity.class);
                            intent.putExtra("idPost", posts.get(position).getId());
                            startActivity(intent);
                        }
                    });

                    lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ShowMyPostsActivity.this, ShowPostActivity.class);
                            intent.putExtra("idPost", posts.get(position).getId());
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(ShowMyPostsActivity.this, "You have no post!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(ShowMyPostsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
