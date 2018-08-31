package com.congresy.congresy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    private Integer aux = 0;

    Button btnCreateConference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Conference creation");

        loadDrawer(R.layout.activity_create_conference);

        final SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        username = sp.getString("Username", "not found");
        userAccountId = sp.getString("UserAccountId", "not found");

        btnCreateConference = findViewById(R.id.btnCreate);

        edtName = findViewById(R.id.edtName);
        spinner = findViewById(R.id.spinner);
        edtPrice = findViewById(R.id.edtPrice);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtSpeakers = findViewById(R.id.edtSpeakers);
        edtDescription = findViewById(R.id.edtDescription1);
        edtPartic = findViewById(R.id.edtPartic);

        // Pace attributes
        edtTown = findViewById(R.id.edtTown);
        edtCountry = findViewById(R.id.edtCountry);
        edtAddress = findViewById(R.id.edtAddress);
        edtPostalCode = findViewById(R.id.edtPostalCode);
        edtDetails = findViewById(R.id.edtDetails);

        // set spinner values
        String[] arraySpinner = new String[] {
                "General", "Physics", "Future", "Gaming", "Present", "Computer Science", "Engineering", "Medicine", "Sports", "eSports", "Consoles", "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        userService = ApiUtils.getUserService();

        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date1 = setDatePicker(edtStart, myCalendar);
        final DatePickerDialog.OnDateSetListener date2 = setDatePicker(edtEnd, myCalendar);
        edtStart.setFocusable(false);
        edtEnd.setFocusable(false);

        edtStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(CreateConferenceActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edtEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(CreateConferenceActivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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

                if (!price.equals(""))
                    json.addProperty("price", Double.valueOf(price));

                json.addProperty("start", start);
                json.addProperty("end", end);
                json.addProperty("speakersNames", speakers);
                json.addProperty("description", description);

                if (!allowedParticipants.equals(""))
                    json.addProperty("allowedParticipants", Integer.valueOf(allowedParticipants));

                if (!allowedParticipants.equals(""))
                    json.addProperty("seatsLeft", Integer.valueOf(allowedParticipants));

                json.addProperty("organizator", sp.getString("Id", "not found"));

                JsonObject jsonPlace = new JsonObject();
                jsonPlace.addProperty("town", town);
                jsonPlace.addProperty("country", country);
                jsonPlace.addProperty("address", address);
                jsonPlace.addProperty("postalCode", postalCode);
                jsonPlace.addProperty("details", details);

                if (name.equals("") || price.equals("") || description.equals("") || town.equals("") || country.equals("") || address.equals("") || postalCode.equals("") || details.equals("")){
                    if(validate(name, price, start, end, description, allowedParticipants, town, country, address, postalCode, details)){
                        doConference(json, jsonPlace);
                    }
                } else {
                    try {
                        if (checkDate(start, end)) {
                            showAlertDialogButtonClicked();
                        } else {
                            if(validate(name, price, start, end, description, allowedParticipants, town, country, address, postalCode, details)){
                                doConference(json, jsonPlace);
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

    private boolean validate(String name, String price, String start, String end, String description, String allowedParticipants, String town, String country, String address, String postalCode, String details){
        if(checkString("both", name, edtName, 20))
            aux++;

        if(checkDouble("both", price, edtPrice))
            aux++;

        if(checkString("both", description, edtDescription, 300))
            aux++;

        if(checkInteger("both", allowedParticipants, edtPartic))
            aux++;

        if (checkString("both", town, edtTown, 40))
            aux++;

        if (checkString("both", country, edtCountry, 30))
            aux++;

        if (checkString("both", address, edtAddress, 60))
            aux++;

        if (checkString("both", postalCode, edtPostalCode, 15))
            aux++;

        if (checkString("both", details, edtDetails, 70))
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
