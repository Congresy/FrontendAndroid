package com.congresy.congresy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.EventListJoinProcessAdapter;
import com.congresy.congresy.adapters.EventListOrganizatorAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import org.joda.time.Interval;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoiningConferenceActivity extends BaseActivity {

    static boolean auxOK = false;

    UserService userService;
    private static List<Event> eventsList;

    Button finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_joining_conference);

        finish = findViewById(R.id.finish);

        userService = ApiUtils.getUserService();

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadEvents();
            }
        });

        loadProcess();
    }


    public void showAlertDialogButtonClicked() {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention!");
        builder.setMessage("You have choosed events that overlaps between them, check again and if all is ok go on and click finish");

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auxOK = true;
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean checkDates(List<Event> eventsList){
        boolean res = false;

        DateTimeFormatter f = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");

        int i = 0;

        if (eventsList.size() > 1) {
            while(i < eventsList.size()){
                LocalDateTime startTime1 = f.parseLocalDateTime(eventsList.get(i).getStart());
                LocalDateTime endTime1 = f.parseLocalDateTime(eventsList.get(i).getEnd());
                LocalDateTime startTime2 = f.parseLocalDateTime(eventsList.get(i+1).getStart());
                LocalDateTime endTime2 = f.parseLocalDateTime(eventsList.get(i+1).getEnd());

                Interval interval1 = new Interval(startTime1.toDateTime(), endTime1.toDateTime());
                Interval interval2 = new Interval(startTime2.toDateTime(), endTime2.toDateTime());

                if(interval1.overlaps(interval2)){
                    res = true;
                    break;
                }
                i++;
            }

        }

        return res;
    }

    private void loadEvents(){
        Call<Actor> call = userService.getActorByUsername(HomeActivity.username);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if(response.isSuccessful()){

                    Actor actor = response.body();

                    executeEvents(actor.getId());

                } else {
                    Toast.makeText(JoiningConferenceActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(JoiningConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void executeEvents(String idActor){
        Intent myIntent = getIntent();
        final String idConference = myIntent.getExtras().get("idConference").toString();

        Call<List<Event>> call = userService.getConferenceEventsUser(idConference, idActor);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if(response.isSuccessful()){

                    List<Event> aux = new ArrayList<>();
                    eventsList = response.body();

                    if(checkDates(eventsList) && !auxOK){
                        showAlertDialogButtonClicked();
                    } else if (auxOK){
                        join();
                    }

                } else {
                    Toast.makeText(JoiningConferenceActivity.this, "This conference have no events!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(JoiningConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProcess(){
        Intent myIntent = getIntent();
        String idConference = myIntent.getExtras().get("idConference").toString();

        Call<List<Event>> call = userService.getConferenceEventsAllAndOrganizator(idConference);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if(response.isSuccessful()){

                    eventsList = response.body();

                    EventListJoinProcessAdapter adapter = new EventListJoinProcessAdapter(getApplicationContext(), eventsList);
                    final ListView lv = findViewById(R.id.listView);

                    lv.setAdapter(adapter);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(JoiningConferenceActivity.this, ShowEventActivity.class);
                            intent.putExtra("idEvent", eventsList.get(position).getId());
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(JoiningConferenceActivity.this, "This conference have no events!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(JoiningConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void join(){
        Call<Actor> call = userService.getActorByUsername(HomeActivity.username);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if(response.isSuccessful()){

                    Actor actor = response.body();

                    execute(actor.getId());

                } else {
                    Toast.makeText(JoiningConferenceActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(JoiningConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void execute(String idActor){

        Intent myIntent = getIntent();
        final String idConference = myIntent.getExtras().get("idConference").toString();
        
        Call call = userService.addParticipant(idConference, idActor);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(JoiningConferenceActivity.this, ShowMyConferencesActivity.class);
                    intent.putExtra("idConference", idConference);
                    startActivity(intent);

                } else {
                    Toast.makeText(JoiningConferenceActivity.this.getApplicationContext(), "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(JoiningConferenceActivity.this.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
