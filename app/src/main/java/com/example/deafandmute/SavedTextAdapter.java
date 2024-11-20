package com.example.deafandmute;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SavedTextAdapter extends RecyclerView.Adapter<SavedTextAdapter.ViewHolder> {
    private List<String> savedTexts;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String text);
    }

    public SavedTextAdapter(List<String> savedTexts, OnItemClickListener onItemClickListener) {
        this.savedTexts = savedTexts;
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String savedText = savedTexts.get(position);
        holder.savedTextTextView.setText(savedText);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.spoke.setVisibility(View.VISIBLE);
                onItemClickListener.onItemClick(savedText);
                int length = savedText.length();
                if(length<=2)
                    holder.spoke.postDelayed(() -> holder.spoke.setVisibility(View.GONE), (length*900));
                else if(length>2 && length<=10)
                    holder.spoke.postDelayed(() -> holder.spoke.setVisibility(View.GONE), (length*600));
                else
                    holder.spoke.postDelayed(() -> holder.spoke.setVisibility(View.GONE), (length*50));
            }
        });
    }
    @Override
    public int getItemCount() {
        return savedTexts.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView savedTextTextView;
        LinearLayout spoke;
        public ViewHolder(View itemView) {
            super(itemView);
            savedTextTextView = itemView.findViewById(R.id.savedTextTextView);
            spoke = itemView.findViewById(R.id.speaking);
        }
    }
}
