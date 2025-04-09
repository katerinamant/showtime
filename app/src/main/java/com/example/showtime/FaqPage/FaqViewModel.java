package com.example.showtime.FaqPage;

import androidx.lifecycle.ViewModel;

public class FaqViewModel extends ViewModel {
    private final FaqPresenter presenter;

    public FaqViewModel() {
        this.presenter = new FaqPresenter();
    }

    public FaqPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // release resources
    }
}
