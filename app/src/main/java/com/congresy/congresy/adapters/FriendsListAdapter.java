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

import com.congresy.congresy.CreateMessageActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowAllConferencesActivity;
import com.congresy.congresy.ShowMyFriendsActivity;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsListAdapter extends BaseAdapter implements ListAdapter {

    private List<Actor> items;
    private Context context;

    private UserService userService = ApiUtils.getUserService();

    public FriendsListAdapter(Context context, List<Actor> items) {
        this.context = context;
        this.items = items;
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
        final ViewHolder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.friends_list, null);
            holder.name = convertView.findViewById(R.id.name);
            holder.unfriend = convertView.findViewById(R.id.unfriend);
            holder.upcoming = convertView.findViewById(R.id.upcoming);
            holder.message = convertView.findViewById(R.id.message);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(items.get(position).getName());

        holder.unfriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                unfriend(items.get(position).getId());
            }
        });

        holder.upcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowAllConferencesActivity.class);
                intent.putExtra("comeFrom", "1");
                intent.putExtra("idActor", items.get(position).getId());
                context.startActivity(intent);
            }
        });

        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateMessageActivity.class);
                intent.putExtra("idReceiver", items.get(position).getId());
                intent.putExtra("comeFrom", "create1");
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private void unfriend (final String idActorToFriend){
        SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String idActor = sp.getString("Id", "not found");

        Call<Actor> call = userService.friend(idActor, idActorToFriend, "unfriend");
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {

                SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("friend " + idActorToFriend);
                editor.apply();

                Intent intent = new Intent(context, ShowMyFriendsActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class ViewHolder {
        TextView name;
        ImageButton unfriend;
        ImageButton upcoming;
        ImageButton message;
    }

}
