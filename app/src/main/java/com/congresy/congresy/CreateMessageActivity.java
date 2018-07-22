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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.congresy.congresy.domain.Event;
import com.congresy.congresy.domain.Message;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import org.joda.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMessageActivity extends BaseActivity {

    UserService userService;

    EditText subjectT;
    EditText bodyT;

    Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_create_message);

        btnCreate = findViewById(R.id.btnCreate);

        subjectT = findViewById(R.id.subject);
        bodyT = findViewById(R.id.bodyT);

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

                if (comeFrom.equals("create")){
                    createMessage(json);
                } else if (comeFrom.equals("reply")) {
                    reply(json);
                }

            }
        });

    }

    private void createMessage(JsonObject json){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Intent myIntent = getIntent();
        String idReceiver = myIntent.getExtras().get("idReceiver").toString();

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
}
