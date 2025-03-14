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
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null) {
            // If user is null, prevent interactions and return
            holder.onfavorite.setVisibility(View.INVISIBLE);
            holder.btnfavorite.setOnClickListener(null);
            return;
        }

        String userId = firebaseUser.getUid();
        String language = context.getString(R.string.lang);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId).child("favouriteCourse").child(language);

        Course course = courseList.get(position);
        holder.courseName.setText(course.getCourseName());
        holder.courseRating.setText(String.valueOf(course.getRating()));
        holder.enrollmentCount.setText(String.valueOf(course.getEnrollmentCount()) + context.getString(R.string.new_signs));

        // Load course icon using Glide
        Glide.with(context).load(course.getCourseIconUrl()).into(holder.courseIcon);

        // Fetch favorite status
        userRef.child(course.getCourseId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                holder.onfavorite.setVisibility(View.VISIBLE);
            } else {
                holder.onfavorite.setVisibility(View.INVISIBLE);
            }
        });

        // Handle favorite button clicks
        holder.btnfavorite.setOnClickListener(v -> {
            if (holder.onfavorite.getVisibility() == View.VISIBLE) {
                holder.onfavorite.setVisibility(View.INVISIBLE);
                userRef.child(course.getCourseId()).removeValue();
            } else {
                holder.onfavorite.setVisibility(View.VISIBLE);
                userRef.child(course.getCourseId()).setValue(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
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
        }
    }
}



