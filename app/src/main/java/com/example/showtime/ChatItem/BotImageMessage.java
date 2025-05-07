package com.example.showtime.ChatItem;

public class BotImageMessage extends ChatItem {
    public int resourceId;

    public BotImageMessage(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int img) {
        this.resourceId = img;
    }
}
