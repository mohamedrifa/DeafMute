package com.example.deafandmute;

public class User {
    public String username;
    public String email;
    public String mobile;
    public String language;
    public String profilePhoto;
    public String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String mobile, String profilePhoto, String password) {
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.profilePhoto = profilePhoto;
        this.password = password;
    }
    public User(String language){
        this.language = language;
    }
    public String getprofilePhoto() {
        return profilePhoto;
    }
}
