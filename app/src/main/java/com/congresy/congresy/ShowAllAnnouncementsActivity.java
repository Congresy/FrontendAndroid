package com.congresy.congresy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.AnnouncementListAdapter;
import com.congresy.congresy.adapters.ConferenceListAllAdapter;
import com.congresy.congresy.domain.Announcement;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAllAnnouncementsActivity extends BaseActivity {

    UserService userService;

    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Announcements");
        loadDrawer(R.layout.activity_show_all_announcements);

        userService = ApiUtils.getUserService();

        add = findViewById(R.id.create);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowAllAnnouncementsActivity.this, CreateAnnouncementActivity.class);
                startActivity(intent);
            }
        });

        loadAllAnnouncements();
    }

    private void loadAllAnnouncements(){
        Call<List<Announcement>> call = userService.getAllAnnouncements();
        call.enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {

                final List<Announcement> res = response.body();

                AnnouncementListAdapter adapter = new AnnouncementListAdapter(ShowAllAnnouncementsActivity.this, res);

                ListView lv = findViewById(R.id.listView);
                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ShowAllAnnouncementsActivity.this, ShowAnnouncementActivity.class);
                            intent.putExtra("idAnnouncement", res.get(position).getId());
                            startActivity(intent);
                        }
                    });

            }

            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {
                Toast.makeText(ShowAllAnnouncementsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}
