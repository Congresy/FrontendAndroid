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

import com.congresy.congresy.AdministrationPostsActivity;
import com.congresy.congresy.ProfileActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.domain.Message;
import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;
import com.google.gson.JsonObject;

import org.joda.time.LocalDateTime;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListAdministratorAdapter extends BaseAdapter implements ListAdapter{

    private List<Post> items;
    private List<Post> itemsAux;
    private Context context;

    public PostListAdministratorAdapter(Context context, List<Post> items, List<Post> itemsAux) {
        this.context = context;
        this.items = items;
        this.itemsAux = itemsAux;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Post getItem(int pos) {
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

            convertView = inflater.inflate(R.layout.post_list_administrator, null);
            holder.title = convertView.findViewById(R.id.title);
            holder.delete = convertView.findViewById(R.id.delete);
            holder.author = convertView.findViewById(R.id.author);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(items.get(position).getTitle());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost(items.get(position).getId(), items.get(position).getAuthorId(), items.get(position));
            }
        });

        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra("goingTo", "Unknown");
                myIntent.putExtra("idAuthor", items.get(position).getAuthorId());
                context.startActivity(myIntent);
            }
        });

        return convertView;
    }

    private void deletePost(String idPost, final String idReceiver, final Post post){
        Call call = ApiUtils.getUserService().deletePost(idPost);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    // adding properties to json for POST
                    JsonObject json = new JsonObject();

                    json.addProperty("body", "The conference with title " + post.getTitle() + " has been deleted by and administrator. For further information contact congresy@gmail.com");
                    json.addProperty("subject", "An element of yours has been deleted");
                    json.addProperty("sentMoment", LocalDateTime.now().toString("dd/MM/yyyy HH:mm"));
                    json.addProperty("senderId", "default");
                    json.addProperty("receiverId", "default");

                    createMessage(json, idReceiver);



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

    private void createMessage(JsonObject json, String idReceiver){
        SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Call<Message> call = ApiUtils.getUserService().createMessage(json, id, idReceiver);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                Intent intent = new Intent(context, AdministrationPostsActivity.class);
                context.startActivity(intent);

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
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
            for (Post p : itemsAux) {
                if (p.getTitle().toLowerCase().contains(charText) || p.getBody().toLowerCase().contains(charText)) {
                    items.add(p);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView title;
        Button delete;
        Button author;
    }
}
