package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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
    TextView edtEnd;
    TextView edtSpeakers;
    TextView edtDescription;
    TextView edtPartic;
    TextView seats;

    TextView ePlace;
    TextView eAddress;
    TextView eDetails;

    Button create;

    ListView comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_show_conference);

        Intent myIntent = getIntent();
        final String idConference = myIntent.getExtras().get("idConference").toString();

        userService = ApiUtils.getUserService();

        edtName = findViewById(R.id.edtName);
        edtTheme = findViewById(R.id.edtTheme);
        edtPrice = findViewById(R.id.edtPrice);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtSpeakers = findViewById(R.id.edtSpeakers);
        edtDescription = findViewById(R.id.edtDescription);
        edtPartic = findViewById(R.id.edtPartic);
        seats = findViewById(R.id.seats);

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

                edtName.setText("Name: " + con.getName());
                edtTheme.setText("Theme: " + con.getTheme());
                edtPrice.setText("Price: " + String.valueOf(con.getPrice()));
                edtStart.setText("Start date: " + con.getStart());
                edtEnd.setText("End date: " + con.getEnd());
                edtSpeakers.setText("Speakers attending: " + con.getSpeakersNames());
                edtDescription.setText("Description: " + con.getDescription());
                edtPartic.setText("Actual allowed participants: " + String.valueOf(con.getAllowedParticipants()));
                seats.setText("Seats left: " + String.valueOf(con.getSeatsLeft()));

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
                eAddress.setText(p.getAddress() + ", " + p.getPostalCode());
                eAddress.setText("Details: " + p.getDetails());

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
