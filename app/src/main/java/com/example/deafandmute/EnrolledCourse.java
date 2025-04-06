package com.example.deafandmute;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private ImageView PlayButton;
    LinearLayout PlayButtonView;
    FirebaseAuth mAuth;
    TextView VideoTimer;
    View view;
    int recents, videoIndex;
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
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            courseId = getArguments().getString(ARG_COURSE_ID);
        }
    }

    public void pausePlayer() {
        if (player != null) {
            player.pause();
            long currentPosition = player.getCurrentPosition();
            String language = getString(R.string.lang);
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) return;
            String userId = user.getUid();
            DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
                    .child("courseHistory").child(language).child(courseId);
            courseRef.child("videoIndex").setValue(currentPosition);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_enrolled_course, container, false);
        playerView = view.findViewById(R.id.playerView);
        PlayButton = view.findViewById(R.id.playButton);
        PlayButtonView = view.findViewById(R.id.playButtonView);
        VideoTimer = view.findViewById(R.id.videoTimer);
        // Initialize ExoPlayer (start with empty video)
        player = new ExoPlayer.Builder(requireActivity()).build();
        playerView.setPlayer(player);
        String language = getString(R.string.lang);
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference courseRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(userId).child("courseHistory")
                .child(language)
                .child(courseId);
        // Initialize RecyclerView
        tutorialRecycler = view.findViewById(R.id.tutorial_recycler);
        tutorialRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        tutorialList = new ArrayList<>();
        playerView.setUseController(false); // hides all playback controls
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    recents += 1;
                    videoIndex = 0;
                    courseRef.child("videoIndex").setValue(videoIndex);
                    adapter = new TutorialAdapter(getContext(), tutorialList, EnrolledCourse.this::playSelectedVideo, recents);
                    tutorialRecycler.setAdapter(adapter);
                    fetchTutorials();
                }
            }
        });
        PlayButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayButton.getVisibility() == View.VISIBLE) {
                    PlayButton.setBackgroundResource(R.drawable.pause_icon);
                    player.play();
                    new CountDownTimer(1000, 1000) {
                        public void onTick(long millisUntilFinished) {}
                        public void onFinish() {
                            PlayButton.setVisibility(View.GONE);}
                    }.start();
                } else {
                    player.pause();
                    PlayButton.setVisibility(View.VISIBLE);
                    PlayButton.setBackgroundResource(R.drawable.play_icon);
                }
            }
        });
        Handler handler = new Handler();
        Runnable updateTimeTask = new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    long currentTime = player.getCurrentPosition();
                    long totalTime = player.getDuration();
                    String timeText = formatTime(currentTime) + " / " + formatTime(totalTime);
                    VideoTimer.setText(timeText);
                }
                handler.postDelayed(this, 1000); // keep running every second
            }
        };
        handler.post(updateTimeTask);
        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    recents = snapshot.child("recentVideo").getValue(Integer.class);
                    videoIndex = snapshot.child("videoIndex").getValue(Integer.class);
                } else {
                    recents = 0;
                    videoIndex = 0;
                }
                tutorialRef = FirebaseDatabase.getInstance()
                        .getReference("courses").child(language).child(courseId).child("tutorials");
                // Initialize Adapter **after** recents is set
                adapter = new TutorialAdapter(getContext(), tutorialList, EnrolledCourse.this::playSelectedVideo, recents);
                tutorialRecycler.setAdapter(adapter);
                // Fetch tutorials **after tutorialRef is initialized**
                fetchTutorials();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return view;
    }
    private String formatTime(long millis) {
        if (millis < 0) millis = 0;
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void playSelectedVideo(String videoUrl, int position) {
        String language = getString(R.string.lang);
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
                .child("courseHistory").child(language).child(courseId);
        LinearLayout progressBar = view.findViewById(R.id.videoProgressBar);
        progressBar.setVisibility(View.VISIBLE); // Show loader
        courseRef.child("recentVideo").setValue(position).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
                player.setMediaItem(mediaItem);
                player.prepare();
                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int state) {
                        if (state == Player.STATE_READY || state == Player.STATE_ENDED) {
                            progressBar.setVisibility(View.GONE); // Hide loader
                        }
                    }
                });
                player.seekTo(videoIndex);
                videoIndex = 0;
                if (PlayButton.getVisibility() == View.VISIBLE) {
                    player.pause();
                } else {
                    player.play();
                }

            } else {
                progressBar.setVisibility(View.GONE); // Hide loader on failure
                Toast.makeText(requireContext(), R.string.failed_to_load_video, Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(getContext(), R.string.failed_to_load_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}
