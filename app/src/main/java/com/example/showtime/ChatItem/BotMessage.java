package com.example.showtime.ChatItem;

public class BotMessage extends ChatItem {
    public String msg;

    public BotMessage(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }
}
