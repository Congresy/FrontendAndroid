package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.domain.Event;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowEventActivity extends BaseActivity {

    private UserService userService;

    TextView edtName;
    TextView edtType;
    TextView edtDescription;
    TextView edtStart;
    TextView edtEnd;
    TextView edtPlace;
    TextView edtRole;

    TextView ePlace;
    TextView eAddress;
    TextView eDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_show_event);

        Intent myIntent = getIntent();
        String idEvent = myIntent.getExtras().get("idEvent").toString();

        userService = ApiUtils.getUserService();

        edtName = findViewById(R.id.edtName);
        edtType = findViewById(R.id.edtType);
        edtRole = findViewById(R.id.edtRole);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtDescription = findViewById(R.id.edtRequirements);

        ePlace = findViewById(R.id.edtPlace);
        eAddress = findViewById(R.id.edtAddress);
        eDetails = findViewById(R.id.edtDetailsT);

        showConference(idEvent);

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
                Toast.makeText(ShowEventActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showConference(String idEvent){
        Call<Event> call = userService.getEvent(idEvent);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {

                Event event = response.body();

                edtName.setText("Name: " + event.getName());
                edtRole.setText("Role: " + event.getRole());
                edtStart.setText("Start time: " + event.getStart());
                edtEnd.setText("End time: " + event.getEnd());
                edtDescription.setText("Description: " + event.getRequirements());
                edtDescription.setText("AllowedParticipants: " + event.getAllowedParticipants());

                loadPlace(event.getPlace());

            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(ShowEventActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}