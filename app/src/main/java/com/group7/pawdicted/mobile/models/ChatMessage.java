package com.group7.pawdicted.mobile.models;

// ChatMessage.java
public class ChatMessage {
    private String content;
    private long time;

    public ChatMessage() {}

    public ChatMessage(String content, long time) {
        this.content = content;
        this.time = time;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }
}

