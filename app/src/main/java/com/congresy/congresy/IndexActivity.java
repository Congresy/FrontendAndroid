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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Announcement;
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
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IndexActivity extends AppCompatActivity {

    private int conferencesSize;
    private int usersSize;
    private int activeConferences;
    private Announcement announcement_;

    TextView data;

    Button btnLogin;
    Button btnRegister;
    Button go;

    UserService userService;

    ImageView image;
    TextView conferenceE;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Home");

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        int logged = sp.getInt("logged", 0);

        if(logged == 1){

             SharedPreferences sp1 = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                String role_ = sp1.getString("Role", "not found");

                if (role_.equals("Administrator")) {
                    Intent intent = new Intent(IndexActivity.this, AdministrationConferencesActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(IndexActivity.this, HomeActivity.class);
                    startActivity(intent);

            }
        } else {

            setContentView(R.layout.activity_index);

            data = findViewById(R.id.data);

            btnLogin = findViewById(R.id.btnLogin);
            btnRegister = findViewById(R.id.btnRegister);
            image = findViewById(R.id.image);
            conferenceE = findViewById(R.id.conference);
            title= findViewById(R.id.title);
            go = findViewById(R.id.go);

            userService = ApiUtils.getUserService();

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
                    intent.putExtra("fromLogin", 0);
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

            loadData();

            showAnnouncement();

            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

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
                    Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
                    intent.putExtra("fromLogin", 0);
                    startActivity(intent);
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

    private void loadData(){
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        Toast.makeText(IndexActivity.this, "Loading data", Toast.LENGTH_SHORT).show();

        Call<List<Conference>> call = userService.getAllConferences();
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
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    Toast.makeText(IndexActivity.this, "Loading done!", Toast.LENGTH_SHORT).show();

                } else {
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    Toast.makeText(IndexActivity.this, "Wait until the server starts and try again in a moment!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                Toast.makeText(IndexActivity.this, "Wait until the server starts and try again in a moment!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkEndDate(String date) throws ParseException {
        boolean res = true;

        Date today = new Date();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date dateFormated = format.parse(date.substring(0,10));

        if (dateFormated.before(today)){
            res = false;
        }

        return res;
    }

    private void showAnnouncement(){
        Call<List<Announcement>> call = userService.getAllAnnouncements();
        call.enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {

                List<Announcement> announcements = response.body();

                if (announcements.size() > 0){
                    Random rand = new Random();
                    announcement_ = announcements.get(rand.nextInt(announcements.size()));

                    Glide.with(getApplicationContext())
                            .load(announcement_.getPicture()) // Image URL
                            .centerCrop() // Image scale type
                            .crossFade()
                            .override(800,500) // Resize image
                            .into(image); // ImageView to display image

                    conferenceE.setText(announcement_.getDescription());
                    title.setText(announcement_.getUrl());
                }

                go.setText("Go!");

                    go.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showConference(announcement_.getIdConference());
                        }
                    });

            }

            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {
                Toast.makeText(IndexActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConference(final String idConference){
        Call<Conference> call = userService.getConference(idConference);
        call.enqueue(new Callback<Conference>() {
            @Override
            public void onResponse(Call<Conference> call, Response<Conference> response) {

                Conference con = response.body();

                Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
                intent.putExtra("idConference", announcement_.getIdConference());
                intent.putExtra("price", String.valueOf(con.getPrice()));
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Conference> call, Throwable t) {
                Toast.makeText(IndexActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadActor(final String idConference){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String username = sp.getString("Username", "not found");

        Call<Actor> call = userService.getActorByUsername(username);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {

                Actor actor = response.body();

               go.setText("Go!");

                    go.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showConference(announcement_.getIdConference());
                        }
                    });

            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(IndexActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
