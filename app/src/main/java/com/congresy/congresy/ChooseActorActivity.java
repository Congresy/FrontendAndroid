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
import android.widget.Toast;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Message;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseActorActivity extends BaseActivity {

    UserService userService;

    Button continue_;
    Button choose;

    String idReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawer(R.layout.activity_choose_actor);

        continue_ = findViewById(R.id.continue_);
        choose = findViewById(R.id.choose);

        userService = ApiUtils.getUserService();

        choose.setVisibility(View.VISIBLE);
        continue_.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        String aux = intent.getExtras().get("received").toString();

        if (aux.equals("1")) {
            choose.setVisibility(View.GONE);
        } else if (aux.equals("0")) {
            continue_.setVisibility(View.GONE);
        }

        continue_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = getIntent();
                String idReceiver_ = intent1.getExtras().get("idReceiver").toString();

                Intent intent = new Intent(ChooseActorActivity.this, CreateMessageActivity.class);
                intent.putExtra("idReceiver", idReceiver_);
                intent.putExtra("comeFrom", "create");
                intent.putExtra("received", "1");
                startActivity(intent);

            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ChooseActorActivity.this);
                LayoutInflater inflater = LayoutInflater.from(ChooseActorActivity.this);
                View view1 = inflater.inflate(R.layout.actors_custom_dialog, null);
                ListView listView = view1.findViewById(R.id.listView);
                SearchView searchView = view1.findViewById(R.id.searchView);


                getAllActors(listView, searchView, view1, dialog);
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

                final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(ChooseActorActivity.this, android.R.layout.simple_list_item_1, arraySpinner);
                listView.setAdapter(stringArrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        idReceiver = (aux.get(position).getId());
                        Intent intent = new Intent(ChooseActorActivity.this, ChooseActorActivity.class);
                        intent.putExtra("idReceiver", aux.get(position).getId());
                        intent.putExtra("received", "1");
                        startActivity(intent);
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
                Toast.makeText(ChooseActorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
