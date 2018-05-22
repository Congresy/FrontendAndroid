package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

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
    EditText edtRole;

    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        edtRole = findViewById(R.id.edtRole);

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
                String role = edtRole.getText().toString();

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
                jsonActor.addProperty("photo", photo);
                jsonActor.addProperty("nick", nick);
                jsonActor.addProperty("role", role);

                json.add("actor", jsonActor);
                json.add("userAccount", jsonAuth);


                //validate form
                if(validateRegister(username, password, name, surname, email, phone, place, photo, nick, role)){
                    doRegister(json);
                }
            }
        });
    }

    private boolean validateRegister(String username, String password, String name, String surname, String email, String phone, String place, String photo, String nick, String role){ //TODO
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0 || name == null || name.trim().length() == 0 ||
                surname == null || surname.trim().length() == 0 || email == null || email.trim().length() == 0 ||
                phone == null || phone.trim().length() == 0 || place == null || place.trim().length() == 0 ||
                photo== null || photo.trim().length() == 0 ||
                nick == null || nick.trim().length() == 0 || role== null || role.trim().length() == 0){
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doRegister(final JsonObject json){
        Call call = userService.register(json);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);

                } else {
                    Toast.makeText(RegisterActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
