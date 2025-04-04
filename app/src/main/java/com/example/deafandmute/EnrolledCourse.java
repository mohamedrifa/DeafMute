package com.example.deafandmute;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EnrolledCourse extends Fragment {

    private RecyclerView tutorialRecycler;
    private TutorialAdapter adapter;
    private List<Tutorial> tutorialList;
    private DatabaseReference tutorialRef;

    private static final String ARG_COURSE_ID = "courseId";
    private String courseId;

    private ExoPlayer player;
    private PlayerView playerView;
    private ImageView fullscreenButton;
    private boolean isFullscreen = false;

    public EnrolledCourse() {
        // Required empty public constructor
    }

    public static EnrolledCourse newInstance(String courseId) {
        EnrolledCourse fragment = new EnrolledCourse();
        Bundle args = new Bundle();
        args.putString(ARG_COURSE_ID, courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getString(ARG_COURSE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrolled_course, container, false);

        // Initialize UI components
        playerView = view.findViewById(R.id.playerView);
        fullscreenButton = view.findViewById(R.id.fullscreen_button);

        // Initialize ExoPlayer
        String videoUrl = "https://drive.google.com/uc?export=download&id=1UFb4Wt7oIM9nFf_mo39uadLrzzpiC8-t";
        player = new ExoPlayer.Builder(requireActivity()).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        fullscreenButton.setOnClickListener(view1 -> toggleFullscreen());

        // Initialize RecyclerView
        tutorialRecycler = view.findViewById(R.id.tutorial_recycler);
        tutorialRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        tutorialList = new ArrayList<>();
        adapter = new TutorialAdapter(getContext(), tutorialList);
        tutorialRecycler.setAdapter(adapter);
        String language = getString(R.string.lang);
        tutorialRef = FirebaseDatabase.getInstance().getReference("courses").child(language).child(courseId).child("tutorials");
        fetchTutorials();
        return view;
    }

    private void fetchTutorials() {
        tutorialRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tutorialList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Tutorial tutorial = childSnapshot.getValue(Tutorial.class);
                    if (tutorial != null) {
                        tutorialList.add(tutorial);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFullscreen() {
        if (!isFullscreen) {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            requireActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            isFullscreen = true;
        } else {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            isFullscreen = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}
