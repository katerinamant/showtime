package com.example.showtime.ChatItem;

public class UserMessage extends ChatItem {
    public String msg;

    public UserMessage(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }
}
