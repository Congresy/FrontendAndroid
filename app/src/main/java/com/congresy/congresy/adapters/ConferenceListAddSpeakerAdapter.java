package com.congresy.congresy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.R;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.ArrayList;
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
    private List<String> state;

    public ConferenceListAddSpeakerAdapter(Context context, List<Actor> items, List<Actor> itemsAux, String idEvent) {
        this.context = context;
        this.items = items;
        this.itemsAux = itemsAux;
        this.idEvent = idEvent;
        this.state = new ArrayList<>();
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

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (state.contains("Dismiss " + items.get(position).getId())){
            holder.add.setImageResource(R.drawable.baseline_remove_black_18dp);
            holder.add.setTag("Dismiss");
        } else if (state.contains("Add " + items.get(position).getId())) {
            holder.add.setImageResource(R.drawable.baseline_add_black_18dp);
            holder.add.setTag("Add");
        } else {
            holder.add.setImageResource(R.drawable.baseline_add_black_18dp);
            holder.add.setTag("Add");
        }

        if (holder.add.getTag().equals("Add")) {
            holder.add.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    join(idEvent, holder, position);
                }
            });
        } else if (holder.add.getTag().equals("Dismiss")) {
            holder.add.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    delete(idEvent, holder, position);
                }
            });
        }

        holder.name.setText(itemsAux.get(position).getSurname()  + ", " + itemsAux.get(position).getName());

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
                    holder.add.setImageResource(R.drawable.baseline_remove_black_18dp);
                    holder.add.setTag("Dismiss");
                    state.add("Dismiss " + idActor);
                    state.remove("Add " + idActor);
                    notifyDataSetChanged();

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
                    holder.add.setImageResource(R.drawable.baseline_add_black_18dp);
                    holder.add.setTag("Add");
                    state.add("Add " + idActor);
                    state.remove("Dismiss " + idActor);
                    notifyDataSetChanged();

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
        ImageButton add;
    }
}