package com.example.showtime.RecyclerView;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.ChatItem.BotImageMessage;
import com.example.showtime.ChatItem.BotMessage;
import com.example.showtime.ChatItem.ChatItem;
import com.example.showtime.ChatItem.RateBanner;
import com.example.showtime.ChatItem.TextMessage;
import com.example.showtime.ChatItem.TicketBanner;
import com.example.showtime.ChatItem.UserMessage;
import com.example.showtime.R;
import com.example.showtime.Reservation.Reservation;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatItem> chatItems = new ArrayList<>();
    private final Context context;
    private final ItemSelectionListener listener;

    public ChatRecyclerViewAdapter(Context context, ItemSelectionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    // View types
    @Override
    public int getItemViewType(int position) {
        ChatItem item = chatItems.get(position);
        if (item instanceof UserMessage) return ChatItem.TYPE_USER;
        if (item instanceof BotMessage) return ChatItem.TYPE_BOT;
        if (item instanceof TextMessage) return ChatItem.TYPE_TEXT;
        if (item instanceof TicketBanner) return ChatItem.TYPE_TICKET_BANNER;
        if (item instanceof BotImageMessage) return ChatItem.TYPE_BOT_IMAGE;
        if (item instanceof RateBanner) return ChatItem.TYPE_RATE_BANNER;

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
        } else if (viewType == ChatItem.TYPE_BOT_IMAGE) {
            View view = inflater.inflate(R.layout.msg_img, parent, false);
            return new BotImageViewHolder(view, context);
        } else if (viewType == ChatItem.TYPE_RATE_BANNER) {
            View view = inflater.inflate(R.layout.banner_rate, parent, false);
            return new RateBannerViewHolder(view, listener);
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
        } else if (holder instanceof BotImageViewHolder) {
            ((BotImageViewHolder) holder).bind((BotImageMessage) item);
        } else if (holder instanceof RateBannerViewHolder) {
            ((RateBannerViewHolder) holder).bind((RateBanner) item);
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

    private static SpannableStringBuilder formatMarkdown(String msg) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        Pattern pattern = Pattern.compile("(\\*\\*[^*]+\\*\\*|\\*[^*]+\\*)");
        Matcher matcher = pattern.matcher(msg);

        int lastEnd = 0;
        while (matcher.find()) {
            // Add plain text before the matched part
            builder.append(msg.substring(lastEnd, matcher.start()));

            String match = matcher.group();
            boolean isBold = match.startsWith("**");

            String innerText = match.substring(isBold ? 2 : 1, match.length() - (isBold ? 2 : 1));
            SpannableString span = new SpannableString(innerText);
            span.setSpan(new StyleSpan(isBold ? Typeface.BOLD : Typeface.ITALIC), 0, innerText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.append(span);

            lastEnd = matcher.end();
        }

        // Append remaining text
        builder.append(msg.substring(lastEnd));
        return builder;
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
            textView.setText(formatMarkdown(msg.getMessage()));
        }
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.msg_textview);
        }

        public void bind(TextMessage msg) {
            textView.setText(formatMarkdown(msg.getMessage()));
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

    static class BotImageViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final ImageView img;

        public BotImageViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            img = itemView.findViewById(R.id.msg_img);
        }

        public void bind(BotImageMessage botImageMessage) {
            img.setImageResource(botImageMessage.getResourceId());

            // Show image popup when clicked
            img.setOnClickListener(v -> {
                // Enlarge image from bot message when pressed
                LayoutInflater inflater = LayoutInflater.from(context);
                View popupView = inflater.inflate(R.layout.popup_enlarged_image, null);

                ImageView enlargedImage = popupView.findViewById(R.id.enlarged_image);
                enlargedImage.setImageResource(botImageMessage.getResourceId());

                // Create the dialog
                AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                        .setView(popupView)
                        .create();

                // Show the dialog
                dialog.show();
            });
        }
    }

    static class RateBannerViewHolder extends RecyclerView.ViewHolder {
        private final ItemSelectionListener listener;
        private final TextView showName, date, time;
        public final RatingBar ratingBar;

        public RateBannerViewHolder(@NonNull View itemView, ItemSelectionListener listener) {
            super(itemView);
            this.listener = listener;

            showName = itemView.findViewById(R.id.rate_showName);
            date = itemView.findViewById(R.id.rate_date);
            time = itemView.findViewById(R.id.rate_time);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }

        public void bind(RateBanner rateBanner) {
            Reservation reservation = rateBanner.getReservation();

            showName.setText(reservation.getShowName());
            date.setText(reservation.getDate());
            time.setText(reservation.getTime());

            ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) ->
                    listener.onRating(ratingBar, reservation.getShowName(), (int) rating)
            );
        }
    }
}
