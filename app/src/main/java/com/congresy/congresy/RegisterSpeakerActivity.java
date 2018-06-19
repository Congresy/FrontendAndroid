package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterSpeakerActivity extends BaseActivity {

    UserService userService;

    EditText edtUsername;
    EditText edtPassword;
    EditText edtName;
    EditText edtSurname;
    EditText edtEmail;
    EditText edtPhone;
    EditText edtPlace;
    EditText edtPhoto;
    EditText edtNick;

    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_speaker);

        btnRegister = findViewById(R.id.btnRegister);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtName = findViewById(R.id.edtName);
        edtSurname = findViewById(R.id.edtSurname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPlace = findViewById(R.id.edtPlace);
        edtPhoto = findViewById(R.id.edtPhoto);
        edtNick = findViewById(R.id.edtNick);

        userService = ApiUtils.getUserService();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                String name = edtName.getText().toString();
                String surname = edtSurname.getText().toString();
                String email = edtEmail.getText().toString();
                String phone = edtPhone.getText().toString();
                String place = edtPlace.getText().toString();
                String photo = edtPhoto.getText().toString();
                String nick = edtNick.getText().toString();

                // adding properties to json for POST
                JsonObject json = new JsonObject();
                JsonObject jsonActor = new JsonObject();
                JsonObject jsonAuth = new JsonObject();

                jsonAuth.addProperty("username", username);
                jsonAuth.addProperty("password", password);

                jsonActor.addProperty("name", name);
                jsonActor.addProperty("surname", surname);
                jsonActor.addProperty("email", email);
                jsonActor.addProperty("phone", phone);
                jsonActor.addProperty("place", place);
                if(!photo.equals("null")){
                    jsonActor.addProperty("photo", photo);
                }
                jsonActor.addProperty("nick", nick);

                jsonActor.addProperty("role", "Speaker");

                json.add("actor", jsonActor);
                json.add("userAccount", jsonAuth);

                doRegister(json);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.index_menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                startActivity(new Intent(this, RegisterSpeakerActivity.class));
                return true;
            case R.id.register:
                startActivity(new Intent(this, RegisterSpeakerActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doRegister(final JsonObject json){
        Call<Actor> call = userService.register(json);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if(response.isSuccessful()){

                    Actor actor = response.body();
                    
                    addSpeaker(actor.getId());

                } else {
                    Toast.makeText(RegisterSpeakerActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(RegisterSpeakerActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSpeaker(String idActor){
        Intent myIntent = getIntent();
        String idEvent = myIntent.getExtras().get("idEvent").toString();
        
        Call<Event> call = userService.addSpeaker(idEvent, idActor);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(RegisterSpeakerActivity.this, ShowMyConferencesActivity.class);
                    startActivity(intent);


                } else {
                    Toast.makeText(RegisterSpeakerActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(RegisterSpeakerActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
