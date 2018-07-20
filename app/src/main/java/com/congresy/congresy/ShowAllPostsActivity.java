package com.congresy.congresy;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.congresy.congresy.adapters.PostListSearchAdapter;
import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAllPostsActivity extends BaseActivity implements SearchView.OnQueryTextListener{

    private PostListSearchAdapter adapter;
    public static List<Post> posts = new ArrayList<>(HomeActivity.posts_);

    UserService userService;

    ListView list;
    SearchView search;

    ListView lv;

    Button myPosts;
    Button filter;
    Button myComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        loadDrawer(R.layout.activity_show_all_posts);

        userService = ApiUtils.getUserService();

        search = findViewById(R.id.search);
        myPosts = findViewById(R.id.myPosts);
        filter = findViewById(R.id.filter);
        myComments = findViewById(R.id.myComments);

        loadMostVotedPosts();

        try {
            Intent myIntent = getIntent();
            String theme_filter = myIntent.getExtras().get("theme_filter").toString();

            List<Post> filteredPosts = new ArrayList<>();

            if (!theme_filter.equals("None")){
                for(Post p : posts){
                    if(p.getCategory().equals(theme_filter)){
                        filteredPosts.add(p);
                    }
                }

                List<Post> aux = new ArrayList<>(filteredPosts);

                lv = findViewById(R.id.listViewAll);
                adapter = new PostListSearchAdapter(this, filteredPosts, aux);

            } else {
                List<Post> aux = new ArrayList<>(posts);

                lv = findViewById(R.id.listViewAll);
                adapter = new PostListSearchAdapter(this, posts, aux);
            }
        } catch (NullPointerException e){
            List<Post> aux = new ArrayList<>(posts);

            lv = findViewById(R.id.listViewAll);
            adapter = new PostListSearchAdapter(this, posts, aux);
        }


        lv.setAdapter(adapter);

        search.setOnQueryTextListener(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShowAllPostsActivity.this, ShowPostActivity.class);
                intent.putExtra("idPost", posts.get(position).getId());
                startActivity(intent);
            }
        });

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowAllPostsActivity.this, ShowMyPostsActivity.class);
                startActivity(intent);
            }
        });

        myComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowAllPostsActivity.this, ShowMyCommentsActivity.class);
                startActivity(intent);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ShowAllPostsActivity.this);
                LayoutInflater inflater = LayoutInflater.from(ShowAllPostsActivity.this);
                View view1 = inflater.inflate(R.layout.post_custom_dialog_filter_theme, null);
                ListView listView = view1.findViewById(R.id.listView);
                SearchView searchView = view1.findViewById(R.id.searchView);

                final String[] arraySpinner = new String[] {
                        "None", "General", "Administration", "Stories", "Tutorial", "Information", "Other"
                };

                final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(ShowAllPostsActivity.this, android.R.layout.simple_list_item_1, arraySpinner);
                listView.setAdapter(stringArrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ShowAllPostsActivity.this, ShowAllPostsActivity.class);
                        intent.putExtra("theme_filter", arraySpinner[position]);
                        startActivity(intent);
                    }
                });

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String newText) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        stringArrayAdapter.getFilter().filter(newText);
                        return false;
                    }

                });
                dialog.setContentView(view1);
                dialog.show();
            }
        });


    }

    private void loadMostVotedPosts(){
        Call<List<Post>> call = userService.getMostVoted();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.isSuccessful()){

                    final List<Post> posts = response.body();
                    List<Post> res = new ArrayList<>();

                    res.add(posts.get(0));
                    res.add(posts.get(1));
                    res.add(posts.get(2));
                    res.add(posts.get(3));

                    ListView lv = findViewById(R.id.listViewMostVoted);
                    ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, res);

                    lv.setAdapter(adapter);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ShowAllPostsActivity.this, ShowPostActivity.class);
                            intent.putExtra("idPost", posts.get(position).getId());
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
