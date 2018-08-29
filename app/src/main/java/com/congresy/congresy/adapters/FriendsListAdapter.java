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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            holder.done = convertView.findViewById(R.id.accept);
            holder.cancel = convertView.findViewById(R.id.cancel);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.done.setVisibility(View.GONE);
        holder.cancel.setVisibility(View.GONE);
        holder.unfriend.setVisibility(View.GONE);
        holder.upcoming.setVisibility(View.GONE);
        holder.message.setVisibility(View.GONE);

        holder.name.setText(items.get(position).getName());

        final SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        final String check1 = sp.getString("statusFriendWhoDontKnowString " + sp.getString("Id", "not found") + ", " + items.get(position).getId(), "0");
        final String check2 = sp.getString("statusFriendWhoDontKnowString " + items.get(position).getId() + ", " + sp.getString("Id", "not found"), "0");

        Set<String> status1 = sp.getStringSet("statusFriendWhoDontKnow1 " + sp.getString("Id", "not found") + ", " + items.get(position).getId(), new HashSet<String>());
        Set<String> status2 = sp.getStringSet("statusFriendWhoDontKnow1 " + items.get(position).getId() + ", " + sp.getString("Id", "not found"), new HashSet<String>());

        List<String> status11 = new ArrayList<>(status1);
        List<String> status22 = new ArrayList<>(status2);

        if (!status11.isEmpty()){
            holder.done.setVisibility(View.VISIBLE);
                holder.cancel.setVisibility(View.VISIBLE);

                holder.done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("statusFriendWhoDontKnowString " + sp.getString("Id", "not found") + ", " + items.get(position).getId(), "1");
                        editor.apply();
                        Intent intent = new Intent(context, ShowMyFriendsActivity.class);
                        context.startActivity(intent);
                    }
                });

                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unfriend(items.get(position).getId());
                    }
                });

        } else if (!status22.isEmpty()) {
            holder.done.setVisibility(View.VISIBLE);
            holder.cancel.setVisibility(View.VISIBLE);

            holder.done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("statusFriendWhoDontKnowString " + sp.getString("Id", "not found") + ", " + items.get(position).getId(), "1");
                        editor.apply();
                        Intent intent = new Intent(context, ShowMyFriendsActivity.class);
                        context.startActivity(intent);
                    }
                });

                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unfriend(items.get(position).getId());
                    }
                });

        } else {
            holder.done.setImageResource(R.drawable.baseline_access_time_black_18dp);
            holder.done.setVisibility(View.VISIBLE);
            holder.done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context.getApplicationContext(), "This friend request is pending", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if ((!check1.equals("0") || !check2.equals("0"))) {
            holder.done.setVisibility(View.GONE);
            holder.cancel.setVisibility(View.GONE);
            holder.unfriend.setVisibility(View.VISIBLE);
            holder.upcoming.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.VISIBLE);
        }


        holder.unfriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                if (!check1.equals("0")){
                    editor.putString("statusFriendWhoDontKnowString " + sp.getString("Id", "not found") + ", " + items.get(position).getId(), "0");
                    editor.putStringSet("statusFriendWhoDontKnow1 " + sp.getString("Id", "not found") + ", " + items.get(position).getId(), new HashSet<String>());
                } else if (!check2.equals("0")){
                    editor.putString("statusFriendWhoDontKnowString " + items.get(position).getId() + ", " + sp.getString("Id", "not found"), "0");
                    editor.putStringSet("statusFriendWhoDontKnow1 " + items.get(position).getId() + ", " + sp.getString("Id", "not found"), new HashSet<String>());
                }
                editor.apply();
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
        ImageButton done;
        ImageButton cancel;
    }

}
