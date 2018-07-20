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

import com.congresy.congresy.EditCommentActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowPostActivity;
import com.congresy.congresy.ShowResponsesOfComment;
import com.congresy.congresy.domain.Comment;
import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentListAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService = ApiUtils.getUserService();

    private List<Comment> items;
    private Context context;

    public CommentListAdapter(Context context, List<Comment> items) {
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

            convertView = inflater.inflate(R.layout.comment_list, null);
            holder.title = convertView.findViewById(R.id.title);
            holder.text = convertView.findViewById(R.id.text);
            holder.up = convertView.findViewById(R.id.voteUp);
            holder.down = convertView.findViewById(R.id.voteDown);
            holder.reply = convertView.findViewById(R.id.reply);
            holder.replies = convertView.findViewById(R.id.replies);
            holder.edit = convertView.findViewById(R.id.edit);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(items.get(position).getTitle());
        holder.text.setText(items.get(position).getText());

        holder.up.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                voteUp(holder, items.get(position).getId(), position);
            }
        });

        holder.down.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                voteDown(holder, items.get(position).getId(), position);
            }
        });

        holder.reply.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

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

        holder.replies.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ShowResponsesOfComment.class);
                myIntent.putExtra("idComment", items.get(position).getId());
                context.startActivity(myIntent);
            }
        });

        return convertView;
    }

    private void voteUp(final ViewHolder holder, String idComment, final int position){
        Call<Comment> call = userService.voteComment(idComment, "Up");
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {

                Toast.makeText(context.getApplicationContext(), "Vote sent correctly!", Toast.LENGTH_SHORT).show();
                holder.up.setVisibility(View.GONE);
                holder.down.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void voteDown(final ViewHolder holder, String idComment, final int position){
        Call<Comment> call = userService.voteComment(idComment, "DOwn");
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {

                Toast.makeText(context.getApplicationContext(), "Vote sent correctly!", Toast.LENGTH_SHORT).show();
                holder.down.setVisibility(View.GONE);
                holder.up.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class ViewHolder {
        TextView title;
        TextView text;
        Button up;
        Button down;
        Button reply;
        Button replies;
        Button edit;
    }

}
