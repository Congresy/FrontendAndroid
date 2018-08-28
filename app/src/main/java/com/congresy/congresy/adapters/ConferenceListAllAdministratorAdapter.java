package com.congresy.congresy.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.AdministrationConferencesActivity;
import com.congresy.congresy.ProfileActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowEventsOfConferenceActivity;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Message;
import com.congresy.congresy.remote.ApiUtils;
import com.google.gson.JsonObject;

import org.joda.time.LocalDateTime;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConferenceListAllAdministratorAdapter extends BaseAdapter implements ListAdapter {

    private List<Conference> items;
    private List<Conference> itemsAux;
    private Context context;

    public ConferenceListAllAdministratorAdapter(Context context, List<Conference> items, List<Conference> itemsAux) {
        this.context = context;
        this.items = items;
        this.itemsAux = itemsAux;
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
        final ViewHolder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.conference_list_administrator, null);
            holder.name = convertView.findViewById(R.id.name);
            holder.events = convertView.findViewById(R.id.events);
            holder.author = convertView.findViewById(R.id.author);
            holder.delete = convertView.findViewById(R.id.delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(items.get(position).getName());

        holder.author.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("goingTo", "Organizator");
                intent.putExtra("idOrganizator", items.get(position).getOrganizator());
                context.startActivity(intent);
            }
        });

        holder.events.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ShowEventsOfConferenceActivity.class);
                myIntent.putExtra("idConference", items.get(position).getId());
                context.startActivity(myIntent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConference(items.get(position).getId(), items.get(position).getOrganizator(), items.get(position));
            }
        });

        return convertView;
    }

    private void deleteConference(String idConference, final String idReceiver, final Conference conference){
        Call call = ApiUtils.getUserService().deleteConference(idConference);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                // adding properties to json for POST
                JsonObject json = new JsonObject();

                json.addProperty("body", "The conference with title " + conference.getName() + " has been deleted by and administrator. For further information contact congresy@gmail.com");
                json.addProperty("subject", "An element of yours has been deleted");
                json.addProperty("sentMoment", LocalDateTime.now().toString("dd/MM/yyyy HH:mm"));
                json.addProperty("senderId", "default");
                json.addProperty("receiverId", "default");

                createMessage(json, idReceiver);

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createMessage(JsonObject json, String idReceiver){
        SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Call<Message> call = ApiUtils.getUserService().createMessage(json, id, idReceiver);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                Intent intent = new Intent(context, AdministrationConferencesActivity.class);
                context.startActivity(intent);

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void filter(String charText) {
        charText = charText.toLowerCase();
        items.clear();

        if (charText.length() == 0) {
            items.addAll(itemsAux);
        } else {
            for (Conference c : itemsAux) {
                if (c.getName().toLowerCase().contains(charText) || c.getDescription().toLowerCase().contains(charText) || c.getSpeakersNames().toLowerCase().contains(charText) || c.getStart().contains(charText)) {
                    items.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView name;
        ImageButton events;
        ImageButton author;
        ImageButton delete;
    }

}
