package com.group7.pawdicted.mobile.services;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ListenerRegistration;
import com.group7.pawdicted.mobile.models.ChatMessage;
import com.group7.pawdicted.mobile.models.ChatRoom;
import com.group7.pawdicted.mobile.models.MessageItem;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChatService {
    private static final String TAG = "ChatService";
    private FirebaseFirestore db;
    private String customerId;

    public ChatService(String customerId) {
        this.db = FirebaseFirestore.getInstance();
        this.customerId = customerId;
    }

    public void sendCustomerMessage(String content, OnMessageSentListener listener) {
        ChatMessage message = new ChatMessage(content, com.google.firebase.Timestamp.now());

        DocumentReference chatRef = db.collection("chats").document(customerId);

        chatRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    updateExistingChat(chatRef, message, listener);
                } else {
                    createNewChatRoom(chatRef, message, listener);
                }
            } else {
                Log.e(TAG, "Lỗi kiểm tra chat room", task.getException());
                if (listener != null) {
                    listener.onFailure("Không thể kiểm tra chat room");
                }
            }
        });
    }

    private void updateExistingChat(DocumentReference chatRef, ChatMessage message, OnMessageSentListener listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("customerSent", FieldValue.arrayUnion(message));

        chatRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Tin nhắn đã được gửi thành công");
                    if (listener != null) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi gửi tin nhắn", e);
                    if (listener != null) {
                        listener.onFailure("Không thể gửi tin nhắn: " + e.getMessage());
                    }
                });
    }

    private void createNewChatRoom(DocumentReference chatRef, ChatMessage message, OnMessageSentListener listener) {
        ChatRoom newChatRoom = new ChatRoom(customerId, customerId);
        newChatRoom.getCustomerSent().add(message);

        chatRef.set(newChatRoom)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Chat room mới đã được tạo và tin nhắn đã được gửi");
                    if (listener != null) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tạo chat room", e);
                    if (listener != null) {
                        listener.onFailure("Không thể tạo chat room: " + e.getMessage());
                    }
                });
    }

    public ListenerRegistration loadAllMessages(OnMessagesLoadedListener listener) {
        DocumentReference chatRef = db.collection("chats").document(customerId);

        return chatRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Lỗi load tin nhắn", e);
                if (listener != null) {
                    listener.onFailure("Không thể load tin nhắn: " + e.getMessage());
                }
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                List<MessageItem> allMessages = new ArrayList<>();

                List<HashMap<String, Object>> customerMessages =
                        (List<HashMap<String, Object>>) documentSnapshot.get("customerSent");
                if (customerMessages != null) {
                    for (HashMap<String, Object> msg : customerMessages) {
                        String content = (String) msg.get("content");

                        Object timeObj = msg.get("time");
                        Timestamp timestamp;
                        if (timeObj instanceof Timestamp) {
                            timestamp = (Timestamp) timeObj;
                        } else if (timeObj instanceof Long) {
                            timestamp = new Timestamp(new java.util.Date((Long) timeObj));
                        } else {
                            timestamp = Timestamp.now();
                        }

                        allMessages.add(new MessageItem(content, timestamp, "customer"));
                    }
                }

                List<HashMap<String, Object>> pawdictedMessages =
                        (List<HashMap<String, Object>>) documentSnapshot.get("pawdictedSent");
                if (pawdictedMessages != null) {
                    for (HashMap<String, Object> msg : pawdictedMessages) {
                        String content = (String) msg.get("content");

                        Object timeObj = msg.get("time");
                        Timestamp timestamp;
                        if (timeObj instanceof Timestamp) {
                            timestamp = (Timestamp) timeObj;
                        } else if (timeObj instanceof Long) {
                            timestamp = new Timestamp(new java.util.Date((Long) timeObj));
                        } else {
                            timestamp = Timestamp.now(); // fallback
                        }

                        allMessages.add(new MessageItem(content, timestamp, "pawdicted"));
                    }
                }

                Collections.sort(allMessages, new Comparator<MessageItem>() {
                    @Override
                    public int compare(MessageItem m1, MessageItem m2) {
                        return m1.getTime().compareTo(m2.getTime());
                    }
                });

                if (listener != null) {
                    listener.onSuccess(allMessages);
                }
            } else {
                if (listener != null) {
                    listener.onSuccess(new ArrayList<>());
                }
            }
        });
    }

    public interface OnMessageSentListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnMessagesLoadedListener {
        void onSuccess(List<MessageItem> messages);
        void onFailure(String error);
    }
}
