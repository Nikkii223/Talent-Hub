package com.app.talenthub.model;

public class Comment {

    private String id;
    private String name;
    private String profileUrl;
    private String comment;
    private long time;

    public Comment() {
    }

    public Comment(String id, String name, String profileUrl, String comment, long time) {
        this.id = id;
        this.name = name;
        this.profileUrl = profileUrl;
        this.comment = comment;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }



    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


}
