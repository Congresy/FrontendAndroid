package com.congresy.congresy.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.R;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import org.joda.time.Interval;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListJoinProcessAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService = ApiUtils.getUserService();

    private String username;

    private List<Event> items;
    private Context context;
    private List<String> state;

    public EventListJoinProcessAdapter(Context context, List<Event> items) {
        this.context = context;
        this.items = items;
        this.state = new ArrayList<>();
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

        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.event_list_join_process, null);
            holder.name = convertView.findViewById(R.id.name);
            holder.join = convertView.findViewById(R.id.join);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.join.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ImageButton b = (ImageButton) v;

                if (b.getTag().toString().equals("Dismiss")){
                    delete(items.get(position).getId(), holder);
                } else {
                    join(items.get(position).getId(), holder);
                }
            }
        });

        holder.name.setText(items.get(position).getName() + " - " + String.valueOf(items.get(position).getSeatsLeft()) + "\n" +  items.get(position).getStart() + " - " + items.get(position).getEnd());

        return convertView;
    }

    private void join(final String idEvent, final ViewHolder holder){
        SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        username = sp.getString("Username", "not found");

        Call<Actor> call = userService.getActorByUsername(username);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if(response.isSuccessful()){

                    final Actor actor = response.body();

                    execute(idEvent, actor.getId(), holder);

                } else {
                    Toast.makeText(context.getApplicationContext(), "You have no social networks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void execute(final String idEvent, String idActor, final ViewHolder holder){
        Call call = userService.addParticipantToEvent(idEvent, idActor);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    holder.join.setTag("Dismiss");
                    holder.join.setImageResource(R.drawable.baseline_remove_black_18dp);
                    state.add("Dismiss " + idEvent);
                    notifyDataSetChanged();

                    Toast.makeText(context.getApplicationContext(), "Joined to event successfully!", Toast.LENGTH_SHORT).show();
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

    private void delete(final String idEvent, final ViewHolder holder){
        SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        username = sp.getString("Username", "not found");

        Call<Actor> call = userService.getActorByUsername(username);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if(response.isSuccessful()){

                    final Actor actor = response.body();

                    executeDelete(idEvent, actor.getId(), holder);

                } else {
                    Toast.makeText(context.getApplicationContext(), "You have no social networks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void executeDelete(final String idEvent, String idActor, final ViewHolder holder){
        Call call = userService.deleteParticipant(idEvent, idActor);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    holder.join.setTag("Join");
                    holder.join.setImageResource(R.drawable.baseline_remove_black_18dp);
                    state.add("Join " + idEvent);
                    notifyDataSetChanged();

                    Toast.makeText(context.getApplicationContext(), "Deleted from event successfully!", Toast.LENGTH_SHORT).show();
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


    static class ViewHolder {
        TextView name;
        ImageButton join;
    }
}