package com.group7.pawdicted.mobile.models;

// ChatRoom.java
import java.util.List;
import java.util.ArrayList;

public class ChatRoom {
    private String chat_id;
    private String customer_id;
    private List<ChatMessage> customerSent;
    private List<ChatMessage> pawdictedSent;

    public ChatRoom() {
        customerSent = new ArrayList<>();
        pawdictedSent = new ArrayList<>();
    }

    public ChatRoom(String chat_id, String customer_id) {
        this.chat_id = chat_id;
        this.customer_id = customer_id;
        this.customerSent = new ArrayList<>();
        this.pawdictedSent = new ArrayList<>();
    }

    // Getters and Setters
    public String getChat_id() { return chat_id; }
    public void setChat_id(String chat_id) { this.chat_id = chat_id; }
    public String getCustomer_id() { return customer_id; }
    public void setCustomer_id(String customer_id) { this.customer_id = customer_id; }
    public List<ChatMessage> getCustomerSent() { return customerSent; }
    public void setCustomerSent(List<ChatMessage> customerSent) { this.customerSent = customerSent; }
    public List<ChatMessage> getPawdictedSent() { return pawdictedSent; }
    public void setPawdictedSent(List<ChatMessage> pawdictedSent) { this.pawdictedSent = pawdictedSent; }
}

