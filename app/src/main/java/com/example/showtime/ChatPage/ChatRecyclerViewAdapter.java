package com.example.showtime.ChatPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.ChatItem.BotMessage;
import com.example.showtime.ChatItem.ChatItem;
import com.example.showtime.ChatItem.TextMessage;
import com.example.showtime.ChatItem.TicketBanner;
import com.example.showtime.ChatItem.UserMessage;
import com.example.showtime.R;
import com.example.showtime.Reservation.Reservation;

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
        if (item instanceof TextMessage) return ChatItem.TYPE_TEXT;
        if (item instanceof TicketBanner) return ChatItem.TYPE_TICKET_BANNER;

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
        } else if (viewType == ChatItem.TYPE_TEXT) {
            View view = inflater.inflate(R.layout.msg_text, parent, false);
            return new TextViewHolder(view);
        } else if (viewType == ChatItem.TYPE_TICKET_BANNER) {
            View view = inflater.inflate(R.layout.banner_ticket, parent, false);
            return new TicketBannerViewHolder(view);
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
        } else if (holder instanceof TextViewHolder) {
            ((TextViewHolder) holder).bind((TextMessage) item);
        } else if (holder instanceof TicketBannerViewHolder) {
            ((TicketBannerViewHolder) holder).bind((TicketBanner) item);
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

    // Delete latest message
    public void deleteLastItem() {
        if (!chatItems.isEmpty()) {
            int lastIndex = chatItems.size() - 1;
            chatItems.remove(lastIndex);
            notifyItemRemoved(lastIndex);
        }
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

    static class TextViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.msg_textview);
        }

        public void bind(TextMessage msg) {
            textView.setText(msg.getMessage());
        }
    }

    static class TicketBannerViewHolder extends RecyclerView.ViewHolder {
        private final TextView reservationCode, date, time, showName, customerName, totalPrice;

        public TicketBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            reservationCode = itemView.findViewById(R.id.ticket_res_code);
            date = itemView.findViewById(R.id.ticket_date);
            time = itemView.findViewById(R.id.ticket_time);
            showName = itemView.findViewById(R.id.ticket_show);
            customerName = itemView.findViewById(R.id.ticket_customer_name);
            totalPrice = itemView.findViewById(R.id.ticket_total_price);
        }

        public void bind(TicketBanner ticketBanner) {
            Reservation reservation = ticketBanner.getReservation();

            reservationCode.setText(reservation.getReservationCode());
            date.setText(reservation.getDate());
            time.setText(reservation.getTime());
            showName.setText(reservation.getShowName());
            customerName.setText(reservation.getCustomerName());
            totalPrice.setText(String.format("%s€", reservation.getTotalPrice()));
        }
    }
}
