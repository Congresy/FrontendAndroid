package com.congresy.congresy;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.domain.Announcement;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAnnouncementActivity extends BaseActivity {


    UserService userService;

    EditText pictureE;
    EditText descriptionE;
    EditText titleE;

    TextView pictureT;
    TextView descriptionT;
    TextView titleT;

    Button save;
    Button choose;

    String idConference_;

    private Integer aux = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Announcement creation");

        loadDrawer(R.layout.activity_create_announcement);
        save = findViewById(R.id.save);

        pictureE = findViewById(R.id.pictureE);
        pictureT = findViewById(R.id.picture);
        titleE = findViewById(R.id.nameE);
        titleT = findViewById(R.id.name);
        descriptionT = findViewById(R.id.description);
        descriptionE = findViewById(R.id.descriptionE);
        save = findViewById(R.id.save);
        choose = findViewById(R.id.chooseConference);

        userService = ApiUtils.getUserService();

        try {
            save.setVisibility(View.VISIBLE);
            pictureE.setVisibility(View.VISIBLE);
            pictureT.setVisibility(View.VISIBLE);
            descriptionE.setVisibility(View.VISIBLE);
            descriptionT.setVisibility(View.VISIBLE);
            titleE.setVisibility(View.VISIBLE);
            titleT.setVisibility(View.VISIBLE);
        } catch (Exception e){
            choose.setVisibility(View.VISIBLE);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JsonObject json = new JsonObject();

                String picture = pictureE.getText().toString();
                String title= titleE.getText().toString();
                String description = descriptionE.getText().toString();

                json.addProperty("picture", picture);
                json.addProperty("url", title);
                json.addProperty("idConference", idConference_);
                json.addProperty("description", description);

                if (validate(picture, title, description)){
                    createAnnouncement(json);
                } else {
                    pictureE.requestFocus();
                }

            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(CreateAnnouncementActivity.this);
                LayoutInflater inflater = LayoutInflater.from(CreateAnnouncementActivity.this);
                View view1 = inflater.inflate(R.layout.actors_custom_dialog, null);
                ListView listView = view1.findViewById(R.id.listView);
                SearchView searchView = view1.findViewById(R.id.searchView);


                getAllConferences(listView, searchView, view1, dialog);
            }
        });

    }

    private boolean validate(String picture, String url, String description){

        if(checkString("blank", picture, pictureE, null) || checkUrl(picture, pictureE))
            aux++;

        if (checkString("both", description, descriptionE, 150))
            aux++;

        if(checkString("both", url, titleE, 50))
            aux++;

        return aux == 0;
    }

    private void createAnnouncement(JsonObject json){
        Call<Announcement> call = userService.createAnnouncement(json);
        call.enqueue(new Callback<Announcement>() {
            @Override
            public void onResponse(Call<Announcement> call, Response<Announcement> response) {

                Intent intent = new Intent(CreateAnnouncementActivity.this, ShowAllAnnouncementsActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Announcement> call, Throwable t) {
                Toast.makeText(CreateAnnouncementActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllConferences(final ListView listView, final SearchView searchView, final View view1, final Dialog dialog){
        Call<List<Conference>> call = userService.getAllConferencesDetailedOrderBy("date");
        call.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {

                final List<Conference> conferences = response.body();

                List<String> arraySpinner = new ArrayList<>();

                for (Conference c : conferences){
                    arraySpinner.add(c.getName());
                }

                final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(CreateAnnouncementActivity.this, android.R.layout.simple_list_item_1, arraySpinner);
                listView.setAdapter(stringArrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        idConference_ = conferences.get(position).getId();
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
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                Toast.makeText(CreateAnnouncementActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
