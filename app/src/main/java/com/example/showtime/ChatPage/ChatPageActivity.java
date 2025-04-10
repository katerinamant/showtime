package com.example.showtime.ChatPage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.ChatItem.BotMessage;
import com.example.showtime.ChatItem.UserMessage;
import com.example.showtime.HelpPage.HelpPageActivity;
import com.example.showtime.LandingPage.LandingPageActivity;
import com.example.showtime.R;
import com.example.showtime.Utils.Utils;

public class ChatPageActivity extends AppCompatActivity {
    String user_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        // Logo button
        ImageView headerLogo = findViewById(R.id.header_logo);
        headerLogo.setOnClickListener(view -> {
            // Go to LandingPage
            Intent intent = new Intent(ChatPageActivity.this, LandingPageActivity.class);
            startActivity(intent);
        });

        // Help button
        ImageView headerHelp = findViewById(R.id.header_help_icon);
        headerHelp.setOnClickListener(view -> {
            // Go to HelpPage
            Intent intent = new Intent(ChatPageActivity.this, HelpPageActivity.class);
            startActivity(intent);
        });

        // Get user input from LandingPageActivity
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            user_input = intent.getStringExtra(Utils.USER_INPUT);
        }

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.chat_page_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ChatRecyclerViewAdapter adapter = new ChatRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);

        // Add user's first message
        adapter.addItem(new UserMessage(user_input));
    }
}
