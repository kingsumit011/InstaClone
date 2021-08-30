package com.android.example.instaclone.Model;

public class User {

    private String userName, emailId, profileimg, bio, id;

    public User() {

    }

    public User(String UserName, String Bio, String EmailId, String id, String imageURL) {
        this.userName = UserName;
        this.emailId = EmailId;
        this.profileimg = imageURL;
        this.bio = Bio;
        this.id = id;
    }

    public String getProfileimg() {
        return profileimg;
    }

    public void setProfileimg(String profileimg) {
        this.profileimg = profileimg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
