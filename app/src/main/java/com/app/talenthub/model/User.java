package com.app.talenthub.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String id;
    private String name;
    private String email;
    private String contact;
    private String type;
    private String companyName;
    private long regDate;
    private String userName;
    private String profileUrl;
    private int post=0, following=0, follower=0;
    private String about;
    private int age;
    private String address;
    private String token;

    public User() {
    }

    public User(String id, String name, String email, String contact, String type, String companyName, long regDate, String userName, String token) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.type = type;
        this.companyName = companyName;
        this.regDate = regDate;
        this.userName = userName;
        this.token = token;
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        contact = in.readString();
        type = in.readString();
        companyName = in.readString();
        regDate = in.readLong();
        userName = in.readString();
        profileUrl = in.readString();
        post = in.readInt();
        following = in.readInt();
        follower = in.readInt();
        about = in.readString();
        age = in.readInt();
        address = in.readString();
        token = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public long getRegDate() {
        return regDate;
    }

    public void setRegDate(long regDate) {
        this.regDate = regDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }


    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(contact);
        dest.writeString(type);
        dest.writeString(companyName);
        dest.writeLong(regDate);
        dest.writeString(userName);
        dest.writeString(profileUrl);
        dest.writeInt(post);
        dest.writeInt(following);
        dest.writeInt(follower);
        dest.writeString(about);
        dest.writeInt(age);
        dest.writeString(address);
        dest.writeString(token);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
