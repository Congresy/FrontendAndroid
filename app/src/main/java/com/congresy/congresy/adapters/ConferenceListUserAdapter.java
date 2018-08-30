package com.congresy.congresy.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.ProfileActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowBarcodeActivity;
import com.congresy.congresy.ShowConferenceActivity;
import com.congresy.congresy.ShowEventsOfConferenceAuxActivity;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

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
            holder.aux = convertView.findViewById(R.id.text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.aux.setVisibility(GONE);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowConferenceActivity.class);
                intent.putExtra("idConference", items.get(position).getId());
                context.startActivity(intent);
            }
        });

        holder.name.setText(items.get(position).getName());

        holder.join.setVisibility(GONE);

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
                myIntent.putExtra("goingTo", "Organizator");
                context.startActivity(myIntent);
            }
        });

        holder.ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(items.get(position).getId());
            }
        });

        return convertView;

    }

    private void loadData(final String idConference){
        Call<Conference> call = userService.getConference(idConference);
        call.enqueue(new Callback<Conference>() {
            @Override
            public void onResponse(Call<Conference> call, Response<Conference> response) {

                Conference con = response.body();

                loadPlace(con.getPlace(),con.getName(),con.getStart(), con.getEnd(),String.valueOf(con.getPrice()), idConference);

            }

            @Override
            public void onFailure(Call<Conference> call, Throwable t) {
                Toast.makeText(context, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void loadPlace(String idPlace, final String name, final String start, final String end, final String price, final String idConference){
        Call<Place> call = userService.getPlace(idPlace);
        call.enqueue(new Callback<Place>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {

                Place p = response.body();

                Intent myIntent = new Intent(context, ShowBarcodeActivity.class);
                myIntent.putExtra("idConference", idConference);
                myIntent.putExtra("nameConference", name);
                myIntent.putExtra("date", start + " - " + end);
                myIntent.putExtra("price", price);
                myIntent.putExtra("countryAndCity", p.getPostalCode() + " - " + p.getTown()  + ", " + p.getCountry());
                myIntent.putExtra("address",  p.getAddress());
                myIntent.putExtra("details",  p.getDetails());
                context.startActivity(myIntent);

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(context, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    static class ViewHolder {
        TextView name;
        ImageButton events;
        ImageButton join;
        ImageButton organizator;
        ImageButton ticket;
        TextView aux;
    }
}