package com.congresy.congresy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.congresy.congresy.remote.ApiUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        if(role.equals("Administrator")){
            osArray.add("Announcements");
            osArray.add("Actors");
            osArray.add("Conferences");
            osArray.add("Posts");
            osArray.add("Comments");
            osArray.add("Messages");
        }

        if(role.equals("User") || role.equals("Speaker")){
            osArray.add("Home");
            osArray.add("Profile");
            osArray.add("Social");
            osArray.add("All conferences");
            osArray.add("My conferences");
            osArray.add("My events");
            osArray.add("Forum");
            osArray.add("Messages");
            osArray.add("My calendar");
        }

        if(role.equals("Organizator")){
            osArray.add("Profile");
            osArray.add("Social");
            osArray.add("My conferences");
            osArray.add("Create conference");
            osArray.add("Forum");
            osArray.add("Messages");
            osArray.add("My calendar");
        }

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(BaseActivity.this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.bringToFront();

        if(role.equals("Administrator")) {
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        Intent intent = new Intent(BaseActivity.this, ShowAllAnnouncementsActivity.class);
                        startActivity(intent);
                    } else if (position == 1) {
                        Intent intent = new Intent(BaseActivity.this, AdministrationActorsActivity.class);
                        startActivity(intent);
                    } else if (position == 2) {
                        Intent intent = new Intent(BaseActivity.this, AdministrationConferencesActivity.class);
                        startActivity(intent);
                    } else if (position == 3) {
                        Intent intent = new Intent(BaseActivity.this, AdministrationPostsActivity.class);
                        startActivity(intent);
                    }  else if (position == 4) {
                        Intent intent = new Intent(BaseActivity.this, AdministrationCommentsActivity.class);
                        startActivity(intent);
                    } else if (position == 5) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyFoldersActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

        if(role.equals("User") || role.equals("Speaker")) {
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        Intent intent = new Intent(BaseActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } else if (position == 1) {
                        Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    } else if (position == 2) {
                        Intent intent = new Intent(BaseActivity.this, SocialActivity.class);
                        startActivity(intent);
                    } else if (position == 3) {
                        Intent intent = new Intent(BaseActivity.this, ShowAllConferencesActivity.class);
                        startActivity(intent);
                    } else if (position == 4) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyConferencesActivity.class);
                        startActivity(intent);
                    } else if (position == 5) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyEventsActivity.class);
                        startActivity(intent);
                    } else if (position == 6) {
                        Intent intent = new Intent(BaseActivity.this, ShowAllPostsActivity.class);
                        startActivity(intent);
                    } else if (position == 7) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyFoldersActivity.class);
                        startActivity(intent);
                    } else if (position == 8) {
                        Intent intent = new Intent(BaseActivity.this, ShowCalendarActivity.class);
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
                        Intent intent = new Intent(BaseActivity.this, SocialActivity.class);
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
                    } else if (position == 6) {
                        Intent intent = new Intent(BaseActivity.this, ShowCalendarActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    public DatePickerDialog.OnDateSetListener setDatePicker(final EditText editText, final Calendar myCalendar){

        return new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("es", "ES"));

                editText.setText(sdf.format(myCalendar.getTime()));
            }

        };
    }

    public TimePickerDialog.OnTimeSetListener setTimePicker(final EditText editText, final Calendar myCalendar){

        return new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);

                String myFormat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("es", "ES"));

                editText.setText(sdf.format(myCalendar.getTime()));
            }

        };
    }

    public static Boolean checkString(String action, String string, EditText editText, Integer length){

        switch (action) {
            case "length":
                if (string.length() > length) {
                    editText.setError("The maximum length of this field is " + String.valueOf(length));
                    return true;
                }
                break;
            case "blank":
                if (string == null || string.codePointCount(0, string.length()) == 0) {
                    editText.setError("This field can not be blank");
                    return true;
                }
                break;
            case "both":
                if (string == null || string.codePointCount(0, string.length()) == 0) {
                    editText.setError("This field can not be blank");
                    return true;
                } else if (string.codePointCount(0, string.length()) > length) {
                    editText.setError("The maximum length of this field is " + String.valueOf(length));
                    return true;
                }
                break;
        }

        return false;
    }

    public static Boolean checkPasswordsAndEmails(String pas1, String pas2, EditText e1, EditText e2, String em1, String em2, EditText e3, EditText e4){

        if (!pas1.equals(pas2) && !em1.equals(em2)) {
            e1.setError("Both passwords must be the same");
            e2.setError("Both passwords must be the same");
            e3.setError("Both emails must be the same");
            e4.setError("Both emails must be the same");
            return true;
        }else if (!pas1.equals(pas2)){
           e1.setError("Both passwords must be the same");
           e2.setError("Both passwords must be the same");
           return true;
       } else if (!em1.equals(em2)) {
            e3.setError("Both emails must be the same");
            e4.setError("Both emails must be the same");
            return true;
        }

        return false;
    }

    public Boolean checkDateTime(String string, String string1){

        if (parseDate(string.substring(0,10)).compareTo(new Date()) <= 0){
            return true;
        } else if (parseTime(string).compareTo( parseTime(string1)) >= 0){
            return true;
        }

        return false;
    }

    public Boolean checkDate(String string, String string1){

        if (parseDate(string.substring(0,10)).compareTo(new Date()) <= 0){
            return true;
        } else if (parseDate(string).compareTo(parseDate(string1)) > 0) {
            return true;
        }

        return false;
    }

    public Boolean checkInteger(String action, String string, EditText editText){

        if (string.codePointCount(0, string.length()) != 0) {
            switch (action) {
                case "length":
                    if (Integer.valueOf(string) <= 0) {
                        editText.setError("The minimum length of this field is 1");
                        return true;
                    }
                    break;
                case "blank":
                    if (String.valueOf(string).codePointCount(0, string.length()) == 0) {
                        editText.setError("This field can not be blank");
                        return true;
                    }
                    break;
                case "both":
                    if (String.valueOf(string).codePointCount(0, string.length()) == 0) {
                        editText.setError("This field can not be blank");
                        return true;
                    } else if (Integer.valueOf(string) <= 0) {
                        editText.setError("The minimum length of this field is 1");
                        return true;
                    }
                    break;
            }
        } else {
            editText.setError("This field can not be blank");
            return true;
        }


        return false;
    }

    public Boolean checkDouble(String action, String string, EditText editText){

        if (string.codePointCount(0, string.length()) != 0) {
            switch (action) {
                case "length":
                    if (Double.valueOf(string) < 0) {
                        editText.setError("The minimum length of this field is 0.0");
                        return true;
                    }
                    break;
                case "blank":
                    if (String.valueOf(string).codePointCount(0, string.length()) == 0) {
                        editText.setError("This field can not be blank");
                        return true;
                    }
                    break;
                case "both":
                    if (String.valueOf(string).codePointCount(0, string.length()) == 0) {
                        editText.setError("This field can not be blank");
                        return true;
                    } else if (Double.valueOf(string) < 0) {
                        editText.setError("The minimum length of this field is 0.0");
                        return true;
                    }
                    break;
            }
        } else {
            editText.setError("This field can not be blank");
            return true;
        }

        return false;
    }

    public Date parseDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));
        try {
            Date res = format.parse(date);
            System.out.println(date);
            return res;
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public Date parseTime(String date){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("es", "ES"));
        try {
            Date res = format.parse(date);
            System.out.println(date);
            return res;
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }


    public static Boolean checkUrl(String string, EditText editText){

        if (!URLUtil.isValidUrl(string)){
            editText.setError("This field must be an url");
            return true;
        }

        return false;
    }

    public static Boolean checkEmail(String string, EditText editText){

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(string).matches()){
            editText.setError("This field must be a valid email");
            return true;
        }

        return false;
    }

    public static Boolean checkPhone(String string, EditText editText){
        String expression = "^(?:(?:00|\\+)\\d{2}|0)[1-9](?:\\d{8})$";

        if (!string.matches(expression)){
            editText.setError("This field must be a valid phone with the corresponding country prefix");
            return true;
        }

        return false;
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
