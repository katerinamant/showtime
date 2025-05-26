package com.example.showtime.RecyclerView;

import android.widget.RatingBar;

import com.example.showtime.Reservation.Reservation;
import com.example.showtime.Utils.SuggestedQuestion;

public interface ItemSelectionListener {
    /**
     * Show a popup for the user
     * to confirm the reservation they selected.
     */
    void onRating(RatingBar ratingBar, Reservation reservation, int rating);

    /**
     * Send to the bot the suggested question
     * that was chosen to the chat.
     */
    void onSuggestedQuestionClick(SuggestedQuestion suggestedQuestion);

    /**
     * Add customer support to the chat.
     */
    void onHelpButtonClick();
}
