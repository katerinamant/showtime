package com.example.showtime.ChatPage;

import com.example.showtime.ChatItem.BotMessage;
import com.example.showtime.ChatItem.UserMessage;

public class ChatPagePresenter {
    public UserMessage getNewUserMessage(String msg) {
        return new UserMessage(msg);
    }

    public BotMessage getNewBotMessage(String msg) {
        return new BotMessage(msg);
    }
}
