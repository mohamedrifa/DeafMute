package com.example.deafandmute;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Community extends Fragment {

    private RecyclerView recyclerView;
    private CommunityAdapter adapter;
    private List<CommunityPost> postList = new ArrayList<>();
    private EditText editTextPostContent;
    private ImageButton buttonPost;
    private LinearLayout inputLayout; // Parent layout to be moved
    private DatabaseReference communityRef;

    public Community() {
        // Required empty public constructor
    }

    public static Community newInstance(String param1, String param2) {
        Community fragment = new Community();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewCommunity);
        editTextPostContent = view.findViewById(R.id.editTextPostContent);
        buttonPost = view.findViewById(R.id.buttonPost);
        inputLayout = view.findViewById(R.id.inputLayout); // Initialize parent layout

        communityRef = FirebaseDatabase.getInstance().getReference("Community");

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommunityAdapter(postList);
        recyclerView.setAdapter(adapter);

        // Setup keyboard visibility listener
        setupKeyboardVisibilityListener(view, inputLayout);

        // Fetch posts from Firebase
        fetchPosts();

        // Handle new post creation
        buttonPost.setOnClickListener(v -> {
            String content = editTextPostContent.getText().toString().trim();
            if (!content.isEmpty()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String authorId = user.getUid();
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                    db.child("Users").child(authorId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot userSnapshot) {
                                    if (userSnapshot.exists()) {
                                        String authorName = userSnapshot.child("username").getValue(String.class);

                                        long timestamp = System.currentTimeMillis();
                                        CommunityPost newPost = new CommunityPost(authorId, authorName, content, timestamp, 0);

                                        db.child("Community").push().setValue(newPost)
                                                .addOnSuccessListener(aVoid -> {
                                                    editTextPostContent.setText(""); // Clear input field
                                                    Toast.makeText(getContext(), R.string.post_created, Toast.LENGTH_SHORT).show();
                                                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(getContext(), R.string.failed_to_create_post, Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getContext(), R.string.failed_to_fetch_user_data, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(getContext(), R.string.post_content_cannot_be_empty, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPosts() {
        communityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    CommunityPost post = postSnapshot.getValue(CommunityPost.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }
                adapter.notifyDataSetChanged();
                if (adapter.getItemCount() > 0) {
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), R.string.failed_to_load_posts, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupKeyboardVisibilityListener(View rootView, View parentLayout) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) { // Keyboard is visible
                parentLayout.setTranslationY(-30); // Directly move up by keypad height
            } else {
                parentLayout.setTranslationY(0); // Reset to default position
            }
        });
    }

}
