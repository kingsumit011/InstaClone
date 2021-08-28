package com.android.example.instaclone.Model;

import java.util.Calendar;

public class Comment {
    String id,commentdata,publisher ;
    long time;

    public Comment(){

    }
    public Comment(String commentdata,String id, String publisher, long time) {
        this.id = id;
        this.commentdata = commentdata;
        this.publisher = publisher;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommentdata() {
        return commentdata;
    }

    public void setCommentdata(String comment) {
        this.commentdata = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTimeDiff(){
        String ans ="";
        long millse = Calendar.getInstance().getTimeInMillis()-getTime() ;
        long mills = Math.abs(millse);
        int Years = (int) (mills/(1000*60*60*24)*365);
        int Days = (int) (mills/(1000*60*60*24)%365);
        int Hours = (int) (mills/(1000 * 60 * 60)%24);
        int Mins = (int) (mills/(1000*60) % 60);
        int Secs = (int) ((mills / 1000) % 60);
        if(Years >0 && Years < 10){
            ans = Years + " years ago";
        }else if(Days != 0){
            ans = Days + " days ago";
        }else if(Hours != 0){
            ans = Hours + " hours ago";
        }else if(Mins != 0 ){
            ans = Mins +" mins ago";
        }else{
            ans = Secs + " secs ago";
        }
        return ans;
    }
}
