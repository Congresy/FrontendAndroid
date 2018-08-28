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
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.AdministrationCommentsActivity;
import com.congresy.congresy.ProfileActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowMyFoldersActivity;
import com.congresy.congresy.domain.Comment;
import com.congresy.congresy.domain.Message;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import org.joda.time.LocalDateTime;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentListAdministratorAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService = ApiUtils.getUserService();

    private List<Comment> items;
    private List<Comment> itemsAux;
    private Context context;

    public CommentListAdministratorAdapter(Context context, List<Comment> items, List<Comment> itemsAux) {
        this.context = context;
        this.items = items;
        this.itemsAux = itemsAux;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Comment getItem(int pos) {
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

            convertView = inflater.inflate(R.layout.comment_list_administrator, null);
            holder.title = convertView.findViewById(R.id.title);
            holder.delete = convertView.findViewById(R.id.delete);
            holder.author = convertView.findViewById(R.id.author);
            holder.aux = convertView.findViewById(R.id.aux);
            holder.text = convertView.findViewById(R.id.text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(items.get(position).getTitle());
        holder.text.setText(items.get(position).getText());
        holder.aux.setText(items.get(position).getThumbsUp() + " - " + items.get(position).getThumbsDown());


        holder.author.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra("goingTo", "Unknown");
                myIntent.putExtra("idAuthor", items.get(position).getAuthor());
                context.startActivity(myIntent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                JsonObject json = new JsonObject();

                json.addProperty("title", items.get(position).getTitle());
                json.addProperty("text", "** Comment deleted **");

                ban(items.get(position).getId(), json, items.get(position).getAuthor());
            }
        });

        return convertView;
    }

    private void ban(String idComment, JsonObject json, final String idReceiver){
        Call<Comment> call = userService.editComment(idComment, json);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {

                // adding properties to json for POST
                JsonObject json = new JsonObject();

                json.addProperty("body", "The comment with title " + response.body().getTitle() + " has been deleted by and administrator. For further information contact congresy@gmail.com");
                json.addProperty("subject", "An element of yours has been deleted");
                json.addProperty("sentMoment", LocalDateTime.now().toString("dd/MM/yyyy HH:mm"));
                json.addProperty("senderId", "default");
                json.addProperty("receiverId", "default");

                createMessage(json, idReceiver);

            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createMessage(JsonObject json, String idReceiver){
        SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Call<Message> call = userService.createMessage(json, id, idReceiver);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                Intent myIntent = new Intent(context, AdministrationCommentsActivity.class);
                context.startActivity(myIntent);


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
            for (Comment c : itemsAux) {
                if (c.getText().toLowerCase().contains(charText) || c.getTitle().toLowerCase().contains(charText)) {
                    items.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView title;
        TextView text;
        TextView aux;
        ImageButton delete;
        ImageButton author;
    }

}
