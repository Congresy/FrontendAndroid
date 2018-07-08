package com.congresy.congresy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.congresy.congresy.R;
import com.congresy.congresy.domain.Post;

import java.util.List;
import java.util.Locale;

public class PostListSearchAdapter extends BaseAdapter implements ListAdapter{


    private List<Post> items;
    private final List<Post> itemsAux;
    private Context context;

    public PostListSearchAdapter(Context context, List<Post> items, List<Post> itemsAux) {
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

            convertView = inflater.inflate(R.layout.post_list_search, null);
            holder.title = convertView.findViewById(R.id.title);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(itemsAux.get(position).getTitle()  + ", " + itemsAux.get(position).getAuthor());

        return convertView;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        items.clear();

        if (charText.length() == 0) {
            items.addAll(itemsAux);
        } else {
            for (Post p : itemsAux) {
                if (p.getTitle().contains(charText) || p.getAuthor().contains(charText)) {
                    items.add(p);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView title;
    }
}
