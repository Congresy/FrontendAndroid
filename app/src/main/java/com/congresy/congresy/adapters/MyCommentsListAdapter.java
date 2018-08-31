package com.congresy.congresy.adapters;

import android.annotation.SuppressLint;
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

import com.congresy.congresy.CreateCommentActivity;
import com.congresy.congresy.EditCommentActivity;
import com.congresy.congresy.ParticipantsOfElementActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowConferenceActivity;
import com.congresy.congresy.ShowMyCommentsActivity;
import com.congresy.congresy.ShowPostActivity;
import com.congresy.congresy.ShowResponsesOfComment;
import com.congresy.congresy.domain.Comment;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCommentsListAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService = ApiUtils.getUserService();

    private List<Comment> items;
    private Context context;

    private int aux;

    public MyCommentsListAdapter(Context context, List<Comment> items) {
        this.context = context;
        this.items = items;
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

            convertView = inflater.inflate(R.layout.my_comments_list, null);
            holder.title = convertView.findViewById(R.id.title);
            holder.text = convertView.findViewById(R.id.text);
            holder.edit = convertView.findViewById(R.id.edit);
            holder.replies = convertView.findViewById(R.id.replies);
            holder.toC = convertView.findViewById(R.id.commentable);
            holder.delete = convertView.findViewById(R.id.delete);
            holder.aux = convertView.findViewById(R.id.aux);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(items.get(position).getTitle());
        holder.text.setText(items.get(position).getText());
        holder.aux.setText(items.get(position).getThumbsUp() + " - " + items.get(position).getThumbsDown());

        holder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                delete(items.get(position).getId());
            }
        });

        try {

            if (!items.get(position).getResponses().isEmpty()){
                String str = String.valueOf(items.get(position).getResponses().size());
                //holder.replies.setText(String.format("Replies (%s)", str));

                holder.replies.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(context, ShowResponsesOfComment.class);
                        myIntent.putExtra("idComment", items.get(position).getId());
                        context.startActivity(myIntent);
                    }
                });
            }

        } catch (Exception e){

            holder.replies.setVisibility(View.GONE);
        }


        holder.toC.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getComment(items.get(position).getId(), position);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, EditCommentActivity.class);
                myIntent.putExtra("idComment", items.get(position).getId());
                context.startActivity(myIntent);
            }
        });

        return convertView;
    }

    private void getComment(final String idComment, final int position){
        Call<Comment> call = userService.getComment(idComment);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {

                getConference(response.body().getCommentable(), position);
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(context, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getConference(final String idConference, final int position){
        Call<Conference> call = userService.getConference(idConference);
        call.enqueue(new Callback<Conference>() {
            @Override
            public void onResponse(Call<Conference> call, Response<Conference> response) {

                if (response.code() == 404){
                    getPost(idConference, position);
                } else {
                    Intent myIntent = new Intent(context, ShowConferenceActivity.class);
                    myIntent.putExtra("idConference", items.get(position).getCommentable());
                    context.startActivity(myIntent);

                }

            }

            @Override
            public void onFailure(Call<Conference> call, Throwable t) {
                Toast.makeText(context, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void getPost(final String idPost, final int position){
        Call<Post> call = userService.getPost(idPost);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (response.code() != 404){
                    Intent myIntent = new Intent(context, ShowPostActivity.class);
                        myIntent.putExtra("idPost", items.get(position).getCommentable());
                        context.startActivity(myIntent);
                }

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(context, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void delete(String idComment){
        Call<Void> call = userService.deleteComment(idComment);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                Toast.makeText(context.getApplicationContext(), "Comment deleted correctly!", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(context, ShowMyCommentsActivity.class);
                context.startActivity(myIntent);

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class ViewHolder {
        TextView title;
        TextView text;
        ImageButton edit;
        ImageButton replies;
        ImageButton toC;
        ImageButton delete;
        TextView aux;
    }

}
