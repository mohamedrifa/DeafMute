package com.example.deafandmute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private FirebaseAuth mAuth;
    private List<Course> courseList;
    private Context context;

    public CourseAdapter(List<Course> courseList, Context context) {
        this.courseList = courseList;
        this.context = context;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseName.setText(course.getCourseName());
        holder.courseRating.setText(String.valueOf(course.getRating()));
        holder.enrollmentCount.setText(String.valueOf(course.getEnrollmentCount()) + " new Signs");

        // Load course icon using Glide
        Glide.with(context).load(course.getCourseIconUrl()).into(holder.courseIcon);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userId = firebaseUser.getUid();  // Replace with actual user ID
        DatabaseReference userRef = holder.databaseReference.child(userId).child("favouriteCourse");

        // Check if the course ID exists in the favorite list and set visibility accordingly
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> favoriteCourses = (List<String>) task.getResult().getValue();

                if (favoriteCourses != null && favoriteCourses.contains(course.getCourseId()))
                    holder.onfavorite.setVisibility(View.VISIBLE);
                else
                    holder.onfavorite.setVisibility(View.INVISIBLE);
            }
        });

        // Set up the favorite button click listener
        holder.btnfavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.onfavorite.getVisibility() == View.VISIBLE) {
                    holder.onfavorite.setVisibility(View.INVISIBLE);
                    updateFavoriteCourses(course.getCourseId(), false, holder);  // Remove from favorites
                } else {
                    holder.onfavorite.setVisibility(View.VISIBLE);
                    updateFavoriteCourses(course.getCourseId(), true, holder);  // Add to favorites
                }
            }
        });
    }


    // Method to add or remove a course ID in the user's favorite course list in Firebase
    private void updateFavoriteCourses(String courseId, boolean add, CourseViewHolder holder) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        DatabaseReference userRef = holder.databaseReference.child(userId).child("favouriteCourse");

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> favoriteCourses;

                // Check if `favouriteCourse` exists and is a List, otherwise create a new list
                if (task.getResult().exists() && task.getResult().getValue() instanceof List) {
                    favoriteCourses = (List<String>) task.getResult().getValue();
                } else {
                    favoriteCourses = new ArrayList<>();
                }

                // Add or remove the course ID from the list
                if (add) {
                    if (!favoriteCourses.contains(courseId)) {
                        favoriteCourses.add(courseId);
                    }
                } else {
                    favoriteCourses.remove(courseId);
                }

                // Update the favorite courses list in Firebase
                userRef.setValue(favoriteCourses).addOnCompleteListener(updateTask -> {
                    if (!updateTask.isSuccessful()) {
                        // Handle failure
                    }
                });
            }
        });
    }




    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        private DatabaseReference databaseReference;
        ImageView courseIcon, onfavorite;
        TextView courseName, courseRating, enrollmentCount;
        LinearLayout btnfavorite;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseIcon = itemView.findViewById(R.id.course_icon);
            courseName = itemView.findViewById(R.id.course_name);
            courseRating = itemView.findViewById(R.id.course_rating);
            enrollmentCount = itemView.findViewById(R.id.enrollment_count);
            btnfavorite = itemView.findViewById(R.id.favoritebutton);
            onfavorite = itemView.findViewById(R.id.favorited);

            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        }
    }
}

