package com.congresy.congresy.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.congresy.congresy.ProfileActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

public class SpeakersOfEventListUserAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService = ApiUtils.getUserService();

    private List<Actor> items;
    private Context context;

    public static List<Actor> speakers;

    public SpeakersOfEventListUserAdapter(Context context, List<Actor> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Actor getItem(int pos) {
        return items.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.speakers_of_event_list_user, null);
            holder.name = convertView.findViewById(R.id.name);
            holder.speakers = convertView.findViewById(R.id.speakers);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(items.get(position).getName() + " " + items.get(position).getSurname());

        holder.speakers.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                intent.putExtra("goingTo", "Speaker");
                intent.putExtra("idSpeaker", items.get(position).getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }


    static class ViewHolder {
        TextView name;
        ImageButton speakers;
    }
}