package com.example.showtime.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.Utils.SuggestedQuestion;
import com.example.showtime.R;

import java.util.ArrayList;
import java.util.List;

public class SuggestedQuestionsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<SuggestedQuestion> items = new ArrayList<>();
    private final Context context;
    private final ItemSelectionListener listener;

    public SuggestedQuestionsRecyclerViewAdapter(Context context, ItemSelectionListener listener) {
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

    // Update questions list
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
        private final ItemSelectionListener listener;
        private SuggestedQuestion suggestedQuestion;
        public final TextView questionText;

        public ViewHolder(@NonNull View view, ItemSelectionListener listener) {
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
}
