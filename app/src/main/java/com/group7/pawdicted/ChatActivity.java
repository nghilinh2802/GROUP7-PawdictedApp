package com.group7.pawdicted;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
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
    private MaterialButton btnSend;
    private RecyclerView recyclerChat;
    private ImageView imgBack;
    private ChatAdapter chatAdapter;
    private ChatService chatService;
    private List<MessageItem> messageList;
    private ListenerRegistration messageListener;
    private String customerId;
    private CustomerManager customerManager;

    private boolean isKeyboardVisible = false;
    private boolean isSending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        setupCustomerManager();
        setupChatService();
        setupRecyclerView();
        setupSendButton();
        setupBackButton();
        setupKeyboardListener();
        loadMessages();
        displayCustomerInfo();
    }

    private void setupKeyboardListener() {
        View rootView = findViewById(R.id.main);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) { // Keyboard visible
                if (!isKeyboardVisible) {
                    isKeyboardVisible = true;
                    onKeyboardShown();
                }
            } else { // Keyboard hidden
                if (isKeyboardVisible) {
                    isKeyboardVisible = false;
                    onKeyboardHidden();
                }
            }
        });
    }

    private void onKeyboardShown() {
        // Scroll to bottom khi keyboard hiện
        if (messageList != null && messageList.size() > 0) {
            recyclerChat.post(() -> {
                recyclerChat.scrollToPosition(messageList.size() - 1);
            });
        }
    }

    private void onKeyboardHidden() {
        // Optional: Handle keyboard hidden
        Log.d("ChatActivity", "Keyboard hidden");
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
            // Kiểm tra action ID để chỉ xử lý Enter key
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_DOWN)) {
                sendMessage();
                return true; // Consume event để ngăn chặn xử lý tiếp
            }
            return false;
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
        // Kiểm tra nếu đang gửi thì return
        if (isSending) {
            return;
        }

        String messageContent = edtMessage.getText().toString().trim();

        if (messageContent.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set flag và disable UI
        isSending = true;
        btnSend.setEnabled(false);
        btnSend.setText("...");

        chatService.sendCustomerMessage(messageContent, new ChatService.OnMessageSentListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    edtMessage.setText("");
                    isSending = false; // Reset flag
                    btnSend.setEnabled(true);
                    btnSend.setText("➤");
                    Toast.makeText(ChatActivity.this, "Tin nhắn đã được gửi", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    isSending = false; // Reset flag
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
        if (customerManager != null) {
            String newCustomerId = customerManager.getCustomerId();
            if (!newCustomerId.equals(customerId)) {
                customerId = newCustomerId;
                chatService = new ChatService(customerId);
                loadMessages();
            }
        }
    }
}
