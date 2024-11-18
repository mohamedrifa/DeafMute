package com.example.deafandmute;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deafandmute.Course;
import com.example.deafandmute.CourseAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList = new ArrayList<>();
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
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        // Set GridLayoutManager to create two columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2); // 2 columns
        recyclerView.setLayoutManager(gridLayoutManager); // Set the layout manager
        // Add item decoration to add margin between items (40dp for columns, 30dp for rows)
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(35), dpToPx(15), true));
        courseAdapter = new CourseAdapter(courseList, getContext());
        recyclerView.setAdapter(courseAdapter);
        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("courses");
        // Fetch data from Firebase and update RecyclerView
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseList.clear();
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);
                    if (course != null) {
                        course.setCourseId(courseSnapshot.getKey()); // Set unique courseId from Firebase
                        courseList.add(course);
                    }
                }
                courseAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load courses", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    // ItemDecoration class to add spacing between items
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
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
