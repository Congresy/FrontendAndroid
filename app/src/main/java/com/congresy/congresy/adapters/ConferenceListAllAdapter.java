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

import com.congresy.congresy.HomeActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowMyConferencesActivity;
import com.congresy.congresy.ShowEventsOfConferenceActivity;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConferenceListAllAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService;
    public static Conference conference_;

    private List<Conference> items;
    private Context context;

    public ConferenceListAllAdapter(Context context, List<Conference> items) {
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

        conference_ = items.get(position);

        TextView listItemText = view.findViewById(R.id.name);
        listItemText.setText(items.get(position).getName());

        Button showEvents = view.findViewById(R.id.btnShowEvents);
        final Button joinEvent = view.findViewById(R.id.btnJoin);

        showEvents.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ShowEventsOfConferenceActivity.class);
                myIntent.putExtra("idConference", items.get(position).getId());
                context.startActivity(myIntent);
            }
        });

        joinEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                join(items.get(position).getId(), HomeActivity.actor_.getId(), position);
            }
        });

        return view;
    }

    private void join(String idConference, String idActor, final int position){
        Call call = userService.addParticipant(idConference, idActor);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(context, ShowMyConferencesActivity.class);
                    intent.putExtra("idConference", items.get(position).getId());
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
