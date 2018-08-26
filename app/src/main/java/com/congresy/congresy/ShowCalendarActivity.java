package com.congresy.congresy;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.EventListOrganizatorAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowCalendarActivity extends BaseActivity {

    CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sets the main layout of the activity
        setContentView(R.layout.activity_show_calendar);

        loadData();

        //initializes the calendarview
        initializeCalendar();
    }

    public void initializeCalendar() {
        calendar = (CalendarView) findViewById(R.id.calendar);

        // sets whether to show the week number.
        calendar.setShowWeekNumber(false);

        // sets the first day of week according to Calendar.
        // here we set Monday as the first day of the Calendar
        calendar.setFirstDayOfWeek(2);

        //The background color for the selected week.
        calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.green));

        //sets the color for the dates of an unfocused month.
        calendar.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));

        //sets the color for the separator line between weeks.
        calendar.setWeekSeparatorLineColor(getResources().getColor(R.color.transparent));

        //sets the color for the vertical bar shown at the beginning and at the end of the selected date.
        calendar.setSelectedDateVerticalBar(R.color.darkgreen);
    }

    public void showAlertDialogButtonClicked(String clicked, List<Event> events) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(clicked);

        final ArrayAdapter<Event> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, events);

        builder.setAdapter(itemsAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = itemsAdapter.getItem(which).getName();
                AlertDialog.Builder builderInner = new AlertDialog.Builder(ShowCalendarActivity.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadData(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String idActor = sp.getString("Id", "not found");

        Call<List<Event>> call = ApiUtils.getUserService().getOwnEvents(idActor);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, final Response<List<Event>> response) {

                final List<Event> res = new ArrayList<>();

                //sets the listener to be notified upon selected date change.
                calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    //show the selected date as a toast
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int day) {

                        for (Event e : response.body()){
                            if (e.getStart().substring(0, 10).equals(day + "/" + month + "/" + year)
                                    ||  e.getEnd().substring(0, 10).equals(day + "/" + month + "/" + year)){
                                res.add(e);
                            }
                        }

                        showAlertDialogButtonClicked(String.valueOf(day + "/" + month + "/" + year), res);

                    }
                });

            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(ShowCalendarActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
