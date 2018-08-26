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

import com.congresy.congresy.ProfileActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowBarcodeActivity;
import com.congresy.congresy.ShowConferenceActivity;
import com.congresy.congresy.ShowEventsOfConferenceAuxActivity;
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
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        userService = ApiUtils.getUserService();
        final ViewHolder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.conference_list_user_own, null);
            holder.name = convertView.findViewById(R.id.name);
            holder.events = convertView.findViewById(R.id.btnShowEvents);
            holder.join = convertView.findViewById(R.id.btnJoin);
            holder.organizator = convertView.findViewById(R.id.organizator);
            holder.ticket = convertView.findViewById(R.id.ticket);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowConferenceActivity.class);
                intent.putExtra("idConference", items.get(position).getId());
                context.startActivity(intent);
            }
        });

        holder.name.setText(items.get(position).getName());

        holder.join.setVisibility(View.GONE);

        holder.events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ShowEventsOfConferenceAuxActivity.class);
                myIntent.putExtra("idConference", items.get(position).getId());
                context.startActivity(myIntent);
            }
        });

        holder.organizator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra("idOrganizator", items.get(position).getOrganizator());
                context.startActivity(myIntent);
            }
        });

        holder.ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ShowBarcodeActivity.class);
                myIntent.putExtra("idConference", items.get(position).getId());
                context.startActivity(myIntent);
            }
        });

        return convertView;

    }

    static class ViewHolder {
        TextView name;
        Button events;
        Button join;
        Button organizator;
        Button ticket;
    }
}