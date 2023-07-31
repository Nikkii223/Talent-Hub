package com.app.talenthub.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Content implements Parcelable {

    private String id,  title, artist, type, category, language, caption, videoUrl, imageUrl, userName, userId, profileUrl;
    private long  date;
    private int like =0, comment = 0;

    public Content() {
    }

    public Content(String id, String title, String artist, String type, String category, String language, String caption, String videoUrl, String imageUrl, String userName, String userId, long date, String profileUrl) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.type = type;
        this.category = category;
        this.language = language;
        this.caption = caption;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.userId = userId;
        this.date = date;
        this.profileUrl = profileUrl;
    }

    protected Content(Parcel in) {
        id = in.readString();
        title = in.readString();
        artist = in.readString();
        type = in.readString();
        category = in.readString();
        language = in.readString();
        caption = in.readString();
        videoUrl = in.readString();
        imageUrl = in.readString();
        userName = in.readString();
        userId = in.readString();
        profileUrl = in.readString();
        date = in.readLong();
        like = in.readInt();
        comment = in.readInt();
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(type);
        dest.writeString(category);
        dest.writeString(language);
        dest.writeString(caption);
        dest.writeString(videoUrl);
        dest.writeString(imageUrl);
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeString(profileUrl);
        dest.writeLong(date);
        dest.writeInt(like);
        dest.writeInt(comment);
    }
}
