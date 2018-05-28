package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.congresy.congresy.adapters.ConferenceListOrganizatorAdapter;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.UserAccount;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditConferenceActivity extends AppCompatActivity {

    private Conference conference = ConferenceListOrganizatorAdapter.conferece_;

    UserService userService;

    EditText edtName;
    EditText edtTheme;
    EditText edtPrice;
    EditText edtStart;
    EditText edtEnd;
    EditText edtSpeakers;
    EditText edtDescription;
    EditText edtPartic;

    Button btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_conference);

        edtName = findViewById(R.id.edtName);
        edtTheme = findViewById(R.id.edtTheme);
        edtPrice = findViewById(R.id.edtPrice);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtSpeakers = findViewById(R.id.edtSpeakers);
        edtDescription = findViewById(R.id.edtDescription);
        edtPartic = findViewById(R.id.edtPartic);

        userService = ApiUtils.getUserService();

        edtName.setText(conference.getName());
        edtTheme.setText(conference.getTheme());
        edtPrice.setText(String.valueOf(conference.getPrice()));
        edtStart.setText(conference.getStart());
        edtEnd.setText(conference.getEnd());
        edtSpeakers.setText(conference.getSpeakersNames());
        edtDescription.setText(conference.getDescription());
        edtPartic.setText(String.valueOf(conference.getAllowedParticipants()));

        btnEdit = findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String theme = edtTheme.getText().toString();
                String price = edtPrice.getText().toString();
                String start = edtStart.getText().toString();
                String end = edtEnd.getText().toString();
                String speakers = edtSpeakers.getText().toString();
                String description = edtDescription.getText().toString();
                String allowedParticipants = edtPartic.getText().toString();

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


                //validate form
                if(validateRegister(name, theme, price, start, end, speakers, description)){
                    editConference(conference.getId(), json);
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

    private void editConferenceAux(String idConference, final JsonObject json){
        Call call = userService.editConference(idConference, json);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(EditConferenceActivity.this, ShowMyConferencesActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(EditConferenceActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(EditConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editConference(final String idConference, final JsonObject json){
        Call<UserAccount> call = userService.getUserAccount(LoginActivity.username);
        call.enqueue(new Callback<UserAccount>() {
            @Override
            public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {

                String userAccountId = response.body().getId();

                json.addProperty("organizator", userAccountId);

                editConferenceAux(idConference, json);
            }

            @Override
            public void onFailure(Call<UserAccount> call, Throwable t) {
                Toast.makeText(EditConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

