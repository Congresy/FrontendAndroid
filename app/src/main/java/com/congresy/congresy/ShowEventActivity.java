package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    TextView edtDescription;
    TextView edtStart;
    TextView edtEnd;
    TextView edtPlace;
    TextView edtRole;

    TextView ePlace;
    TextView eAddress;
    TextView eDetails;

    ImageButton speakers;

    LinearLayout ll;
    Button participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Event");
        loadDrawer(R.layout.activity_show_event);

        Intent myIntent = getIntent();
        final String idEvent = myIntent.getExtras().get("idEvent").toString();

        userService = ApiUtils.getUserService();

        edtName = findViewById(R.id.edtName);
        edtRole = findViewById(R.id.edtRole);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtDescription = findViewById(R.id.edtRequirements);
        speakers = findViewById(R.id.speakers);
        ll = findViewById(R.id.header);
        participants = findViewById(R.id.participants);

        ll.setVisibility(View.GONE);

        ePlace = findViewById(R.id.edtPlace);
        eAddress = findViewById(R.id.edtAddress);
        eDetails = findViewById(R.id.edtDetailsT);


        speakers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ShowEventActivity.this, ShowSpeakersOfEventActivity.class);
                        intent.putExtra("idEvent", idEvent);
                        intent.putExtra("comeFrom", "user");
                        startActivity(intent);
                    }
                });

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
                eAddress.setText(p.getPostalCode() + " " + p.getAddress());
                eDetails.setText(p.getDetails());

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(ShowEventActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showConference(final String idEvent){
        Call<Event> call = userService.getEvent(idEvent);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {

                Event event = response.body();

                try {
                    if (getIntent().getStringExtra("comeFrom").equals("owner")) {
                        ll.setVisibility(View.VISIBLE);

                        participants.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent myIntent = new Intent(getApplicationContext(), ParticipantsOfElementActivity.class);
                                myIntent.putExtra("comeFrom", "event");
                                myIntent.putExtra("idEvent", idEvent);
                                getApplication().startActivity(myIntent);
                            }
                        });
                    }
                } catch (Exception e) {
                    ll.setVisibility(View.GONE);
                }

                edtName.setText(event.getName());
                edtStart.setText(event.getStart() + " - " + event.getEnd());
                edtDescription.setText(event.getRequirements());
                edtRole.setText("Event categorized as " + event.getRole() + ", has " + event.getAllowedParticipants() + " maximum participants");
                edtEnd.setText("Right now with " + String.valueOf(event.getSeatsLeft()) + " seats left");

                loadPlace(event.getPlace());

            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(ShowEventActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}