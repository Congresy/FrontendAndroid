package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    protected void loadDrawer(int layout) {

        setContentView(layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout =  findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        setupDrawer();

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String role = sp.getString("Role", "Not found");

        ListView mDrawerList = findViewById(R.id.navList);

        List<String> osArray = new ArrayList<>();

        if(role.equals("User")){
            osArray.add("Profile");
            osArray.add("My social networks");
            osArray.add("All conferences");
            osArray.add("My conferences");
            osArray.add("My events");
            osArray.add("Forum");
            osArray.add("Folders");
        }

        if(role.equals("Organizator")){
            osArray.add("Profile");
            osArray.add("My social networks");
            osArray.add("My conferences");
            osArray.add("Create conference");
            osArray.add("Forum");
            osArray.add("Folders");
        }

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(BaseActivity.this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.bringToFront();

        if(role.equals("User")) {
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    } else if (position == 1) {
                        Intent intent = new Intent(BaseActivity.this, ShowMySocialNetworksActivity.class);
                        startActivity(intent);
                    } else if (position == 2) {
                        Intent intent = new Intent(BaseActivity.this, ShowAllConferencesActivity.class);
                        startActivity(intent);
                    } else if (position == 3) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyConferencesActivity.class);
                        startActivity(intent);
                    } else if (position == 4) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyEventsActivity.class);
                        startActivity(intent);
                    } else if (position == 5) {
                        Intent intent = new Intent(BaseActivity.this, ShowAllPostsActivity.class);
                        startActivity(intent);
                    } else if (position == 6) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyFoldersActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

        if(role.equals("Organizator")) {
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    } else if (position == 1) {
                        Intent intent = new Intent(BaseActivity.this, ShowMySocialNetworksActivity.class);
                        startActivity(intent);
                    } else if (position == 2) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyConferencesActivity.class);
                        startActivity(intent);
                    } else if (position == 3) {
                        Intent intent = new Intent(BaseActivity.this, CreateConferenceActivity.class);
                        startActivity(intent);
                    } else if (position == 4) {
                        Intent intent = new Intent(BaseActivity.this, ShowAllPostsActivity.class);
                        startActivity(intent);
                    } else if (position == 5) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyFoldersActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }


    public void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_slideshow);
        mDrawerToggle.syncState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.index_menu_options_logged, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout(){
        Call call = ApiUtils.getUserService().logout();
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("Username");
                editor.remove("Password");
                editor.remove("Role");
                editor.remove("Name");
                editor.remove("Id");
                editor.putInt("logged", 0);
                editor.apply();

                startActivity(new Intent(BaseActivity.this, IndexActivity.class));
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(BaseActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
