package com.group7.pawdicted;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.group7.pawdicted.mobile.adapters.ChatAdapter;
import com.group7.pawdicted.mobile.models.MessageItem;
import com.group7.pawdicted.mobile.services.ChatService;
import com.group7.pawdicted.mobile.services.CustomerManager;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private EditText edtMessage;
    private Button btnSend;
    private RecyclerView recyclerChat;
    private ImageView imgBack;
    private ChatAdapter chatAdapter;
    private ChatService chatService;
    private List<MessageItem> messageList;
    private ListenerRegistration messageListener;
    private String customerId;
    private CustomerManager customerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupCustomerManager();
        setupChatService();
        setupRecyclerView();
        setupSendButton();
        setupBackButton();
        loadMessages();
        displayCustomerInfo();
    }

    private void initViews() {
        edtMessage = findViewById(R.id.edt_message);
        btnSend = findViewById(R.id.btnSend);
        recyclerChat = findViewById(R.id.recycler_chat);
        imgBack = findViewById(R.id.imgBack);
    }

    private void setupCustomerManager() {
        customerManager = CustomerManager.getInstance(this);
        customerId = customerManager.getCustomerId();
        Log.d("ChatActivity", "Customer ID: " + customerId);
        Log.d("ChatActivity", "Is logged in: " + customerManager.isLoggedIn());
    }

    private void setupChatService() {
        chatService = new ChatService(customerId);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        recyclerChat.setAdapter(chatAdapter);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSendButton() {
        btnSend.setOnClickListener(v -> sendMessage());

        edtMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void setupBackButton() {
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> {
                finish();
            });
        }
    }

    private void sendMessage() {
        String messageContent = edtMessage.getText().toString().trim();

        if (messageContent.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSend.setEnabled(false);
        btnSend.setText("Đang gửi...");

        chatService.sendCustomerMessage(messageContent, new ChatService.OnMessageSentListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    edtMessage.setText("");
                    btnSend.setEnabled(true);
                    btnSend.setText("➤");
                    Toast.makeText(ChatActivity.this, "Tin nhắn đã được gửi", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    btnSend.setEnabled(true);
                    btnSend.setText("➤");
                    Toast.makeText(ChatActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadMessages() {
        messageListener = chatService.loadAllMessages(new ChatService.OnMessagesLoadedListener() {
            @Override
            public void onSuccess(List<MessageItem> messages) {
                runOnUiThread(() -> {
                    chatAdapter.updateMessages(messages);
                    if (messages.size() > 0) {
                        recyclerChat.scrollToPosition(messages.size() - 1);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "Lỗi load tin nhắn: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void displayCustomerInfo() {
        customerManager.getCustomerInfo(new CustomerManager.OnCustomerInfoListener() {
            @Override
            public void onSuccess(DocumentSnapshot customerDoc, String displayName) {
                runOnUiThread(() -> {
                    String welcomeMessage;
                    if (customerManager.isLoggedIn()) {
                        welcomeMessage = "Xin chào " + displayName + "!";
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Chat - " + displayName);
                        }
                    } else {
                        welcomeMessage = "Chào mừng bạn đến với Pawdicted!";
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Chat - Khách vãng lai");
                        }
                    }

                    Toast.makeText(ChatActivity.this, welcomeMessage, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e("ChatActivity", "Error getting customer info: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "Chào mừng bạn đến với Pawdicted!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showLoginPrompt() {
        if (!customerManager.isLoggedIn()) {
            Toast.makeText(this, "Đăng nhập để có trải nghiệm chat tốt hơn!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageListener != null) {
            messageListener.remove();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh customer info khi quay lại activity (có thể user vừa login)
        if (customerManager != null) {
            String newCustomerId = customerManager.getCustomerId();
            if (!newCustomerId.equals(customerId)) {
                // Customer ID đã thay đổi (có thể vừa login/logout)
                customerId = newCustomerId;
                chatService = new ChatService(customerId);
                loadMessages(); // Reload tin nhắn với customer ID mới
            }
        }
    }
}
