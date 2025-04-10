package com.example.showtime.FaqPage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.HelpPage.HelpPageActivity;
import com.example.showtime.LandingPage.LandingPageActivity;
import com.example.showtime.R;
import com.example.showtime.Utils.Faq;
import com.example.showtime.Utils.Utils;

import java.util.List;

public class FaqActivity extends AppCompatActivity {
    private FaqViewModel viewModel;
    int faqCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        // Get FAQ category id from HelpPageActivity
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            faqCategoryId = intent.getIntExtra(Utils.FAQ_CATEGORY_ID, 0);
        }

        viewModel = new ViewModelProvider(this).get(FaqViewModel.class);

        // Logo button
        ImageView headerLogo = findViewById(R.id.header_logo);
        headerLogo.setOnClickListener(view -> {
            // Go to LandingPage
            Intent intent = new Intent(FaqActivity.this, LandingPageActivity.class);
            startActivity(intent);
        });

        // Help button
        ImageView headerHelp = findViewById(R.id.header_help_icon);
        headerHelp.setOnClickListener(view -> {
            // Go to HelpPage
            Intent intent = new Intent(FaqActivity.this, HelpPageActivity.class);
            startActivity(intent);
        });

        // Get list of questions
        List<Faq> faqs = viewModel.getPresenter().getFaqCategory(faqCategoryId);

        // FAQ Recycler View
        RecyclerView recyclerView = findViewById(R.id.faq_page_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FaqRecyclerViewAdapter(faqs));
    }
}
