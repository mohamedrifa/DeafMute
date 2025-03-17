package com.example.deafandmute;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CourseView extends Fragment {
    private static final String ARG_COURSE_ID = "courseId"; // Correct key name
    private String courseId;
    TextView id;

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
        View view = inflater.inflate(R.layout.fragment_course_view, container, false); // Correct layout
        id = view.findViewById(R.id.courseId);

        if (courseId != null) {
            id.setText(courseId); // Display courseId
        }

        return view;
    }
}
