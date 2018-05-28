package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListOrganizatorAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowConference extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conference);

        Intent myIntent = getIntent();
        String idConference = myIntent.getExtras().get("idConference").toString();

        userService = ApiUtils.getUserService();

        edtName = findViewById(R.id.edtName);
        edtTheme = findViewById(R.id.edtTheme);
        edtPrice = findViewById(R.id.edtPrice);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtSpeakers = findViewById(R.id.edtSpeakers);
        edtDescription = findViewById(R.id.edtDescription);
        edtPartic = findViewById(R.id.edtPartic);

        showConference(idConference);

    }

    @SuppressLint("SetTextI18n")
    private void showConference(String idConference){
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

                }

                @Override
                public void onFailure(Call<Conference> call, Throwable t) {
                    Toast.makeText(ShowConference.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
        });

    }

}
