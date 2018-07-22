package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
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

public class EditEventActivity extends BaseActivity {

    UserService userService;

    EditText edtName;
    EditText edtType;
    EditText edtDescription;
    EditText edtStart;
    EditText edtEnd;
    EditText edtPlace;
    Spinner edtRole;

    // Place attributes
    EditText edtTown;
    EditText edtCountry;
    EditText edtAddress;
    EditText edtPostalCode;
    EditText edtDetails;

    Button btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_edit_event);

        btnEdit = findViewById(R.id.btnEdit);

        edtName = findViewById(R.id.edtName);
        edtType = findViewById(R.id.edtType);
        edtRole = findViewById(R.id.spinner);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtPlace = findViewById(R.id.edtPlace);
        edtDescription = findViewById(R.id.edtRequirements);

        // Pace attributes
        edtTown = findViewById(R.id.edtTown);
        edtCountry = findViewById(R.id.edtCountry);
        edtAddress = findViewById(R.id.edtAddress);
        edtPostalCode = findViewById(R.id.edtPostalCode);
        edtDetails = findViewById(R.id.edtDetailsP);

        userService = ApiUtils.getUserService();

        // set spinner values
        String[] arraySpinner = new String[] {
                "Social Event", "Workshop", "Ordinary", "Invitation"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtRole.setAdapter(adapter);

        Intent myIntent = getIntent();
        String nameAux = myIntent.getExtras().get("role").toString();

        int index = 0;
        for (String s : arraySpinner){
            if(s.equals(nameAux)){
                edtRole.setSelection(index);
                break;
            }
            index++;
        }

        getEvent();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String type = edtType.getText().toString();
                String role = edtRole.getSelectedItem().toString();
                String start = edtStart.getText().toString();
                String end = edtEnd.getText().toString();
                String description = edtDescription.getText().toString();

                // Place attributes
                String town = edtTown.getText().toString();
                String country = edtCountry.getText().toString();
                String address = edtAddress.getText().toString();
                String postalCode = edtPostalCode.getText().toString();
                String details = edtDetails.getText().toString();

                // adding properties to json for POST
                JsonObject json = new JsonObject();

                json.addProperty("name", name);
                json.addProperty("type", type);
                json.addProperty("start", start);
                json.addProperty("end", end);
                json.addProperty("role", role);
                json.addProperty("requirements", description);

                JsonObject jsonPlace = new JsonObject();
                jsonPlace.addProperty("town", town);
                jsonPlace.addProperty("country", country);
                jsonPlace.addProperty("address", address);
                jsonPlace.addProperty("postalCode", postalCode);
                jsonPlace.addProperty("details", details);

                editEvent(json, jsonPlace);

            }
        });
    }

    private void editPlace(final JsonObject jsonPlace, String id, final String idConference){
        Call<Place> call = userService.editPlace(jsonPlace, id);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {

                Intent intent = new Intent(EditEventActivity.this, ShowEventsOfConferenceActivity.class);
                intent.putExtra("idConference", idConference);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(EditEventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editEvent(final JsonObject json, final JsonObject jsonPlace){
        Intent myIntent = getIntent();
        String idEvent = myIntent.getExtras().get("idEvent").toString();

        Call<Event> call = userService.editEvent(idEvent, json);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if(response.isSuccessful()){

                    Event event = response.body();

                    editPlace(jsonPlace, event.getPlace(), event.getConference());

                } else {
                    Toast.makeText(EditEventActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(EditEventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPlace(String id){
        Call<Place> call = userService.getPlace(id);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {

                Place place = response.body();

                edtTown.setText(place.getTown());
                edtCountry.setText(place.getCountry());
                edtPostalCode.setText(place.getPostalCode());
                edtAddress.setText(place.getAddress());
                edtDetails.setText(place.getDetails());

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(EditEventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getEvent(){
        Intent myIntent = getIntent();
        String idEvent = myIntent.getExtras().get("idEvent").toString();

        Call<Event> call = userService.getEvent(idEvent);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {

                Event event = response.body();

                edtName.setText(event.getName());
                edtType.setText(event.getType());
                edtStart.setText(event.getStart());
                edtEnd.setText(event.getEnd());
                edtPlace.setText(event.getPlace());
                edtDescription.setText(event.getRequirements());

                getPlace(event.getPlace());
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(EditEventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
