package com.example.deafandmute;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deafandmute.Course;
import com.example.deafandmute.CourseAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


public class Home extends Fragment {

    private RelativeLayout basicNeeds, textToSpeech, speechtoText;
    private RecyclerView recyclerView, recyclerView1;
    private CourseAdapter allCourseAdapter, enrolledCourseAdapter;
    private List<Course> courseList = new ArrayList<>();
    private List<Course> enrolledCourses = new ArrayList<>();
    TextView EnrolledText;
    private DatabaseReference databaseReference;

    public Home() {
        // Required empty public constructor
    }
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView1 = view.findViewById(R.id.recyclerView1);
        EnrolledText = view.findViewById(R.id.enrolledText);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(35), dpToPx(15), true));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(layoutManager);

        int spacingInPixels = dpToPx(16); // or any spacing you want
        recyclerView1.addItemDecoration(new HorizontalSpaceItemDecoration(spacingInPixels));

        allCourseAdapter = new CourseAdapter(courseList, getContext());
        enrolledCourseAdapter = new CourseAdapter(enrolledCourses, getContext());

        recyclerView.setAdapter(allCourseAdapter);
        recyclerView1.setAdapter(enrolledCourseAdapter);

        String language = getString(R.string.lang);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        String userId = user.getUid();

        // Fetch ALL courses first, then fetch ENROLLED
        DatabaseReference allCoursesRef = FirebaseDatabase.getInstance().getReference("courses/" + language);
        allCoursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseList.clear();
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);
                    if (course != null) {
                        course.setCourseId(courseSnapshot.getKey());
                        courseList.add(course);
                    }
                }
                allCourseAdapter.notifyDataSetChanged();

                // Once all courses are loaded, fetch enrolled courses
                fetchEnrolledCourses(userId, language);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Buttons to navigate to other fragments
        basicNeeds = view.findViewById(R.id.BasicNeeds);
        textToSpeech = view.findViewById(R.id.TextToSpeech);
        speechtoText = view.findViewById(R.id.SpeechToText);

        basicNeeds.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new BasicNeeds())
                .commit());

        textToSpeech.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TextToSpeechFragment())
                .commit());

        speechtoText.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SpeechToText())
                .commit());

        return view;
    }

    // Separate method to fetch enrolled courses after all courses are loaded
    private void fetchEnrolledCourses(String userId, String language) {
        DatabaseReference enrolledRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId).child("courseHistory").child(language);
        enrolledRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                enrolledCourses.clear();
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    String enrolledCourseId = courseSnapshot.getKey();
                    for (Course course : courseList) {
                        if (course.getCourseId().equals(enrolledCourseId)) {
                            EnrolledText.setVisibility(View.VISIBLE);
                            enrolledCourses.add(course);
                            break;
                        }
                    }
                }
                enrolledCourseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // ItemDecoration class to add spacing between items
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int columnSpacing;
        private int rowSpacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int columnSpacing, int rowSpacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.columnSpacing = columnSpacing;
            this.rowSpacing = rowSpacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                // Horizontal spacing (left and right) - 40dp for columns
                outRect.left = columnSpacing - column * columnSpacing / spanCount;
                outRect.right = (column + 1) * columnSpacing / spanCount;

                // Vertical spacing (top and bottom) - 30dp for rows
                if (position < spanCount) { // top edge
                    outRect.top = rowSpacing;
                }
                outRect.bottom = rowSpacing; // bottom edge
            } else {
                // Horizontal spacing (left and right) - 40dp for columns
                outRect.left = column * columnSpacing / spanCount;
                outRect.right = columnSpacing - (column + 1) * columnSpacing / spanCount;

                // Vertical spacing (top for items below the first row) - 30dp for rows
                if (position >= spanCount) {
                    outRect.top = rowSpacing;
                }
            }
        }
    }
    // Method to convert dp to px for consistent spacing
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}
class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public HorizontalSpaceItemDecoration(int space) {
        this.space = space;
    }
    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.left = space;
        }
        outRect.right = space;
    }
}
