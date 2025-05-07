package com.example.showtime.ChatPage;

import android.content.Context;
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

public class SuggestedQuestionsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<SuggestedQuestion> items = new ArrayList<>();
    private final Context context;
    private final SuggestedQuestionsListener listener;

    public SuggestedQuestionsRecyclerViewAdapter(Context context, SuggestedQuestionsListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_question, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final SuggestedQuestion question = items.get(position);

        ((SuggestedQuestionsRecyclerViewAdapter.ViewHolder) holder).setSuggestedQuestion(question);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setQuestions(List<SuggestedQuestion> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    // Clear questions list
    public void clearQuestions() {
        items.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final SuggestedQuestionsListener listener;
        private SuggestedQuestion suggestedQuestion;
        public final TextView questionText;

        public ViewHolder(@NonNull View view, SuggestedQuestionsListener listener) {
            super(view);
            this.listener = listener;

            this.questionText = view.findViewById(R.id.question_text);
        }

        public void setSuggestedQuestion(SuggestedQuestion suggestedQuestion) {
            this.suggestedQuestion = suggestedQuestion;
            questionText.setText(suggestedQuestion.getQuestion());
        }

        @Override
        public void onClick(View v) {
            listener.onSuggestedQuestionClick(this.suggestedQuestion);
        }
    }

    /**
     * Define an interface in order to pass events to the ChatPageActivity.
     */
    public interface SuggestedQuestionsListener {
        void onSuggestedQuestionClick(SuggestedQuestion suggestedQuestion);
    }
}
