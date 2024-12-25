package com.example.deafandmute;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Favourite extends Fragment {

    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String userId;

    public Favourite() {
        // Required empty public constructor
    }

    public static Favourite newInstance() {
        return new Favourite();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        recyclerView = view.findViewById(R.id.recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2); // 2 columns
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(35), dpToPx(15), true));

        courseAdapter = new CourseAdapter(courseList, getContext());
        recyclerView.setAdapter(courseAdapter);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        // Reference to the user's favorite courses
        String language = getString(R.string.lang);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("favouriteCourse/"+language);

        loadFavoriteCourses();

        return view;
    }

    private void loadFavoriteCourses() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> favoriteCourseIds = new ArrayList<>();
                for (DataSnapshot favSnapshot : snapshot.getChildren()) {
                    String courseId = favSnapshot.getValue(String.class);
                    if (courseId != null) {
                        favoriteCourseIds.add(courseId);
                    }
                }
                fetchFavoriteCourseDetails(favoriteCourseIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load favorite courses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFavoriteCourseDetails(List<String> favoriteCourseIds) {
        String language = getString(R.string.lang);
        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference("courses/"+language);
        courseList.clear();

        coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);
                    if (course != null && favoriteCourseIds.contains(courseSnapshot.getKey())) {
                        course.setCourseId(courseSnapshot.getKey());
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
                outRect.left = columnSpacing - column * columnSpacing / spanCount;
                outRect.right = (column + 1) * columnSpacing / spanCount;
                if (position < spanCount) { // top edge
                    outRect.top = rowSpacing;
                }
                outRect.bottom = rowSpacing; // bottom edge
            } else {
                outRect.left = column * columnSpacing / spanCount;
                outRect.right = columnSpacing - (column + 1) * columnSpacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = rowSpacing;
                }
            }
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}
