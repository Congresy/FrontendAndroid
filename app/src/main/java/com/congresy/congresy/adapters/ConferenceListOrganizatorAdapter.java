package com.congresy.congresy.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.CreateMessageActivity;
import com.congresy.congresy.EditConferenceActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowConferenceActivity;
import com.congresy.congresy.ShowEventsOfConferenceActivity;
import com.congresy.congresy.ShowMyConferencesActivity;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConferenceListOrganizatorAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService = ApiUtils.getUserService();

    private List<Conference> items;
    private Context context;

    public ConferenceListOrganizatorAdapter(Context context, List<Conference> items) {
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

            convertView = inflater.inflate(R.layout.conference_list_organizator, null);
            holder.name = convertView.findViewById(R.id.name);
            holder.events = convertView.findViewById(R.id.btnShowEvents);
            holder.edit = convertView.findViewById(R.id.btnEditConference);
            holder.delete = convertView.findViewById(R.id.btnDeleteConference);
            holder.message = convertView.findViewById(R.id.btnMessage);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowConferenceActivity.class);
                intent.putExtra("idConference", items.get(position).getId());
                intent.putExtra("comeFrom", "organizator");
                context.startActivity(intent);
            }
        });

        holder.name.setText(items.get(position).getName());

        holder.events.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context.getApplicationContext(), ShowEventsOfConferenceActivity.class);
                myIntent.putExtra("idConference", items.get(position).getId());
                context.startActivity(myIntent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                deleteConference(items.get(position).getId());
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, EditConferenceActivity.class);
                myIntent.putExtra("idConference", items.get(position).getId());
                context.startActivity(myIntent);
            }
        });


        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items.get(position).getParticipants() == null){
                    Toast.makeText(context.getApplicationContext(), "This conference has no participants to send a broadcast message", Toast.LENGTH_SHORT).show();
                } else {
                    Intent myIntent = new Intent(context, CreateMessageActivity.class);
                    myIntent.putExtra("fromConference", items.get(position).getId());
                    context.startActivity(myIntent);
                }
            }
        });

        return convertView;
    }

    private void deleteConference(String idConference){
        Call call = userService.deleteConference(idConference);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(context, ShowMyConferencesActivity.class);
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

    static class ViewHolder {
        TextView name;
        ImageButton events;
        ImageButton edit;
        ImageButton delete;
        ImageButton message;
    }
}