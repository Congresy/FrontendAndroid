package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.congresy.congresy.domain.Event;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends BaseActivity {

    UserService userService;

    EditText edtName;
    EditText edtType;
    EditText edtDescription;
    EditText edtStart;
    EditText edtEnd;
    EditText edtPlace;
    EditText edtAllw;
    Spinner s;

    // Place attributes
    EditText edtTown;
    EditText edtCountry;
    EditText edtAddress;
    EditText edtPostalCode;
    EditText edtDetails;

    Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_create_event);

        btnCreate = findViewById(R.id.btnCreate);

        edtName = findViewById(R.id.edtName);
        edtType = findViewById(R.id.edtType);
        s = findViewById(R.id.spinner);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtPlace = findViewById(R.id.edtPlace);
        edtDescription = findViewById(R.id.edtRequirements);
        edtAllw = findViewById(R.id.edtAllw);

        // Pace attributes
        edtTown = findViewById(R.id.edtTown);
        edtCountry = findViewById(R.id.edtCountry);
        edtAddress = findViewById(R.id.edtAddress);
        edtPostalCode = findViewById(R.id.edtPostalCode);
        edtDetails = findViewById(R.id.edtDetails);

        userService = ApiUtils.getUserService();

        // set spinner values
        String[] arraySpinner = new String[] {
                "Social Event", "Workshop", "Ordinary", "Invitation"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String type = edtType.getText().toString();
                String start = edtStart.getText().toString();
                String end = edtEnd.getText().toString();
                String description = edtDescription.getText().toString();
                String allw = edtAllw.getText().toString();

                // Place attributes
                String town = edtTown.getText().toString();
                String country = edtCountry.getText().toString();
                String address = edtAddress.getText().toString();
                String postalCode = edtPostalCode.getText().toString();
                String details = edtDetails.getText().toString();

                // adding properties to json for POST
                JsonObject json = new JsonObject();

                String role = s.getSelectedItem().toString();

                json.addProperty("name", name);
                json.addProperty("type", type);
                json.addProperty("start", start);
                json.addProperty("end", end);
                json.addProperty("role", role);
                json.addProperty("requirements", description);
                json.addProperty("allowedParticipants", Integer.valueOf(allw));

                JsonObject jsonPlace = new JsonObject();
                jsonPlace.addProperty("town", town);
                jsonPlace.addProperty("country", country);
                jsonPlace.addProperty("address", address);
                jsonPlace.addProperty("postalCode", postalCode);
                jsonPlace.addProperty("details", details);

                Intent myIntent = getIntent();
                String idConference = myIntent.getExtras().get("idConference").toString();

                json.addProperty("conference", idConference);

                createEvent(json, jsonPlace);
            }
        });
    }

    private void createPlace(final JsonObject jsonPlace, String idPlace, final String idConference){
        Call<Place> call = userService.createPlace(jsonPlace, idPlace);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {

                Intent intent = new Intent(CreateEventActivity.this, ShowEventsOfConferenceActivity.class);
                intent.putExtra("idConference", idConference);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(CreateEventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createEvent(final JsonObject json, final JsonObject jsonPlace){
        Call<Event> call = userService.createEvent(json);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if(response.isSuccessful()){

                    Event event = response.body();

                    createPlace(jsonPlace, event.getId(), event.getConference());

                } else {
                    Toast.makeText(CreateEventActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(CreateEventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
