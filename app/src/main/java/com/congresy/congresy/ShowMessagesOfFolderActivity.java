package com.congresy.congresy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.EventListOrganizatorAdapter;
import com.congresy.congresy.adapters.MessagesListAdapter;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.domain.Message;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMessagesOfFolderActivity extends BaseActivity {

    UserService userService;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Messages");
        loadDrawer(R.layout.activity_show_messages_of_folder);

        userService = ApiUtils.getUserService();

        lv = findViewById(R.id.listView);

        loadMessages();
    }

    private void loadMessages(){
        Intent myIntent = getIntent();
        String idFolder = myIntent.getExtras().get("idFolder").toString();

        Call<List<Message>> call = userService.getMessagesOfFolder(idFolder);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if(response.isSuccessful()){

                    List<Message> messages = response.body();

                    MessagesListAdapter adapter = new MessagesListAdapter(getApplicationContext(), messages);

                    lv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(ShowMessagesOfFolderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
