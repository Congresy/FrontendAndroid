package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.RetrofitClient;
import com.congresy.congresy.remote.UserService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IndexActivity extends AppCompatActivity {

    private int conferencesSize;
    private int usersSize;
    private int activeConferences;

    TextView data;

    Button btnLogin;
    Button btnRegister;

    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        int logged = sp.getInt("logged", 0);

        if(logged == 1){
            Intent intent = new Intent(IndexActivity.this, HomeActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_index);

        data = findViewById(R.id.data);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        userService = ApiUtils.getUserService();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
                startActivity(intent);
                }
        });
        
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, RegisterActivity.class);
                startActivity(intent);
                }
        });

        LoadData();

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
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getAllUsers(){
        Call<List<Actor>> call = userService.getAllUsers();
        call.enqueue(new Callback<List<Actor>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {
                if(response.isSuccessful()){

                    List<Actor> body = response.body();
                    usersSize = body.size();

                    data.setText(conferencesSize + " conferences registed in our database\n" + activeConferences + " active conferences\n" + usersSize + " users using our app");

                } else {
                    Toast.makeText(IndexActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(IndexActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadData(){
        Call<List<Conference>> call = userService.getAllConferencesDetailedOrderByDate();
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if(response.isSuccessful()){

                    List<Conference> body = response.body();
                    conferencesSize = body.size();

                    for (Conference c : body){
                        try {
                            if(checkEndDate(c.getEnd())){
                                activeConferences++;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    getAllUsers();

                } else {
                    Toast.makeText(IndexActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(IndexActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkEndDate(String date) throws ParseException {
        boolean res = true;

        Date today = new Date();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        Date dateFormated = format.parse(date);

        if (dateFormated.before(today)){
            res = false;
        }

        return res;
    }
}
