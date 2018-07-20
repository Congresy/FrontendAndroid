package com.congresy.congresy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.congresy.congresy.R;
import com.congresy.congresy.domain.Comment;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

public class CommentListAdapter extends BaseAdapter implements ListAdapter {

    private UserService userService;

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

        userService = ApiUtils.getUserService();
        ViewHolder holder;
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

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(items.get(position).getTitle());
        holder.text.setText(items.get(position).getText());

        holder.up.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        holder.down.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        holder.reply.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == this.getCount() - 1) ? 1 : 0;
    }

    static class ViewHolder {
        TextView title;
        TextView text;
        Button up;
        Button down;
        Button reply;
    }

}
