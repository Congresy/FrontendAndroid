package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.MyCommentsListAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Comment;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMyCommentsActivity extends BaseActivity {

    UserService userService;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My comments");
        loadDrawer(R.layout.activity_show_my_comments);

        lv = findViewById(R.id.listView);

        userService = ApiUtils.getUserService();

        loadMyComments();

    }

    private void loadMyComments(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not_found");

        Call<List<Comment>> call = userService.getMyComments(id);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                if (response.isSuccessful()){

                    List<Comment> myComments = response.body();

                    MyCommentsListAdapter adapter = new MyCommentsListAdapter(getApplicationContext(), myComments);

                    lv.setAdapter(adapter);
                } else {
                    Toast.makeText(ShowMyCommentsActivity.this, "You have no comments yet!", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(ShowMyCommentsActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
