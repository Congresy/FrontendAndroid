package com.congresy.congresy.adapters;

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

import com.congresy.congresy.EditSocialNetworkActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowMySocialNetworksActivity;
import com.congresy.congresy.domain.SocialNetwork;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialNetworkAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService;

    public static SocialNetwork socialNetwork_;
    private List<SocialNetwork> items;
    private Context context;

    public SocialNetworkAdapter(Context context, List<SocialNetwork> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public SocialNetwork getItem(int pos) {
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

            convertView = inflater.inflate(R.layout.social_network_list, null);
            holder.name = convertView.findViewById(R.id.name);
            holder.edit = convertView.findViewById(R.id.edit);
            holder.delete = convertView.findViewById(R.id.deleteSN);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(items.get(position).getName());

        holder.edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                socialNetwork_ = items.get(position);

                Intent myIntent = new Intent(context, EditSocialNetworkActivity.class);
                myIntent.putExtra("name", items.get(position).getName());
                context.startActivity(myIntent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                delete(items.get(position).getId());
            }
        });

        return convertView;
    }

    private void delete(String idSocialNetwork){
        Call call = userService.deleteSocialNetwork(idSocialNetwork);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(context, ShowMySocialNetworksActivity.class);
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

    static class ViewHolder {
        TextView name;
        ImageButton edit;
        ImageButton delete;
    }

}
