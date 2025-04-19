package com.example.deafandmute;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {
    private List<String> childList;

    public ChildAdapter(List<String> childList) {
        this.childList = childList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.itemTextView.setText(childList.get(position));

        String category = childList.get(position);
        Context context = holder.itemView.getContext();
        int stringId = context.getResources().getIdentifier(category.toLowerCase(), "string", context.getPackageName());
        if (stringId != 0) {
            holder.itemTextView.setText("\"" + context.getString(stringId) + "\"");
        } else {
            holder.itemTextView.setText("\"" + category + "\"");
        }
        int drawableId = context.getResources().getIdentifier(category.toLowerCase(), "drawable", context.getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(drawableId)
                .into(holder.Icon);

    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;
        ImageView Icon;
        ViewHolder(View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            Icon = itemView.findViewById(R.id.icon);
        }
    }
}

