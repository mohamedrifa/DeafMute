package com.example.deafandmute;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder> {

    private List<Tutorial> tutorialList;
    private Context context;

    public TutorialAdapter(Context context, List<Tutorial> tutorialList) {
        this.context = context;
        this.tutorialList = tutorialList;
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
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tutorial.getUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tutorialList.size();
    }

    public static class TutorialViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public TutorialViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tutorial_name);
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


