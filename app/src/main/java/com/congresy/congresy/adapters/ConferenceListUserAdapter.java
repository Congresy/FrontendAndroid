package com.congresy.congresy.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.congresy.congresy.R;
import com.congresy.congresy.ShowEventsOfConferenceActivity;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

public class ConferenceListUserAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService;

    private List<Conference> items;
    private Context context;

    public ConferenceListUserAdapter(Context context, List<Conference> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Conference getItem(int pos) {
        return items.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return Long.valueOf(items.get(pos).getId());
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        userService = ApiUtils.getUserService();
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.conference_list_user, null);
        }

        TextView listItemText = view.findViewById(R.id.name);
        listItemText.setText(items.get(position).getName());

        Button showEvents = view.findViewById(R.id.btnShowEvents);
        Button joinEvent = view.findViewById(R.id.btnJoin);

        joinEvent.setVisibility(View.GONE);

        showEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ShowEventsOfConferenceActivity.class);
                myIntent.putExtra("idConference", items.get(position).getId());
                context.startActivity(myIntent);
            }
        });

    return view;

    }
}