package com.example.showtime.FaqPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.R;
import com.example.showtime.Utils.Faq;

import java.util.List;

public class FaqRecyclerViewAdapter extends RecyclerView.Adapter<FaqRecyclerViewAdapter.ViewHolder> {
    private final List<Faq> faqs;

    public FaqRecyclerViewAdapter(List<Faq> faqs) {
        this.faqs = faqs;
    }

    @NonNull
    @Override
    public FaqRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_faq, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FaqRecyclerViewAdapter.ViewHolder holder, int position) {
        final Faq currentFaq = faqs.get(position);
        String question = currentFaq.getQuestion();
        holder.question.setText(question);
        String answer = currentFaq.getAnswer();
        holder.answer.setText(answer);
    }

    @Override
    public int getItemCount() {
        return faqs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView question, answer;

        public ViewHolder(@NonNull View view) {
            super(view);
            question = view.findViewById(R.id.faq_question);
            answer = view.findViewById(R.id.faq_answer);
        }
    }
}
