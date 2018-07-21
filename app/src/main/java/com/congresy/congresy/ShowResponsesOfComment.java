package com.congresy.congresy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.CommentListAdapter;
import com.congresy.congresy.adapters.CommentListForResponsesAdapter;
import com.congresy.congresy.domain.Comment;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowResponsesOfComment extends BaseActivity {

    UserService userService = ApiUtils.getUserService();

    ListView respones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_show_responses_of_comment);

        respones = findViewById(R.id.listView);

        Intent intent = getIntent();
        String id = intent.getExtras().get("idComment").toString();

        loadComments(id);
    }

    private void loadComments(final String idComment){
        Call<List<Comment>> call = userService.getResponsesOfComment(idComment);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                List<Comment> commentsOfCommentable = response.body();

                CommentListForResponsesAdapter adapter = new CommentListForResponsesAdapter(getApplicationContext(), commentsOfCommentable, idComment);

                respones.setAdapter(adapter);

                justifyListViewHeightBasedOnChildren(respones);

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(ShowResponsesOfComment.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
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
        listView.setScrollContainer(false);
    }
}
