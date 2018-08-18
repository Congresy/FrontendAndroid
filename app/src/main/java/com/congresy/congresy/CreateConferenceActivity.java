package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateConferenceActivity extends BaseActivity {

    UserService userService;

    private String username;
    private String userAccountId;

    EditText edtName;
    EditText edtPrice;
    EditText edtStart;
    EditText edtEnd;
    EditText edtSpeakers;
    EditText edtDescription;
    EditText edtPartic;

    // Place attributes
    EditText edtTown;
    EditText edtCountry;
    EditText edtAddress;
    EditText edtPostalCode;
    EditText edtDetails;

    Spinner spinner;

    Button btnCreateConference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_create_conference);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        username = sp.getString("Username", "not found");
        userAccountId = sp.getString("UserAccountId", "not found");

        btnCreateConference = findViewById(R.id.btnCreate);

        edtName = findViewById(R.id.edtName);
        spinner = findViewById(R.id.spinner);
        edtPrice = findViewById(R.id.edtPrice);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtSpeakers = findViewById(R.id.edtSpeakers);
        edtDescription = findViewById(R.id.edtDescription);
        edtPartic = findViewById(R.id.edtPartic);

        // Pace attributes
        edtTown = findViewById(R.id.edtTown);
        edtCountry = findViewById(R.id.edtCountry);
        edtAddress = findViewById(R.id.edtAddress);
        edtPostalCode = findViewById(R.id.edtPostalCode);
        edtDetails = findViewById(R.id.edtDetails);

        // set spinner values
        String[] arraySpinner = new String[] {
                "General", "Another", "New one"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        userService = ApiUtils.getUserService();

        btnCreateConference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String theme = spinner.getSelectedItem().toString();
                String price = edtPrice.getText().toString();
                String start = edtStart.getText().toString();
                String end = edtEnd.getText().toString();
                String speakers = edtSpeakers.getText().toString();
                String description = edtDescription.getText().toString();
                String allowedParticipants = edtPartic.getText().toString();

                // Place attributes
                String town = edtTown.getText().toString();
                String country = edtCountry.getText().toString();
                String address = edtAddress.getText().toString();
                String postalCode = edtPostalCode.getText().toString();
                String details = edtDetails.getText().toString();

                // adding properties to json for POST
                JsonObject json = new JsonObject();

                json.addProperty("name", name);
                json.addProperty("theme", theme);
                json.addProperty("price", Double.valueOf(price));
                json.addProperty("start", start);
                json.addProperty("end", end);
                json.addProperty("speakersNames", speakers);
                json.addProperty("description", description);
                json.addProperty("allowedParticipants", Integer.valueOf(allowedParticipants));
                json.addProperty("seatsLeft", Integer.valueOf(allowedParticipants));
                json.addProperty("organizator", userAccountId);

                JsonObject jsonPlace = new JsonObject();
                jsonPlace.addProperty("town", town);
                jsonPlace.addProperty("country", country);
                jsonPlace.addProperty("address", address);
                jsonPlace.addProperty("postalCode", postalCode);
                jsonPlace.addProperty("details", details);

                //validate form
                if(validateRegister(name, theme, price, start, end, speakers, description)){
                    doConference(json, jsonPlace);
                }
            }
        });
    }

    private boolean validateRegister(String name, String theme, String price, String start, String end, String speakers, String descripton){ //TODO
        if(name == null || name.trim().length() == 0 || theme == null || theme.trim().length() == 0 ||
                price == null || price.trim().length() == 0 || end == null || end .trim().length() == 0 ||
                start == null || start.trim().length() == 0 ||
                speakers == null || speakers.trim().length() == 0 || descripton == null || descripton.trim().length() == 0){
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doConference(final JsonObject json, final JsonObject jsonPlace){
        Call<Conference> call = userService.createConference(json);
        call.enqueue(new Callback<Conference>() {
            @Override
            public void onResponse(Call<Conference> call, Response<Conference> response) {
                if(response.isSuccessful()){

                    createPlace(jsonPlace, response.body().getId());

                } else {
                    Toast.makeText(CreateConferenceActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Conference> call, Throwable t) {
                Toast.makeText(CreateConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPlace(final JsonObject jsonPlace, String id){
        Call<Place> call = userService.createPlace(jsonPlace, id);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {

                Intent intent = new Intent(CreateConferenceActivity.this, ShowMyConferencesActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(CreateConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
