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

import com.congresy.congresy.R;
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
        return Long.valueOf(items.get(pos).getId());
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.conference_list_organizator, null);
        }

        TextView listItemText = view.findViewById(R.id.name);
        listItemText.setText(items.get(position).getName());

        Button showEvents = view.findViewById(R.id.btnShowEvents);
        final Button deleteConference = view.findViewById(R.id.btnDeleteConference);

        showEvents.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context.getApplicationContext(), ShowEventsOfConferenceActivity.class);
                myIntent.putExtra("idConference", items.get(position).getId());
                context.startActivity(myIntent);
            }
        });

        deleteConference.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                deleteConference(items.get(position).getId());
            }
        });

        return view;
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
}
