package com.group7.pawdicted.mobile.models;

// MessageItem.java
public class MessageItem {
    private String content;
    private long time;
    private String sender;

    public MessageItem() {}

    public MessageItem(String content, long time, String sender) {
        this.content = content;
        this.time = time;
        this.sender = sender;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
}

