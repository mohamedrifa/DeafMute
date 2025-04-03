package com.example.deafandmute;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

public class EnrolledCourse extends Fragment {
    private static final String ARG_COURSE_ID = "courseId";
    private String courseId;
    private ExoPlayer player;
    private PlayerView playerView;
    private ImageView fullscreenButton;
    private boolean isFullscreen = false; // Added missing variable

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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrolled_course, container, false);

        playerView = view.findViewById(R.id.playerView);
        fullscreenButton = view.findViewById(R.id.fullscreen_button);
        // Ensure your Google Drive file is accessible
        String videoUrl = "https://drive.google.com/uc?export=download&id=1UFb4Wt7oIM9nFf_mo39uadLrzzpiC8-t";
        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(requireActivity()).build();
        playerView.setPlayer(player);

        // Set media source
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        fullscreenButton.setOnClickListener(view1 -> toggleFullscreen());

        return view;
    }

    private void toggleFullscreen() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}
