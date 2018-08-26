package com.congresy.congresy.remote;

import android.content.Context;
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

    private final int color;
    private final HashSet<CalendarDay> dates;
    private Drawable drawable;
    private Context context;
    private List<Event> events;

    public DayViewDecorator(Context context, int color, Collection<CalendarDay> dates, List<Event> events) {
        this.context = context;
        this.color = color;
        this.dates = new HashSet<>(dates);
        this.events = new ArrayList<>(events);
        drawable = ContextCompat.getDrawable(context, R.drawable.circle_backgorund_calendar);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return !events.isEmpty();
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(color));
        view.setSelectionDrawable(drawable);
    }
}
