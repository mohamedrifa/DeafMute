package com.example.deafandmute;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CURRENT_USER = 0;
    private static final int VIEW_TYPE_OTHER_USER = 1;

    private List<CommunityPost> postList;
    private String currentUserId;
    private String previousAuthorId = "";  // Store the previous post's author ID

    public CommunityAdapter(List<CommunityPost> postList) {
        this.postList = postList;

        // Initialize FirebaseAuth and get the current user's ID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        }
    }

    @Override
    public int getItemViewType(int position) {
        CommunityPost post = postList.get(position);
        // Check if the post's author matches the current user
        if (post.getAuthorId().equals(currentUserId)) {
            return VIEW_TYPE_CURRENT_USER;
        } else {
            return VIEW_TYPE_OTHER_USER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_CURRENT_USER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.current_user_item_post, parent, false);
            return new CurrentUserViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.other_user_item_post, parent, false);
            return new OtherUserViewHolder(view);
        }
    }
    @Override
    public int getItemCount() {
        return postList.size();
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommunityPost post = postList.get(position);

        // Determine if this post is the first message from the author
        boolean isFirstMessageFromUser = isFirstMessageFromAuthor(position);

        if (holder instanceof CurrentUserViewHolder) {
            ((CurrentUserViewHolder) holder).bind(post, isFirstMessageFromUser);
        } else if (holder instanceof OtherUserViewHolder) {
            ((OtherUserViewHolder) holder).bind(post, isFirstMessageFromUser);
        }
    }

    // Helper method to check if a post is the first message from an author
    private boolean isFirstMessageFromAuthor(int position) {
        if (position == 0) {
            // Always show author name for the first item in the list
            return true;
        }
        CommunityPost currentPost = postList.get(position);
        CommunityPost previousPost = postList.get(position - 1);

        // Compare the current post's author ID with the previous post's author ID
        return !currentPost.getAuthorId().equals(previousPost.getAuthorId());
    }

    // Updated bind method in the ViewHolder classes
    public static class CurrentUserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAuthor, textViewContent;

        public CurrentUserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewContent = itemView.findViewById(R.id.textViewContent);
        }

        public void bind(CommunityPost post, boolean isFirstMessageFromUser) {
            textViewAuthor.setVisibility(isFirstMessageFromUser ? View.VISIBLE : View.GONE);
            if (isFirstMessageFromUser) {
                textViewAuthor.setText(post.getAuthorName());
            }
            textViewContent.setText(post.getContent());
        }
    }

    public static class OtherUserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAuthor, textViewContent;

        public OtherUserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewContent = itemView.findViewById(R.id.textViewContent);
        }

        public void bind(CommunityPost post, boolean isFirstMessageFromUser) {
            textViewAuthor.setVisibility(isFirstMessageFromUser ? View.VISIBLE : View.GONE);
            if (isFirstMessageFromUser) {
                textViewAuthor.setText(post.getAuthorName());
            }
            textViewContent.setText(post.getContent());
        }
    }
}
