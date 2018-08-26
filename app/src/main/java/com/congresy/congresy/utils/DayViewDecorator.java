package com.congresy.congresy.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;

import com.congresy.congresy.R;
import com.congresy.congresy.domain.Event;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class DayViewDecorator implements com.prolificinteractive.materialcalendarview.DayViewDecorator {

    private Drawable drawable;
    private List<Event> events;

    public DayViewDecorator(Context context, List<Event> events) {
        this.events = new ArrayList<>(events);
        drawable = ContextCompat.getDrawable(context, R.drawable.circle_backgorund_calendar);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        int month = day.getMonth() + 1;
        String monthRes;

        int day1 = day.getDay();
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


        String date = String.valueOf(dayRes + "/" + monthRes + "/" + day.getYear());

        for (Event e : events){
            if (e.getStart().substring(0,10).equals(date) || e.getEnd().substring(0,10).equals(date)){
                return true;
            }
        }

        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.RED));

        view.setSelectionDrawable(drawable);
    }
}
