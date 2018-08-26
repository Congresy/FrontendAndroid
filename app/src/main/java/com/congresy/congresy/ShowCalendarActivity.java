package com.congresy.congresy;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.utils.DayViewDecorator;
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

public class ShowCalendarActivity extends BaseActivity {

    MaterialCalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sets the main layout of the activity
        setContentView(R.layout.activity_show_calendar);

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        calendar = findViewById(R.id.calendarView);

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
        loadData();
    }


    public void showAlertDialogButtonClicked(String clicked, final List<Event> events) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(clicked);

        final ArrayAdapter<Event> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, events);

        builder.setAdapter(itemsAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(ShowCalendarActivity.this, ShowEventActivity.class);
                intent.putExtra("idEvent", events.get(which).getId());
                startActivity(intent);

            }
        });

        builder.setPositiveButton("Go back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadData(){
        Toast.makeText(ShowCalendarActivity.this, "Loading upcoming events", Toast.LENGTH_SHORT).show();

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String idActor = sp.getString("Id", "not found");

        Call<List<Event>> call = ApiUtils.getUserService().getOwnEvents(idActor, "all");
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                List<Event> events  = response.body();

                calendar.addDecorator(new DayViewDecorator(getApplicationContext(), events));

                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                Toast.makeText(ShowCalendarActivity.this, "Loading done!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(ShowCalendarActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEventsOfDate(){
        Toast.makeText(ShowCalendarActivity.this, "Loading events of selected date", Toast.LENGTH_SHORT).show();

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        final String idActor = sp.getString("Id", "not found");

        calendar.setOnDateChangedListener(new OnDateSelectedListener() {

           @Override
            public void onDateSelected(@NonNull final MaterialCalendarView materialCalendarView, @NonNull final CalendarDay calendarDay, boolean b) {

               findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

               int month = calendarDay.getMonth() + 1;
               String monthRes;

               int day1 = calendarDay.getDay();
               String dayRes;

               if (String.valueOf(month).length() != 2){
                   monthRes = "0" + String.valueOf(month);
               } else {
                   monthRes = String.valueOf(month);
               }

               if (String.valueOf(day1).length() != 2){
                   dayRes = "0" + String.valueOf(day1);
               } else {
                   dayRes = String.valueOf(day1);
               }


               final String date = String.valueOf(dayRes + "/" + monthRes + "/" + calendarDay.getYear());

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
                        } else {
                            Toast.makeText(ShowCalendarActivity.this, "This date has no events", Toast.LENGTH_SHORT).show();
                        }

                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        Toast.makeText(ShowCalendarActivity.this, "Loading done!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<List<Event>> call, Throwable t) {
                        Toast.makeText(ShowCalendarActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

}
