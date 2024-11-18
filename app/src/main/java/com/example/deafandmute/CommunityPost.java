package com.example.deafandmute;

public class CommunityPost {
    private String authorId;
    private String authorName;
    private String content;
    private long timestamp;
    private int likes;

    // Empty constructor for Firebase
    public CommunityPost() {}

    public CommunityPost(String authorId, String authorName, String content, long timestamp, int likes) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = likes;
    }

    public String getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public int getLikes() { return likes; }
}

