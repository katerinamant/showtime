package com.example.showtime.HelpPage;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.showtime.FaqPage.FaqActivity;
import com.example.showtime.LandingPage.LandingPageActivity;
import com.example.showtime.R;
import com.example.showtime.Utils.Utils;

public class HelpPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);

        // Logo button
        ImageView headerLogo = findViewById(R.id.header_logo);
        headerLogo.setOnClickListener(view -> {
            // Go to LandingPage
            Intent intent = new Intent(HelpPageActivity.this, LandingPageActivity.class);
            startActivity(intent);
        });

        // Set up FAQ button intents
        Button[] buttons = new Button[]{
                findViewById(R.id.btn_faq_1),
                findViewById(R.id.btn_faq_2),
                findViewById(R.id.btn_faq_3),
                findViewById(R.id.btn_faq_4),
                findViewById(R.id.btn_faq_5)
        };

        for (int i = 1; i <= buttons.length; i++) {
            Button btn = buttons[i - 1];

            int finalI = i;
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(HelpPageActivity.this, FaqActivity.class);
                intent.putExtra(Utils.FAQ_CATEGORY_ID, finalI);
                startActivity(intent);
            });
        }

        // Copyright footnote
        TextView copyrightText = findViewById(R.id.copyright_text);
        copyrightText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
