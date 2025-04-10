package com.example.showtime.ChatItem;

public class TextMessage extends ChatItem {
    public String msg;

    public TextMessage(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }
}
