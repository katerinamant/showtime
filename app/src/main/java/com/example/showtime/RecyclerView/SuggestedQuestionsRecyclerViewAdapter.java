package com.example.showtime.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.R;
import com.example.showtime.Utils.SuggestedQuestion;

import java.util.ArrayList;
import java.util.List;

public class SuggestedQuestionsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<SuggestedQuestion> items = new ArrayList<>();
    private final Context context;
    private final ItemSelectionListener listener;
    private boolean showHelpButton;

    // View Types
    private static final int TYPE_HELP_BUTTON = 0;
    private static final int TYPE_QUESTION = 1;

    public SuggestedQuestionsRecyclerViewAdapter(Context context, ItemSelectionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate based on type of button
        if (viewType == TYPE_HELP_BUTTON) {
            View view = inflater.inflate(R.layout.list_item_question_coral, parent, false);
            return new HelpButtonViewHolder(view, listener);
        } else {
            View view = inflater.inflate(R.layout.list_item_question, parent, false);
            return new QuestionViewHolder(view, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HELP_BUTTON) {
            ((HelpButtonViewHolder) holder).bind();
        } else {
            int adjustedPosition = showHelpButton ? position - 1 : position;
            final SuggestedQuestion question = items.get(adjustedPosition);
            ((QuestionViewHolder) holder).setSuggestedQuestion(question);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (showHelpButton && position == 0) {
            return TYPE_HELP_BUTTON;
        }
        return TYPE_QUESTION;
    }

    @Override
    public int getItemCount() {
        return items.size() + (showHelpButton ? 1 : 0);
    }

    // Used to determine if the first button
    // should be the help button
    public void setShowHelpButton(Boolean showHelpButton) {
        this.showHelpButton = showHelpButton;
    }

    // Update questions list
    @SuppressLint("NotifyDataSetChanged")
    public void setQuestions(List<SuggestedQuestion> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    // Clear questions list
    @SuppressLint("NotifyDataSetChanged")
    public void clearQuestions() {
        items.clear();
        showHelpButton = false;
        notifyDataSetChanged();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemSelectionListener listener;
        private SuggestedQuestion suggestedQuestion;
        public final TextView questionText;

        public QuestionViewHolder(@NonNull View view, ItemSelectionListener listener) {
            super(view);
            this.listener = listener;

            this.questionText = view.findViewById(R.id.question_text);
            view.setOnClickListener(this);
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

    public static class HelpButtonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemSelectionListener listener;

        public HelpButtonViewHolder(@NonNull View view, ItemSelectionListener listener) {
            super(view);
            this.listener = listener;
            view.setOnClickListener(this);
        }

        public void bind() {

        }

        @Override
        public void onClick(View v) {
            listener.onHelpButtonClick();
        }
    }

}
