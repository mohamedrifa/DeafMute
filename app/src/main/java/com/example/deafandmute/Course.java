package com.example.deafandmute;
public class Course {
    private String courseId;
    private String courseIconUrl;
    private String courseName;
    private String description;
    private double rating;
    private int enrollmentCount;
    private boolean isFavorite;

    // Required empty constructor
    public Course() { }
    // Full constructor
    public Course(String courseId, String courseIconUrl, String courseName, double rating, int enrollmentCount,String description, boolean isFavorite) {
        this.courseId = courseId;
        this.courseIconUrl = courseIconUrl;
        this.courseName = courseName;
        this.rating = rating;
        this.enrollmentCount = enrollmentCount;
        this.description = description;
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseIconUrl() {
        return courseIconUrl;
    }
    public void setCourseIconUrl(String courseIconUrl) {
        this.courseIconUrl = courseIconUrl;
    }
    public String getCourseName() {
        return courseName;
    }
    public  String getDescription() {
        return description;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
    public int getEnrollmentCount() {
        return enrollmentCount;
    }
    public void setEnrollmentCount(int enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }
}

