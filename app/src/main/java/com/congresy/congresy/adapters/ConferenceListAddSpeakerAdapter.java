package com.congresy.congresy.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConferenceListAddSpeakerAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService = ApiUtils.getUserService();

    private List<Actor> items;
    private final List<Actor> itemsAux;
    private Context context;
    private String idEvent;

    public ConferenceListAddSpeakerAdapter(Context context, List<Actor> items, List<Actor> itemsAux, String idEvent) {
        this.context = context;
        this.items = items;
        this.itemsAux = itemsAux;
        this.idEvent = idEvent;
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

            convertView = inflater.inflate(R.layout.conference_list_add_speaker, null);
            holder.name = convertView.findViewById(R.id.name);
            holder.add = convertView.findViewById(R.id.add);
            holder.remove = convertView.findViewById(R.id.remove);

            holder.remove.setVisibility(View.INVISIBLE);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String aux1 = sp.getString("Speaker deleted " + itemsAux.get(position).getId(), "not found");
        String aux2 = sp.getString("Speaker added " + itemsAux.get(position).getId(), "not found");

        if (!aux1.equals("not found") && aux2.equals("not found")){
            holder.add.setVisibility(View.VISIBLE);
            holder.remove.setVisibility(View.INVISIBLE);
        } else if (!aux2.equals("not found") && aux1.equals("not found")){
            holder.add.setVisibility(View.INVISIBLE);
            holder.remove.setVisibility(View.VISIBLE);
        }

        holder.name.setText(itemsAux.get(position).getSurname()  + ", " + itemsAux.get(position).getName());

        holder.add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                join(idEvent, holder, position);
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(idEvent, holder, position);
            }
        });

        return convertView;
    }

    private void join(final String idEvent, final ViewHolder holder, int position){
        Call<Actor> call = userService.getActorById(itemsAux.get(position).getId());
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if(response.isSuccessful()){

                    final Actor actor = response.body();

                    execute(idEvent, actor.getId(), holder);

                } else {
                    Toast.makeText(context.getApplicationContext(), "There are no speakers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void execute(String idEvent, final String idActor, final ViewHolder holder){
        Call call = userService.addSpeaker(idEvent, idActor);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    holder.add.setVisibility(View.INVISIBLE);
                    holder.remove.setVisibility(View.VISIBLE);

                    SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("Speaker added " + idActor, "found");
                    editor.remove("Speaker deleted " + idActor);
                    editor.apply();

                    Toast.makeText(context.getApplicationContext(), "Added speaker successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context.getApplicationContext(), "There are no speakers", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void delete(final String idEvent, final ViewHolder holder, int position){
        Call<Actor> call = userService.getActorById(itemsAux.get(position).getId());
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if(response.isSuccessful()){

                    final Actor actor = response.body();

                    executeDelete(idEvent, actor.getId(), holder);

                } else {
                    Toast.makeText(context.getApplicationContext(), "There are no speakers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void executeDelete(String idEvent, final String idActor, final ViewHolder holder){
        Call call = userService.addSpeaker(idEvent, idActor);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    holder.add.setVisibility(View.VISIBLE);
                    holder.remove.setVisibility(View.INVISIBLE);

                    SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("Speaker deleted " + idActor, "found");
                    editor.remove("Speaker added " + idActor);
                    editor.apply();

                    Toast.makeText(context.getApplicationContext(), "Removed speaker successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context.getApplicationContext(), "There are no speakers", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        items.clear();

        if (charText.length() == 0) {
            items.addAll(itemsAux);
        } else {
            for (Actor a : itemsAux) {
                if (a.getName().contains(charText) ||  a.getSurname().contains(charText)) {
                    items.add(a);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView name;
        Button add;
        Button remove;
    }
}