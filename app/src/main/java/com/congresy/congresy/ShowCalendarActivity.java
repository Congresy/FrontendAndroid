package com.congresy.congresy;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.congresy.congresy.adapters.EventListOrganizatorAdapter;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowCalendarActivity extends BaseActivity  implements OnDateSelectedListener {

    MaterialCalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sets the main layout of the activity
        setContentView(R.layout.activity_show_calendar);

        calendar = findViewById(R.id.calendarView);

        calendar.setOnDateChangedListener(this);
        calendar.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        Calendar instance = Calendar.getInstance();
        calendar.setSelectedDate(instance);

        calendar.state().edit()
                .setMinimumDate(CalendarDay.from(1990, 1, 1))
                .setMaximumDate(CalendarDay.from(2100, 12, 31))
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        loadEventsOfDate();
    }


    public void showAlertDialogButtonClicked(String clicked, List<Event> events) {

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

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadEventsOfDate(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        final String idActor = sp.getString("Id", "not found");

        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
           @Override
            public void onDateSelected(@NonNull final MaterialCalendarView materialCalendarView, @NonNull final CalendarDay calendarDay, boolean b) {

                final int month = calendarDay.getMonth() + 1;
                final String date = String.valueOf(calendarDay.getDay() + "/" + month + "/" + calendarDay.getYear());

                Call<List<Event>> call = ApiUtils.getUserService().getOwnEvents(idActor, date);
                call.enqueue(new Callback<List<Event>>() {
                    @Override
                    public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                        List<Event> res = response.body();
                        List<Event> aux = new ArrayList<>(response.body());

                        for (Event e : aux){
                            if (e.getStart().substring(0, 10).equals(date) ||  e.getEnd().substring(0, 10).equals(date)){
                                res.add(e);
                            }
                        }

                        if (!res.isEmpty()){
                            materialCalendarView.setDateTextAppearance(Color.RED);
                            showAlertDialogButtonClicked(date, res);
                        }

                    }

                    @Override
                    public void onFailure(Call<List<Event>> call, Throwable t) {
                        Toast.makeText(ShowCalendarActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {

    }

}
