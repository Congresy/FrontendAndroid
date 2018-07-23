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
import android.widget.Toast;

import com.congresy.congresy.EditEventActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowEventsOfConferenceActivity;
import com.congresy.congresy.ShowSpeakersOfEventActivity;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListOrganizatorAdapter extends BaseAdapter implements ListAdapter {

    public static Event event_;
    private UserService userService = ApiUtils.getUserService();
    public static List<Actor> speakers;

    private List<Event> items;
    private Context context;

    public EventListOrganizatorAdapter(Context context, List<Event> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Event getItem(int pos) {
        return items.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        event_ = items.get(position);

        final ViewHolder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.event_list_organizator, null);
            holder.name = convertView.findViewById(R.id.name);
            holder.speakers = convertView.findViewById(R.id.speakers);
            holder.edit = convertView.findViewById(R.id.btnEditEvent);
            holder.delete = convertView.findViewById(R.id.btnDeleteEvent);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(items.get(position).getName());

        holder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                deleteEvent(items.get(position).getId());
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, EditEventActivity.class);
                myIntent.putExtra("role", items.get(position).getRole());
                myIntent.putExtra("idEvent", items.get(position).getId());
                context.startActivity(myIntent);
            }
        });

        holder.speakers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowSpeakersOfEventActivity.class);
                intent.putExtra("idEvent", items.get(position).getId());
                intent.putExtra("comeFrom", "organizator");
                context.startActivity(intent);
            }
        });


        return convertView;
    }

    private void deleteEvent(String idEvent){
        final String idConference = event_.getConference();
        Call call = userService.deleteEvent(idEvent);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(context, ShowEventsOfConferenceActivity.class);
                    intent.putExtra("idConference", idConference);
                    context.startActivity(intent);

                } else {
                    Toast.makeText(context.getApplicationContext(), "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*private void getSpeakers(final int position){
        Call<List<Actor>> call = userService.getAllActorsByRole("Speaker");
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {
                if(response.isSuccessful()){

                    speakers = response.body();

                    Intent intent = new Intent(context, SearchSpeakersActivity.class);
                    intent.putExtra("idEvent", items.get(position).getId());
                    context.startActivity(intent);


                } else {
                    Toast.makeText(context.getApplicationContext(), "There are no speakers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    static class ViewHolder {
        TextView name;
        Button speakers;
        Button edit;
        Button delete;
    }
}
