package com.example.showtime.HelpPage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.showtime.ChatPage.ChatPageActivity;
import com.example.showtime.LandingPage.LandingPageActivity;
import com.example.showtime.R;

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
    }
}
