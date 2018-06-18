package com.congresy.congresy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.congresy.congresy.adapters.EventListOrganizatorAdapter;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEventActivity extends BaseActivity {

    private Event event = EventListOrganizatorAdapter.event_;

    UserService userService;

    EditText edtName;
    EditText edtType;
    EditText edtDescription;
    EditText edtStart;
    EditText edtEnd;
    EditText edtPlace;
    EditText edtRole;

    Button btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_edit_event);

        btnEdit = findViewById(R.id.btnEdit);

        edtName = findViewById(R.id.edtName);
        edtType = findViewById(R.id.edtType);
        edtRole = findViewById(R.id.edtRole);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtPlace = findViewById(R.id.edtPlace);
        edtDescription = findViewById(R.id.edtRequirements);

        userService = ApiUtils.getUserService();

        edtName.setText(event.getName());
        edtType.setText(event.getType());
        edtRole.setText(event.getRole());
        edtStart.setText(event.getStart());
        edtEnd.setText(event.getEnd());
        edtPlace.setText(event.getPlace());
        edtDescription.setText(event.getRequirements());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String type = edtType.getText().toString();
                String role = edtRole.getText().toString();
                String start = edtStart.getText().toString();
                String end = edtEnd.getText().toString();
                String place = edtPlace.getText().toString();
                String description = edtDescription.getText().toString();

                // adding properties to json for POST
                JsonObject json = new JsonObject();

                json.addProperty("name", name);
                json.addProperty("type", type);
                json.addProperty("start", start);
                json.addProperty("end", end);
                json.addProperty("role", role);
                json.addProperty("place", place);
                json.addProperty("requirements", description);

                //validate form
                if(validateRegister(name, description, start, end, role, place)){
                    editEvent(json);
                }
            }
        });
    }

    private boolean validateRegister(String name, String price, String start, String end, String speakers, String descripton){ //TODO
        if(name == null || name.trim().length() == 0 ||
                price == null || price.trim().length() == 0 || end == null || end .trim().length() == 0 ||
                start == null || start.trim().length() == 0 ||
                speakers == null || speakers.trim().length() == 0 || descripton == null || descripton.trim().length() == 0){
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void editEvent(final JsonObject json){
        Call<Event> call = userService.editEvent(event.getId(), json);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if(response.isSuccessful()){

                    Event event = response.body();

                    Intent intent = new Intent(EditEventActivity.this, ShowEventsOfConferenceActivity.class);
                    intent.putExtra("idConference", event.getConference());
                    startActivity(intent);

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

}
