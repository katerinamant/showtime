package com.example.showtime.ChatPage;

import com.example.showtime.ChatItem.BotMessage;
import com.example.showtime.ChatItem.TextMessage;
import com.example.showtime.ChatItem.TicketBanner;
import com.example.showtime.ChatItem.UserMessage;
import com.example.showtime.Reservation.Reservation;

public class ChatPagePresenter {
    public UserMessage getNewUserMessage(String msg) {
        return new UserMessage(msg);
    }

    public BotMessage getNewBotMessage(String msg) {
        return new BotMessage(msg);
    }

    public TextMessage getNewTextMessage(String msg) {
        return new TextMessage(msg);
    }

    public TicketBanner getNewTicketBanner(Reservation reservation) {
        return new TicketBanner(reservation);
    }
}
