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

import com.congresy.congresy.EditPostActivity;
import com.congresy.congresy.R;
import com.congresy.congresy.ShowMyPostsActivity;
import com.congresy.congresy.domain.Post;
import com.congresy.congresy.remote.ApiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListOwnAdapter extends BaseAdapter implements ListAdapter{

    private List<Post> items;
    private Context context;

    public PostListOwnAdapter(Context context, List<Post> items) {
        this.context = context;
        this.items = items;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.post_list_own, null);
            holder.title = convertView.findViewById(R.id.title);
            holder.edit = convertView.findViewById(R.id.edit);
            holder.delete = convertView.findViewById(R.id.delete);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(!items.get(position).getDraft()){
            holder.edit.setVisibility(View.INVISIBLE);
        }

        final int new_position = position;

        holder.title.setText(items.get(position).getTitle());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, EditPostActivity.class);
                myIntent.putExtra("idPost", items.get(new_position).getId());
                myIntent.putExtra("category", items.get(new_position).getCategory());
                context.startActivity(myIntent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost(items.get(new_position).getId());
            }
        });

        return convertView;
    }

    private void deletePost(String idPost){
        Call call = ApiUtils.getUserService().deletePost(idPost);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(context, ShowMyPostsActivity.class);
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
        TextView title;
        ImageButton edit;
        ImageButton delete;
    }
}
