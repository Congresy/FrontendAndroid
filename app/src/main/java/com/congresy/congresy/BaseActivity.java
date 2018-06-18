package com.congresy.congresy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    protected void loadDrawer(int layout) {

        setContentView(layout);

        ListView mDrawerList = findViewById(R.id.navList);

        List<String> osArray = new ArrayList<>();
        osArray.add("Profile");
        osArray.add("My social networks");
        osArray.add("All conferences");
        osArray.add("My conferences");
        osArray.add("Create conference");

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(BaseActivity.this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.bringToFront();

        if(LoginActivity.role.equals("User")){
            osArray.remove("Create conference");
        }

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else if(position == 1){
                    Intent intent = new Intent(BaseActivity.this, ShowMySocialNetworksActivity.class);
                    startActivity(intent);
                } else if(position == 2) {
                    Intent intent = new Intent(BaseActivity.this, ShowAllConferencesActivity.class);
                    startActivity(intent);
                } else if(position == 3){
                    Intent intent = new Intent(BaseActivity.this, ShowMyConferencesActivity.class);
                    startActivity(intent);
                } else if(position == 4){
                    Intent intent = new Intent(BaseActivity.this, CreateConferenceActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

}
