package com.example.deafandmute;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CourseView extends Fragment {
    private static final String ARG_COURSE_ID = "courseId"; // Correct key name
    private String courseId;
    private TextView CourseName, CourseLevel, Ratings, Reviews, CourseLevel1, Experience;
    private ImageView CourseImage;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;

    public CourseView() {
        // Required empty public constructor
    }

    public static CourseView newInstance(String courseId) {
        CourseView fragment = new CourseView();
        Bundle args = new Bundle();
        args.putString(ARG_COURSE_ID, courseId); // Correct key name
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getString(ARG_COURSE_ID); // Use correct key
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_view, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("courses");

        // Check if user is logged in
        if (user == null) {
            Toast.makeText(requireActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        String userId = user.getUid();

        // Initialize views
        CourseName = view.findViewById(R.id.courseTitle);
        CourseLevel = view.findViewById(R.id.courseLevel);
        CourseImage = view.findViewById(R.id.courseIcon);
        Ratings = view.findViewById(R.id.ratings);
        Reviews = view.findViewById(R.id.reviews);
        CourseLevel1 = view.findViewById(R.id.courseLevel1);
        Experience = view.findViewById(R.id.experience);

        // Retrieve courseId from arguments
        Bundle args = getArguments();
        if (args != null) {
            courseId = args.getString("courseId");  // Ensure this key is passed
        } else {
            Toast.makeText(requireActivity(), "Course ID not found", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Fetch data from Firebase
        databaseReference.child(getString(R.string.lang)).child(courseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = snapshot.child("courseName").getValue(String.class);
                            String level = snapshot.child("level").getValue(String.class);
                            String imageUrl = snapshot.child("courseIconUrl").getValue(String.class);
                            String ratings = String.valueOf(snapshot.child("rating").getValue()); // Convert Double to String
                            String reviews = String.valueOf(snapshot.child("enrollmentCount").getValue());

                            if (!TextUtils.isEmpty(name)) CourseName.setText(name);
                            if (!TextUtils.isEmpty(level)) {
                                CourseLevel.setText(level + " Level of Sign Course");
                                CourseLevel1.setText(level + " Level");

                                // Optimized Level-based Experience Text
                                String experienceText;
                                switch (level) {
                                    case "Beginner":
                                        experienceText = "No prior experience required";
                                        break;
                                    case "Intermediate":
                                        experienceText = "Beginner experience required";
                                        break;
                                    case "Master":
                                        experienceText = "Intermediate experience required";
                                        break;
                                    default:
                                        experienceText = "Experience level not specified";
                                }
                                Experience.setText(experienceText);
                            }
                            if (!TextUtils.isEmpty(ratings)) Ratings.setText(ratings+" ");
                            if (!TextUtils.isEmpty(reviews)) Reviews.setText("("+reviews+" reviews)");
                            if (!TextUtils.isEmpty(imageUrl)) Glide.with(requireContext()).load(imageUrl).into(CourseImage);
                        } else {
                            Toast.makeText(requireActivity(), "Course data not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireActivity(), R.string.failed_to_load_course_data, Toast.LENGTH_SHORT).show();
                    }
                });

        return view;
    }

}
