package com.app.talenthub.model;

public class Notification {

    private String id;
    private String senderId;
    private String senderName;
    private String senderImage;
    private String receiverId;
    private String contentId;
    private String message;
    private long timeStamp;

    public Notification() {
    }

    public Notification(String id, String senderId, String senderName, String senderImage, String receiverId, String message, long timeStamp, String contentId) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverId = receiverId;
        this.message = message;
        this.timeStamp = timeStamp;
        this.contentId = contentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }


    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
}
