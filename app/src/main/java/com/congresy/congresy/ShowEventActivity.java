package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowEventActivity extends AppCompatActivity {

    private Conference conference;

    private UserService userService;

    TextView edtName;
    TextView edtType;
    TextView edtDescription;
    TextView edtStart;
    TextView edtEnd;
    TextView edtPlace;
    TextView edtRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        Intent myIntent = getIntent();
        String idEvent = myIntent.getExtras().get("idEvent").toString();

        userService = ApiUtils.getUserService();

        edtName = findViewById(R.id.edtName);
        edtType = findViewById(R.id.edtType);
        edtRole = findViewById(R.id.edtRole);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtPlace = findViewById(R.id.edtPlace);
        edtDescription = findViewById(R.id.edtRequirements);

        showConference(idEvent);

    }

    @SuppressLint("SetTextI18n")
    private void showConference(String idEvent){
        Call<Event> call = userService.getEvent(idEvent);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {

                Event event = response.body();

                edtName.setText("Name: " + event.getName());
                edtType.setText("Type: " + event.getType());
                edtRole.setText("Role: " + event.getRole());
                edtStart.setText("Start time: " + event.getStart());
                edtEnd.setText("End time: " + event.getEnd());
                edtPlace.setText("Place: " + event.getPlace());
                edtDescription.setText("Description: " + event.getRequirements());

            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(ShowEventActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}