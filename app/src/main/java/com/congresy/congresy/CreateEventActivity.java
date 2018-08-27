package com.congresy.congresy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends BaseActivity {

    UserService userService;

    EditText edtName;
    EditText edtDescription;
    EditText edtStart;
    EditText edtEnd;
    EditText edtStartTime;
    EditText edtEndTime;
    EditText edtAllw;
    Spinner s;

    // Place attributes
    EditText edtTown;
    EditText edtCountry;
    EditText edtAddress;
    EditText edtPostalCode;
    EditText edtDetails;

    Button btnCreate;

    private Integer aux = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_create_event);

        btnCreate = findViewById(R.id.btnCreate);

        edtName = findViewById(R.id.edtName);
        s = findViewById(R.id.spinner);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtStartTime = findViewById(R.id.edtStartTime);
        edtEndTime = findViewById(R.id.edtEndTime);
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

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date1 = setDatePicker(edtStart, myCalendar);
        final DatePickerDialog.OnDateSetListener date2 = setDatePicker(edtEnd, myCalendar);
        final TimePickerDialog.OnTimeSetListener time1 = setTimePicker(edtStartTime, myCalendar);
        final TimePickerDialog.OnTimeSetListener time2 = setTimePicker(edtEndTime, myCalendar);

        edtStart.setFocusable(false);
        edtEnd.setFocusable(false);
        edtStartTime.setFocusable(false);
        edtEndTime.setFocusable(false);

        edtStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(CreateEventActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edtEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(CreateEventActivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new TimePickerDialog(CreateEventActivity.this, time1,
                        myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                        true).show();

            }
        });

        edtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new TimePickerDialog(CreateEventActivity.this, time2,
                        myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                        true).show();

            }
        });


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String start = edtStart.getText().toString();
                String end = edtEnd.getText().toString();
                String startTime = edtStartTime.getText().toString();
                String endTime = edtEndTime.getText().toString();
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
                json.addProperty("start", start + " " + startTime);
                json.addProperty("end", end + " " + endTime);
                json.addProperty("role", role);
                json.addProperty("requirements", description);

                if (!allw.equals(""))
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

                if (name.equals("") || allw.equals("") || description.equals("") || town.equals("") || country.equals("") || address.equals("") || postalCode.equals("") || details.equals("")){
                    if (validate(name, description, allw, town, country, address, postalCode, details)){
                        createEvent(json, jsonPlace);
                    } else {
                        edtName.requestFocus();
                    }
                } else {
                    try {
                        if (checkDateTime(start + " " + startTime, end + " " + endTime)) {
                            showAlertDialogButtonClicked();
                        } else {
                            if (validate(name, description, allw, town, country, address, postalCode, details)){
                                createEvent(json, jsonPlace);
                            } else {
                                edtName.requestFocus();
                            }
                        }
                    } catch (Exception e){
                        showAlertDialogButtonClicked();
                    }
                }
            }
        });
    }

    private boolean validate(String name, String description, String allowedParticipants, String town, String country, String address, String postalCode, String details){
        if(checkString("both", name, edtName, 20))
            aux++;

        if(checkString("both", description, edtDescription, 80))
            aux++;

        if(checkInteger("both", allowedParticipants, edtAllw))
            aux++;

        if(checkString("both", town, edtTown, 20))
            aux++;

        if(checkString("both", country, edtCountry, 20))
            aux++;

        if(checkString("both", address, edtAddress, 30))
            aux++;

        if(checkString("both", postalCode, edtPostalCode, 15))
            aux++;

        if(checkString("both", details, edtDetails, 20))
            aux++;

        return aux == 0;
    }

    public void showAlertDialogButtonClicked() {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention!");
        builder.setMessage("Dates are incorrect or are blank. Re-enter them and check that the start date is not today and that the start date and time are not after (and are not the same) that the end date and time.");

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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

    public void showAlertDialogButtonClickedCustom(String text) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention!");
        builder.setMessage(text);

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
                    if (response.code() == 409) {
                        showAlertDialogButtonClickedCustom("Start and end dates must be placed in the period of time of the conference of this event");
                    } else if (response.code() == 406){
                        showAlertDialogButtonClickedCustom("Participants number (in addition of the rest of the events) can not be greater than conferences one");
                    } else {
                        Toast.makeText(CreateEventActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(CreateEventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
