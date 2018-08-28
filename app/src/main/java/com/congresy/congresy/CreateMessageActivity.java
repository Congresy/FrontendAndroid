package com.congresy.congresy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.domain.Message;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMessageActivity extends BaseActivity {

    UserService userService;

    EditText subjectT;
    EditText bodyT;

    Button btnCreate;

    Button choose;

    String idReceiver;

    LinearLayout ll;

    private Integer aux = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        try {

            final String idConference = intent.getExtras().get("fromConference").toString();

            setTitle("Broadcast message creation");

            loadDrawer(R.layout.activity_create_message);

            btnCreate = findViewById(R.id.btnCreate);

            subjectT = findViewById(R.id.subject);
            bodyT = findViewById(R.id.bodyT);

            btnCreate = findViewById(R.id.btnCreate);

            choose = findViewById(R.id.choose);

            ll = findViewById(R.id.header);
            ll.setVisibility(View.GONE);

            userService = ApiUtils.getUserService();

            btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String subject = subjectT.getText().toString();
                    String body = bodyT.getText().toString();

                    // adding properties to json for POST
                    JsonObject json = new JsonObject();

                    json.addProperty("subject", subject);
                    json.addProperty("body", body);
                    json.addProperty("sentMoment", LocalDateTime.now().toString("dd/MM/yyyy HH:mm"));
                    json.addProperty("senderId", "default");
                    json.addProperty("receiverId", "default");

                    sendMessageToParticipants(json, idConference);
                }
            });

        } catch (NullPointerException e){

            setTitle("Message creation");

            loadDrawer(R.layout.activity_create_message);

            Intent myIntent = getIntent();
            String comeFrom = myIntent.getExtras().get("comeFrom").toString();

            ll = findViewById(R.id.header);
            ll.setVisibility(View.GONE);

            if (comeFrom.equals("create")){
                ll.setVisibility(View.VISIBLE);
            }

            btnCreate = findViewById(R.id.btnCreate);

            subjectT = findViewById(R.id.subject);
            bodyT = findViewById(R.id.bodyT);

            choose = findViewById(R.id.choose);

            btnCreate = findViewById(R.id.btnCreate);

            userService = ApiUtils.getUserService();

            btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String subject = subjectT.getText().toString();
                    String body = bodyT.getText().toString();

                    // adding properties to json for POST
                    JsonObject json = new JsonObject();

                    json.addProperty("subject", subject);
                    json.addProperty("body", body);
                    json.addProperty("sentMoment", LocalDateTime.now().toString("dd/MM/yyyy HH:mm"));
                    json.addProperty("senderId", "default");
                    json.addProperty("receiverId", "default");

                    Intent myIntent = getIntent();
                    String comeFrom = myIntent.getExtras().get("comeFrom").toString();

                    if (validate(subject, body)){
                        if (comeFrom.equals("create")){
                            createMessage(json);
                            ll.setVisibility(View.VISIBLE);
                        } else if (comeFrom.equals("reply")) {
                            reply(json);
                        } else if (comeFrom.equals("create1")){
                            createMessage1(json, myIntent.getStringExtra("idReceiver"));
                        }
                    } else {
                        subjectT.requestFocus();
                    }
                }
            });

        }

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(CreateMessageActivity.this);
                LayoutInflater inflater = LayoutInflater.from(CreateMessageActivity.this);
                View view1 = inflater.inflate(R.layout.actors_custom_dialog, null);
                ListView listView = view1.findViewById(R.id.listView);
                SearchView searchView = view1.findViewById(R.id.searchView);

                getAllActors(listView, searchView, view1, dialog);
            }
        });

    }

    private boolean validate(String subject, String body){
        if(checkString("both", subject, subjectT, 20))
            aux++;

        if(checkString("both", body, bodyT, 80))
            aux++;

        return aux == 0;
    }

    private void sendMessageToParticipants(JsonObject json, String idConference){
        Call<List<Message>> call = userService.createMessageToParticipants(json, idConference);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {

                Intent intent = new Intent(CreateMessageActivity.this, ShowMyConferencesActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(CreateMessageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createMessage(JsonObject json){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Call<Message> call = userService.createMessage(json, id, idReceiver);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                Intent intent = new Intent(CreateMessageActivity.this, ShowMyFoldersActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(CreateMessageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createMessage1(JsonObject json, String id1){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Call<Message> call = userService.createMessage(json, id, id1);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                Intent intent = new Intent(CreateMessageActivity.this, ShowMyFoldersActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(CreateMessageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reply(JsonObject json){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Intent myIntent = getIntent();
        String idMessage = myIntent.getExtras().get("idMessage").toString();

        Call<Message> call = userService.replyMessage(json, idMessage, id);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                Intent intent = new Intent(CreateMessageActivity.this, ShowMyFoldersActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(CreateMessageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllActors(final ListView listView, final SearchView searchView, final View view1, final Dialog dialog){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        final String id = sp.getString("Id", "not found");

        Call<List<Actor>> call = userService.getAllActors();
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {

                final List<Actor> actors = response.body();

                List<String> arraySpinner = new ArrayList<>();

                final List<Actor> aux = new ArrayList<>(actors);

                for (Actor a : actors){
                    if (a.getId().equals(id)) {
                        aux.remove(a);
                    } else {
                        arraySpinner.add(a.getName() + ", " + a.getSurname());
                    }
                }

                final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(CreateMessageActivity.this, android.R.layout.simple_list_item_1, arraySpinner);
                listView.setAdapter(stringArrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        idReceiver = (aux.get(position).getId());
                        dialog.dismiss();
                    }
                });

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String newText) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        stringArrayAdapter.getFilter().filter(newText);
                        return false;
                    }

                });
                dialog.setContentView(view1);
                dialog.show();

            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(CreateMessageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
