package com.congresy.congresy.adapters;

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

import com.congresy.congresy.R;
import com.congresy.congresy.ShowAllAnnouncementsActivity;
import com.congresy.congresy.domain.Announcement;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnnouncementListAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService = ApiUtils.getUserService();

    private List<Announcement> items;
    private Context context;

    public static List<Announcement> speakers;

    public AnnouncementListAdapter(Context context, List<Announcement> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Announcement getItem(int pos) {
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

            convertView = inflater.inflate(R.layout.announcements_list, null);
            holder.url = convertView.findViewById(R.id.url);
            holder.delete = convertView.findViewById(R.id.delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.url.setText(items.get(position).getUrl());

        holder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                delete(items.get(position).getId());
            }
        });

        return convertView;
    }


    private void delete(String id){
        Call call = userService.deleteAnnouncement(id);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                Intent intent = new Intent(context.getApplicationContext(), ShowAllAnnouncementsActivity.class);
                context.getApplicationContext().startActivity(intent);

            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    static class ViewHolder {
        TextView url;
        ImageButton delete;
    }
}