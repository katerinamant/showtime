package com.example.showtime.ChatPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.ChatItem.BotMessage;
import com.example.showtime.ChatItem.UserMessage;
import com.example.showtime.R;
import com.example.showtime.ChatItem.ChatItem;

import java.util.ArrayList;
import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatItem> chatItems = new ArrayList<>();
    private final Context context;

    public ChatRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    // View types
    @Override
    public int getItemViewType(int position) {
        ChatItem item = chatItems.get(position);
        if (item instanceof UserMessage) return ChatItem.TYPE_USER;
        if (item instanceof BotMessage) return ChatItem.TYPE_BOT;

        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Create the appropriate ViewHolder
        if (viewType == ChatItem.TYPE_USER) {
            View view = inflater.inflate(R.layout.msg_user, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == ChatItem.TYPE_BOT) {
            View view = inflater.inflate(R.layout.msg_bot, parent, false);
            return new BotViewHolder(view);
        }

        throw new IllegalArgumentException("Unknown viewType " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatItem item = chatItems.get(position);

        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind((UserMessage) item);
        } else if (holder instanceof BotViewHolder) {
            ((BotViewHolder) holder).bind((BotMessage) item);
        }
    }

    @Override
    public int getItemCount() {
        return chatItems.size();
    }

    // Dynamically add new messages
    public void addItem(ChatItem item) {
        chatItems.add(item);
        notifyItemInserted(chatItems.size() - 1);
    }

    // Clear messages list
    public void clearChat() {
        chatItems.clear();
        notifyDataSetChanged();
    }

    // --- ViewHolder classes ---

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.msg_user_text);
        }

        public void bind(UserMessage msg) {
            textView.setText(msg.getMessage());
        }
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.msg_bot_text);
        }

        public void bind(BotMessage msg) {
            textView.setText(msg.getMessage());
        }
    }
}
