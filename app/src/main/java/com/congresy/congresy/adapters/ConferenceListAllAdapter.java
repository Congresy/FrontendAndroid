package com.congresy.congresy.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.JoiningConferenceActivity;
import com.congresy.congresy.ProfileActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowConferenceActivity;
import com.congresy.congresy.ShowEventsOfConferenceActivity;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.remote.ApiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConferenceListAllAdapter extends BaseAdapter implements ListAdapter {

    public static Conference conference_;
    private Place res = null;

    private List<Conference> items;
    private List<Conference> itemsAux;
    private Context context;

    public ConferenceListAllAdapter(Context context, List<Conference> items, List<Conference> itemsAux) {
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

            convertView = inflater.inflate(R.layout.conference_list_user, null);
            holder.name = convertView.findViewById(R.id.name);
            holder.events = convertView.findViewById(R.id.btnShowEvents);
            holder.join = convertView.findViewById(R.id.btnJoin);
            holder.organizator = convertView.findViewById(R.id.organizator);
            holder.aux = convertView.findViewById(R.id.text);

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

        conference_ = items.get(position);

        holder.name.setText(items.get(position).getName());

        try {
            holder.aux.setText("(" + items.get(position).getSeatsLeft() + ")");
        } catch (NullPointerException e){
            holder.aux.setText("(0)");
            holder.join.setClickable(false);
        }

        holder.join.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, JoiningConferenceActivity.class);
                intent.putExtra("idConference", items.get(position).getId());
                intent.putExtra("price", items.get(position).getPrice());
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

        holder.organizator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra("goingTo", "Organizator");
                myIntent.putExtra("idOrganizator", items.get(position).getOrganizator());
                context.startActivity(myIntent);
            }
        });

        return convertView;
    }

    // Filter Class
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

    public void filterTheme(String charText, String theme) {
        charText = charText.toLowerCase();
        items.clear();

        if (charText.length() == 0) {
            items.addAll(itemsAux);
        } else {
            for (Conference c : itemsAux) {
                if (c.getTheme().toLowerCase().equals(theme.toLowerCase())){
                    if (c.getName().toLowerCase().contains(charText) || c.getDescription().toLowerCase().contains(charText) || c.getSpeakersNames().toLowerCase().contains(charText) || c.getStart().contains(charText)) {
                        items.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterPlace(String charText) {
        int index = 0;
        charText = charText.toLowerCase();
        items.clear();

        if (charText.length() == 0) {
            items.addAll(itemsAux);
        } else {
            for (Conference c : itemsAux) {
                if (loadPlace(c.getPlace()).getDetails().toLowerCase().contains(charText) || loadPlace(c.getPlace()).getPostalCode().contains(charText) || loadPlace(c.getPlace()).getAddress().toLowerCase().contains(charText) || loadPlace(c.getPlace()).getCountry().toLowerCase().contains(charText) || loadPlace(c.getPlace()).getTown().toLowerCase().contains(charText)) {
                    items.add(itemsAux.get(index));
                }
                index++;
            }
        }
        notifyDataSetChanged();
    }

    private Place loadPlace(String idPlace){

        Call<Place> call = ApiUtils.getUserService().getPlace(idPlace);
        call.enqueue(new Callback<Place>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {

                res = response.body();

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(context, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

        return res;

    }

    static class ViewHolder {
        TextView name;
        TextView aux;
        ImageButton events;
        ImageButton join;
        ImageButton organizator;
    }

}
