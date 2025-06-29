package com.group7.pawdicted.mobile.models;

import com.google.firebase.Timestamp;

// MessageItem.java
public class MessageItem {
    private String content;
    private Timestamp time;
    private String sender;

    public MessageItem() {}

    public MessageItem(String content, Timestamp time, String sender) {
        this.content = content;
        this.time = time;
        this.sender = sender;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Timestamp getTime() { return time; }
    public void setTime(Timestamp time) { this.time = time; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
}

