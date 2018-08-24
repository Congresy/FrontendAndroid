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

import com.congresy.congresy.AdministrationActorsActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActorListBannedAdministratorAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService = ApiUtils.getUserService();

    private List<Actor> items;
    private Context context;
    private List<Actor> itemsAux;

    public static List<Actor> speakers;

    public ActorListBannedAdministratorAdapter(Context context, List<Actor> items, List<Actor> itemsAux) {
        this.context = context;
        this.items = items;
        this.itemsAux = itemsAux;
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

            convertView = inflater.inflate(R.layout.actors_list_administrator_banned, null);
            holder.name = convertView.findViewById(R.id.name);
            holder.delete = convertView.findViewById(R.id.delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(items.get(position).getName());


        holder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                unban(items.get(position).getId());
            }
        });

        return convertView;
    }

    private void unban(String idActor){
        Call call = userService.banActor(idActor, "unban");
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(context.getApplicationContext(), AdministrationActorsActivity.class);
                    context.getApplicationContext().startActivity(intent);

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

    public void filter(String charText) {
        charText = charText.toLowerCase();
        items.clear();

        if (charText.length() == 0) {
            items.addAll(itemsAux);
        } else {
            for (Actor a : itemsAux) {
                if (a.getName().toLowerCase().contains(charText) || a.getSurname().toLowerCase().contains(charText) || a.getEmail().toLowerCase().contains(charText)) {
                    items.add(a);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView name;
        Button delete;
    }
}