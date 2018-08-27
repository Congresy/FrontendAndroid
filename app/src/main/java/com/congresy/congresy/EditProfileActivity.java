package com.congresy.congresy;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity {

    UserService userService;

    EditText edtPassword;
    EditText edtPasswordConfirm;
    EditText edtName;
    EditText edtSurname;
    EditText edtEmail;
    EditText edtPhone;
    EditText edtPhoto;

    // Place attributes
    EditText edtTown;
    EditText edtCountry;
    EditText edtAddress;
    EditText edtPostalCode;
    EditText edtDetails;

    Button btnContinue;
    CheckBox private_;

    private Boolean privateAux;
    private Integer aux = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_edit_profile);

        private_ = findViewById(R.id.private_);
        btnContinue = findViewById(R.id.btnContinue);
        edtPassword = findViewById(R.id.edtPassword);
        edtPasswordConfirm = findViewById(R.id.edtPasswordConfirm);
        edtName = findViewById(R.id.edtName);
        edtSurname = findViewById(R.id.edtSurname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPhoto = findViewById(R.id.edtPhoto);

        // Pace attributes
        edtTown = findViewById(R.id.edtTown);
        edtCountry = findViewById(R.id.edtCountry);
        edtAddress = findViewById(R.id.edtAddress);
        edtPostalCode = findViewById(R.id.edtPostalCode);
        edtDetails = findViewById(R.id.edtDetails);

        userService = ApiUtils.getUserService();

        getProfile();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = edtPassword.getText().toString();
                String passwordConfirm = edtPasswordConfirm.getText().toString();
                String name = edtName.getText().toString();
                String surname = edtSurname.getText().toString();
                String email = edtEmail.getText().toString();
                String phone = edtPhone.getText().toString();
                String photo = edtPhoto.getText().toString();

                // Place attributes
                String town = edtTown.getText().toString();
                String country = edtCountry.getText().toString();
                String address = edtAddress.getText().toString();
                String postalCode = edtPostalCode.getText().toString();
                String details = edtDetails.getText().toString();

                // adding properties to json for POST
                JsonObject json = new JsonObject();
                JsonObject jsonActor = new JsonObject();
                JsonObject jsonAuth = new JsonObject();

                jsonAuth.addProperty("username", "default");
                jsonAuth.addProperty("password", password);

                jsonActor.addProperty("private_", privateAux);


                jsonActor.addProperty("name", name);
                jsonActor.addProperty("surname", surname);
                jsonActor.addProperty("email", email);
                jsonActor.addProperty("phone", phone);
                jsonActor.addProperty("role", "User");

                if(!photo.equals("null")){
                    jsonActor.addProperty("photo", photo);
                }

                json.add("actor", jsonActor);
                json.add("userAccount", jsonAuth);

                JsonObject jsonPlace = new JsonObject();
                jsonPlace.addProperty("town", town);
                jsonPlace.addProperty("country", country);
                jsonPlace.addProperty("address", address);
                jsonPlace.addProperty("postalCode", postalCode);
                jsonPlace.addProperty("details", details);

                if (!passwordConfirm.equals("") || !password.equals("")){
                    if (!password.equals(passwordConfirm)) {
                        showAlertDialogButtonClicked("password");
                    }
                } else {
                    if (validate(name, surname, email, phone, photo, town, country, address, postalCode, details, password, passwordConfirm)) {
                        editProfile(json, jsonPlace);
                    } else {
                        edtPassword.requestFocus();
                    }
                }



            }
        });
    }

    private boolean validate(String name, String surname, String email, String phone, String photo, String town, String country, String address, String postalCode, String details, String pas1, String pas2){

        if (checkString("both", name, edtName, 20))
            aux++;

        if (checkString("both", surname, edtSurname, 40))
            aux++;

        if (checkString("blank", email, edtEmail, null) || checkEmail(email, edtEmail))
            aux++;

        if (checkString("blank", phone, edtPhone, null) || checkPhone(phone, edtPhone))
            aux++;

        if (checkString("both", town, edtTown, 20))
            aux++;

        if (checkString("both", country, edtCountry, 20))
            aux++;

        if (checkString("both", address, edtAddress, 30))
            aux++;

        if (checkString("both", postalCode, edtPostalCode, 15))
            aux++;

        if (checkString("both", details, edtDetails, 20))
            aux++;

        if (checkString("blank", pas1, edtPassword, null))
            aux++;

        if (checkString("blank", pas2, edtPasswordConfirm, null))
            aux++;

        if (!photo.equals(""))
            if(checkUrl(photo, edtPhoto) && checkString("blank", photo, edtPhoto, null))
                aux++;

        if (aux == 0){
            if (!pas1.equals(pas2)) {
                edtPasswordConfirm.setError("Both passwords must be the same");
                edtPassword.setError("Both passwords must be the same");
                aux++;
            }
        }

        return aux == 0;

    }

    public void showAlertDialogButtonClicked(String action) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention!");

        if (!action.equals("both")){
            builder.setMessage("Your " + action + " don't match the confirmation");
        } else if (action.equals("both")){
            builder.setMessage("Your password and email don't match the confirmation");
        }

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

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.private_:
                privateAux = checked;
                break;
        }
    }

    private void editProfile(JsonObject json, final JsonObject jsonPlace){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Call<Actor> call = userService.editActor(id, json);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {

                if (response.isSuccessful()){
                    editPlace(jsonPlace, response.body().getPlace());
                }  else {
                    if (response.code() == 409) {
                        Toast.makeText(EditProfileActivity.this, "Username is already taken", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 406){
                        Toast.makeText(EditProfileActivity.this, "Email is already taken", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editPlace(JsonObject jsonPlace, String id){
        Call<Place> call = userService.editPlace(jsonPlace, id);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {

                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProfile(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Call<Actor> call = userService.getActorById(id);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {

                Actor a = response.body();

                edtName.setText(a.getName());
                edtSurname.setText(a.getSurname());
                edtEmail.setText(a.getEmail());
                edtPhone.setText(a.getPhone());
                edtPhoto.setText(a.getPhoto());

                private_.setChecked(a.getPrivate());

                getPlace(a.getPlace());
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPlace(String idPlace){
        Call<Place> call = userService.getPlace(idPlace);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {

                Place p = response.body();

                edtTown.setText(p.getTown());
                edtDetails.setText(p.getDetails());
                edtAddress.setText(p.getAddress());
                edtPostalCode.setText(p.getPostalCode());
                edtCountry.setText(p.getCountry());

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
