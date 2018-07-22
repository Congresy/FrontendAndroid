package com.congresy.congresy.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.CreateMessageActivity;
import com.congresy.congresy.EditEventActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowEventsOfConferenceActivity;
import com.congresy.congresy.ShowMessagesOfFolderActivity;
import com.congresy.congresy.ShowMyFoldersActivity;
import com.congresy.congresy.ShowSpeakersOfEventActivity;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.domain.Message;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesListAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService = ApiUtils.getUserService();
    public static List<Actor> speakers;

    private List<Message> items;
    private Context context;

    public MessagesListAdapter(Context context, List<Message> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Message getItem(int pos) {
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

            convertView = inflater.inflate(R.layout.messages_list, null);
            holder.subject = convertView.findViewById(R.id.subject);
            holder.body = convertView.findViewById(R.id.body);
            holder.reply = convertView.findViewById(R.id.reply);
            holder.toTrash = convertView.findViewById(R.id.toTrash);
            holder.delete = convertView.findViewById(R.id.delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.subject.setText(items.get(position).getSubject());
        holder.body.setText(items.get(position).getBody());

        SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String folderName = sp.getString("folderName", "not found");

        if (!folderName.equals("Trash")){
            holder.delete.setVisibility(View.GONE);
            holder.toTrash.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
            holder.toTrash.setVisibility(View.GONE);
        }

        holder.reply.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, CreateMessageActivity.class);
                myIntent.putExtra("idMessage", items.get(position).getId());
                myIntent.putExtra("comeFrom", "reply");
                context.startActivity(myIntent);
            }
        });

        holder.toTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToTrash(items.get(position).getId());
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(items.get(position).getId());
            }
        });

        return convertView;
    }

    private void moveToTrash(String idMessage){
        SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Call<Message> call = userService.sendMessagetoTrash(id, idMessage);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                Intent intent = new Intent(context, ShowMyFoldersActivity.class);
                context.startActivity(intent);

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void delete(String idMessage){
        SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Call<Void> call = userService.deleteMessage(id, idMessage);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                Intent intent = new Intent(context, ShowMyFoldersActivity.class);
                context.startActivity(intent);

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    static class ViewHolder {
        TextView subject;
        TextView body;
        Button reply;
        Button toTrash;
        Button delete;
    }
}
