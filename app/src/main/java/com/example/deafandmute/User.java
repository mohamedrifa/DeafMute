package com.example.deafandmute;

public class User {
    public String username;
    public String email;
    public String mobile;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String mobile) {
        this.username = username;
        this.email = email;
        this.mobile = mobile;
    }
}
