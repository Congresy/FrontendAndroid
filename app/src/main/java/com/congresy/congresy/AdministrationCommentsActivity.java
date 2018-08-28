package com.congresy.congresy;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.congresy.congresy.adapters.CommentListAdministratorAdapter;
import com.congresy.congresy.domain.Comment;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdministrationCommentsActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    CommentListAdministratorAdapter adapter;

    UserService userService;

    ListView lv;
    SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Comments");

        loadDrawer(R.layout.activity_administration_comments);

        lv = findViewById(R.id.listView);
        search = findViewById(R.id.search);

        userService = ApiUtils.getUserService();

        loadAllComments();

        search.setOnQueryTextListener(this);

    }

    private void loadAllComments(){
        Call<List<Comment>> call = userService.getAllComments();
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                List<Comment> comments = response.body();
                List<Comment> aux = new ArrayList<>(comments);

                adapter = new CommentListAdministratorAdapter(getApplicationContext(), comments, aux);

                lv.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(AdministrationCommentsActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
