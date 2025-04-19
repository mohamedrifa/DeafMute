package com.example.deafandmute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder> {

    private final List<String> categoryList;
    private final Map<String, ArrayList<String>> dataMap;

    public ParentAdapter(Map<String, ArrayList<String>> dataMap) {
        this.dataMap = dataMap;
        this.categoryList = new ArrayList<>(dataMap.keySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String category = categoryList.get(position);
        Context context = holder.itemView.getContext();
        int resId = context.getResources().getIdentifier(category.toLowerCase(), "string", context.getPackageName());
        if (resId != 0) {
            holder.headingTextView.setText(resId);
        } else {
            holder.headingTextView.setText(category);
        }
        ArrayList<String> childItems = dataMap.get(category);
        ChildAdapter childAdapter = new ChildAdapter(childItems);
        holder.childRecyclerView.setLayoutManager(
                new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        holder.childRecyclerView.setAdapter(childAdapter);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView headingTextView;
        RecyclerView childRecyclerView;

        ViewHolder(View itemView) {
            super(itemView);
            headingTextView = itemView.findViewById(R.id.headingTextView);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
        }
    }
}
