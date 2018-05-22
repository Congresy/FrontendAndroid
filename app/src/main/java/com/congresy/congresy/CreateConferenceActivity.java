package com.congresy.congresy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.UserAccount;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.congresy.congresy.remote.ApiUtils.useSession;

public class CreateConferenceActivity extends AppCompatActivity {

    UserService userService;

    public static String userAccount;
    public static String userAccountId;

    EditText edtName;
    EditText edtTheme;
    EditText edtPrice;
    EditText edtStart;
    EditText edtEnd;
    EditText edtSpeakers;
    EditText edtDescription;

    Button btnCreateConference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_conference);

        btnCreateConference = findViewById(R.id.btnCreate);

        edtName = findViewById(R.id.edtName);
        edtTheme = findViewById(R.id.edtTheme);
        edtPrice = findViewById(R.id.edtPrice);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtSpeakers = findViewById(R.id.edtSpeakers);
        edtDescription = findViewById(R.id.edtDescription);

        userService = ApiUtils.getUserService();

        // new LoadUserAccount().execute();

        btnCreateConference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String theme = edtTheme.getText().toString();
                String price = edtPrice.getText().toString();
                String start = edtStart.getText().toString();
                String end = edtEnd.getText().toString();
                String speakers = edtSpeakers.getText().toString();
                String description = edtDescription.getText().toString();

                // adding properties to json for POST
                JsonObject json = new JsonObject();

                json.addProperty("name", name);
                json.addProperty("theme", theme);
                json.addProperty("price", Double.valueOf(price));
                json.addProperty("start", start);
                json.addProperty("end", end);
                json.addProperty("speakersNames", speakers);
                json.addProperty("description", description);



                //validate form
                if(validateRegister(name, theme, price, start, end, speakers, description)){
                    createConference(json);
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

    private void doConference(final JsonObject json){
        Call call = userService.createConference(json);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(CreateConferenceActivity.this, HomeActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(CreateConferenceActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(CreateConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createConference(final JsonObject json){
        Call<UserAccount> call = userService.getUserAccount(LoginActivity.username);
        call.enqueue(new Callback<UserAccount>() {
            @Override
            public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                userAccountId = response.body().getId();

                JsonArray organizators = new JsonArray();
                organizators.add(userAccountId);

                json.add("organizators", organizators);

                doConference(json);
            }

            @Override
            public void onFailure(Call<UserAccount> call, Throwable t) {
                Toast.makeText(CreateConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}