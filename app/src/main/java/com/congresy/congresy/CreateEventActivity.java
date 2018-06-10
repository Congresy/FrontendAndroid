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
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {

    UserService userService;

    EditText edtName;
    EditText edtType;
    EditText edtDescription;
    EditText edtStart;
    EditText edtEnd;
    EditText edtPlace;
    Spinner s;

    Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        btnCreate = findViewById(R.id.btnCreate);

        edtName = findViewById(R.id.edtName);
        edtType = findViewById(R.id.edtType);
        s = findViewById(R.id.spinner);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtPlace = findViewById(R.id.edtPlace);
        edtDescription = findViewById(R.id.edtRequirements);

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
                String place = edtPlace.getText().toString();
                String description = edtDescription.getText().toString();

                // adding properties to json for POST
                JsonObject json = new JsonObject();

                String role = s.getSelectedItem().toString();

                json.addProperty("name", name);
                json.addProperty("type", type);
                json.addProperty("start", start);
                json.addProperty("end", end);
                json.addProperty("role", role);
                json.addProperty("place", place);
                json.addProperty("requirements", description);

                Intent myIntent = getIntent();
                String idConference = myIntent.getExtras().get("idConference").toString();

                json.addProperty("conference", idConference);


                //validate form
                if(validateRegister(name, type, description, start, end, role, place)){
                    createEvent(json);
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

    private void createEvent(final JsonObject json){
        Call<Event> call = userService.createEvent(json);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if(response.isSuccessful()){

                    Event event = response.body();

                    Intent intent = new Intent(CreateEventActivity.this, ShowEventsOfConferenceActivity.class);
                    intent.putExtra("idConference", event.getConference());
                    startActivity(intent);

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
