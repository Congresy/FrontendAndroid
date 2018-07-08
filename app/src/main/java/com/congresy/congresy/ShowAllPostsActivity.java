package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListAddSpeakerAdapter;
import com.congresy.congresy.adapters.PostListSearchAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAllPostsActivity extends BaseActivity implements SearchView.OnQueryTextListener{

    private PostListSearchAdapter adapter;
    private final static List<Post> posts = new ArrayList<>(HomeActivity.posts_);

    UserService userService;

    ListView list;
    SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        loadDrawer(R.layout.activity_show_all_posts);

        userService = ApiUtils.getUserService();

        search = findViewById(R.id.search);

        loadMostVotedPosts();

        List<Post> aux = new ArrayList<>(posts);

        ListView lv = findViewById(R.id.listViewAll);
        adapter = new PostListSearchAdapter(this, posts, aux);

        lv.setAdapter(adapter);

        search.setOnQueryTextListener(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShowAllPostsActivity.this, ShowEventActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadMostVotedPosts(){
        Call<List<Post>> call = userService.getMostVoted();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.isSuccessful()){

                    List<Post> posts = response.body();
                    List<Post> res = new ArrayList<>();

                    res.add(posts.get(0));
                    //res.add(posts.get(1));
                    //res.add(posts.get(2));
                    //res.add(posts.get(3));

                    ListView lv = findViewById(R.id.listViewMostVoted);
                    ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, res);

                    lv.setAdapter(adapter);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ShowAllPostsActivity.this, ShowEventActivity.class);
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(ShowAllPostsActivity.this, "You have no social networks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(ShowAllPostsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
