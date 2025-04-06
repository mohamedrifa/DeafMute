package com.example.deafandmute;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder> {

    private List<Tutorial> tutorialList;
    private Context context;
    private OnTutorialClickListener listener;
    private int selectedPosition; // Track selected item

    public interface OnTutorialClickListener {
        void onTutorialClick(String videoUrl, int position);
    }

    public TutorialAdapter(Context context, List<Tutorial> tutorialList, OnTutorialClickListener listener, int recents) {
        this.context = context;
        this.tutorialList = tutorialList;
        this.listener = listener;
        selectedPosition = recents;
    }

    @NonNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tutorial, parent, false);
        return new TutorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
        Tutorial tutorial = tutorialList.get(position);
        holder.nameTextView.setText(tutorial.getName());
        if (position == selectedPosition ) {
            holder.ListBar.setBackgroundResource(R.drawable.tutorial_selected);
            holder.nameTextView.setTextColor(Color.parseColor("#FFFFFF"));
            listener.onTutorialClick(tutorial.getUrl(), position);
        } else {
            holder.ListBar.setBackgroundResource(R.drawable.tutorial_item_border);
            holder.nameTextView.setTextColor(Color.parseColor("#808080"));
        }
        holder.ListBar.setOnClickListener(v -> selectVideo(position ));
    }
    void selectVideo(int position) {
        if (selectedPosition != position) {
            selectedPosition = position;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return tutorialList.size();
    }

    public static class TutorialViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        LinearLayout ListBar;
        public TutorialViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tutorial_name);
            ListBar = itemView.findViewById(R.id.listbar);
        }
    }
}
class Tutorial {
    private String name;
    private String url;

    public Tutorial() {
        // Default constructor required for calls to DataSnapshot.getValue(Tutorial.class)
    }

    public Tutorial(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}


