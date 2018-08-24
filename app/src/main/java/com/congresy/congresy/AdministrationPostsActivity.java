package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.congresy.congresy.adapters.PostListAdministratorAdapter;
import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdministrationPostsActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private PostListAdministratorAdapter adapter;

    UserService userService;

    ListView list;
    SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDrawer(R.layout.activity_administration_posts);

        userService = ApiUtils.getUserService();

        list = findViewById(R.id.listView);
        search = findViewById(R.id.search);

        loadAllPosts();

        search.setOnQueryTextListener(this);
    }

    private void loadAllPosts(){
        Call<List<Post>> call = userService.getAllPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.isSuccessful()){

                    final List<Post> posts = response.body();
                    final List<Post> aux = new ArrayList<>(posts);

                    adapter = new PostListAdministratorAdapter(getApplicationContext(), posts, aux);

                    list.setAdapter(adapter);

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(AdministrationPostsActivity.this, ShowPostActivity.class);
                            intent.putExtra("idPost", posts.get(position).getId());
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(AdministrationPostsActivity.this, "You have no post!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(AdministrationPostsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }

}
