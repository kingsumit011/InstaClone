package com.android.example.instaclone.Model;

import java.util.Calendar;
import java.util.List;

public class Post {
    private String description;
    private Long time;
    private String imageUrl;
    private String postId;
    private String publisher;
    private List<Comment> mList;

    public Post() {

    }

    public Post(String description, String imageUrl, String postId, String publisher, Long time, List<Comment> mList) {
        this.description = description;
        this.time = time;
        this.imageUrl = imageUrl;
        this.postId = postId;
        this.publisher = publisher;
        this.mList = mList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTime() {
        if (time == null) {
            return Long.valueOf(0);
        }
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTimeDiff() {
        String ans = "";
        long millse = Calendar.getInstance().getTimeInMillis() - getTime();
        long mills = Math.abs(millse);
        int Years = (int) (mills / (1000 * 60 * 60 * 24) * 365);
        int Days = (int) (mills / (1000 * 60 * 60 * 24) % 365);
        int Hours = (int) (mills / (1000 * 60 * 60) % 24);
        int Mins = (int) (mills / (1000 * 60) % 60);
        int Secs = (int) ((mills / 1000) % 60);
        if (Years > 0 && Years < 10) {
            ans = Years + " years ago";
        } else if (Days != 0) {
            ans = Days + " days ago";
        } else if (Hours != 0) {
            ans = Hours + " hours ago";
        } else if (Mins != 0) {
            ans = Mins + " mins ago";
        } else {
            ans = Secs + " secs ago";
        }
        return ans;
    }
}
