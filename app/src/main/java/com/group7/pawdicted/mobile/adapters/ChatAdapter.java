package com.group7.pawdicted.mobile.adapters;

// ChatAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.R;
import com.group7.pawdicted.mobile.models.MessageItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CUSTOMER = 1;
    private static final int TYPE_PAWDICTED = 2;

    private List<MessageItem> messages;
    private SimpleDateFormat timeFormat;

    public ChatAdapter(List<MessageItem> messages) {
        this.messages = messages;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSender().equals("customer") ? TYPE_CUSTOMER : TYPE_PAWDICTED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CUSTOMER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_customer, parent, false);
            return new CustomerMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_pawdicted, parent, false);
            return new PawdictedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageItem message = messages.get(position);

        if (holder instanceof CustomerMessageViewHolder) {
            ((CustomerMessageViewHolder) holder).bind(message);
        } else if (holder instanceof PawdictedMessageViewHolder) {
            ((PawdictedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<MessageItem> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    class CustomerMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtTime;

        CustomerMessageViewHolder(View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txt_message);
            txtTime = itemView.findViewById(R.id.txt_time);
        }

        void bind(MessageItem message) {
            txtMessage.setText(message.getContent());
            txtTime.setText(timeFormat.format(message.getTime().toDate()));
        }
    }

    class PawdictedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtTime;

        PawdictedMessageViewHolder(View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txt_message);
            txtTime = itemView.findViewById(R.id.txt_time);
        }

        void bind(MessageItem message) {
            txtMessage.setText(message.getContent());
            txtTime.setText(timeFormat.format(message.getTime().toDate()));
        }
    }
}
