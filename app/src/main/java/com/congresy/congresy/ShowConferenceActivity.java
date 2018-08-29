package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.adapters.CommentListAdapter;
import com.congresy.congresy.domain.Comment;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowConferenceActivity extends BaseActivity {

    private Conference conference;

    private UserService userService;

    TextView edtName;
    TextView edtTheme;
    TextView edtPrice;
    TextView edtStart;
    TextView edtDescription;

    TextView ePlace;
    TextView eAddress;
    TextView eDetails;

    ImageButton create;

    ListView comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Conference");
        loadDrawer(R.layout.activity_show_conference);

        Intent myIntent = getIntent();
        final String idConference = myIntent.getExtras().get("idConference").toString();

        userService = ApiUtils.getUserService();

        edtName = findViewById(R.id.edtName);
        edtTheme = findViewById(R.id.edtTheme);
        edtPrice = findViewById(R.id.edtPrice);
        edtStart = findViewById(R.id.startAndEnd);
        edtDescription = findViewById(R.id.edtDescription);

        ePlace = findViewById(R.id.edtPlace);
        eAddress = findViewById(R.id.edtAddress);
        eDetails = findViewById(R.id.edtDetailsT);

        create = findViewById(R.id.create);
        comments = findViewById(R.id.listView);

        showConference(idConference);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowConferenceActivity.this, CreateCommentActivity.class);
                intent.putExtra("idCommentable", idConference);
                intent.putExtra("comeFrom", "commentable");
                intent.putExtra("parent", "conference");
                startActivity(intent);

            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showConference(final String idConference){
        Call<Conference> call = userService.getConference(idConference);
        call.enqueue(new Callback<Conference>() {
            @Override
            public void onResponse(Call<Conference> call, Response<Conference> response) {

                Conference con = response.body();

                edtName.setText(con.getName());
                edtTheme.setText("Content categorized as " + con.getTheme().toLowerCase() + ", has " + con.getAllowedParticipants() + " maximum participants");
                edtPrice.setText("Price: " + String.valueOf(con.getPrice()) + ", right now with " + String.valueOf(con.getSeatsLeft()) + " seats left");
                edtStart.setText(con.getStart() + " - " + con.getEnd());
                edtDescription.setText(con.getDescription() + "\n\n Main speakers attending this conference: " + con.getSpeakersNames());

                loadComments(idConference, con.getPlace());

                }

                @Override
                public void onFailure(Call<Conference> call, Throwable t) {
                    Toast.makeText(ShowConferenceActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
        });

    }

    private void loadComments(final String idPost, final String idPlace){
        Call<List<Comment>> call = userService.getAllCommentsOfCommentableItem(idPost);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                List<Comment> commentsOfCommentable = response.body();

                CommentListAdapter adapter = new CommentListAdapter(getApplicationContext(), commentsOfCommentable);

                comments.setAdapter(adapter);

                justifyListViewHeightBasedOnChildren(comments);

                comments.setScrollContainer(false);

                loadPlace(idPlace);

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(ShowConferenceActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadPlace(String idPlace){
        Call<Place> call = userService.getPlace(idPlace);
        call.enqueue(new Callback<Place>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {

                Place p = response.body();

                ePlace.setText(p.getTown() + ", " + p.getCountry());
                eAddress.setText(p.getPostalCode() + " " + p.getAddress());
                eDetails.setText(p.getDetails());

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(ShowConferenceActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
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
