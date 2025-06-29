package com.group7.pawdicted.mobile.models;

import com.google.firebase.Timestamp;

// ChatMessage.java
public class ChatMessage {
    private String content;
    private Timestamp time;

    public ChatMessage() {}

    public ChatMessage(String content, Timestamp time) {
        this.content = content;
        this.time = time;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Timestamp getTime() { return time; }
    public void setTime(Timestamp time) { this.time = time; }
}

