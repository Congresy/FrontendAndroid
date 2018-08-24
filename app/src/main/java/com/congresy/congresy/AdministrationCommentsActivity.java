package com.congresy.congresy;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.CommentListAdministratorAdapter;
import com.congresy.congresy.domain.Comment;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdministrationCommentsActivity extends BaseActivity {

    UserService userService;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_administration_comments);

        lv = findViewById(R.id.listView);

        userService = ApiUtils.getUserService();

        loadAllComments();

    }

    private void loadAllComments(){
        Call<List<Comment>> call = userService.getAllComments();
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                List<Comment> comments = response.body();

                CommentListAdministratorAdapter adapter = new CommentListAdministratorAdapter(getApplicationContext(), comments);

                lv.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(AdministrationCommentsActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    
}
