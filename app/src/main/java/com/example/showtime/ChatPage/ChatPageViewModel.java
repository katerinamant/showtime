package com.example.showtime.ChatPage;

import androidx.lifecycle.ViewModel;

public class ChatPageViewModel extends ViewModel {
    private final ChatPagePresenter presenter;

    public ChatPageViewModel() {
        this.presenter = new ChatPagePresenter();
    }

    public ChatPagePresenter getPresenter() {
        return this.presenter;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // release resources
    }
}
