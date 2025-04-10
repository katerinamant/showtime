package com.example.showtime.ChatPage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.ChatItem.BotMessage;
import com.example.showtime.ChatItem.UserMessage;
import com.example.showtime.HelpPage.HelpPageActivity;
import com.example.showtime.LandingPage.LandingPageActivity;
import com.example.showtime.R;
import com.example.showtime.Utils.Utils;

public class ChatPageActivity extends AppCompatActivity {
    private ChatPageViewModel viewModel;
    String user_input;
    UserMessage userMessage;
    BotMessage botMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        // Get user input from LandingPageActivity
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            user_input = intent.getStringExtra(Utils.USER_INPUT);
        }

        viewModel = new ViewModelProvider(this).get(ChatPageViewModel.class);

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

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.chat_page_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ChatRecyclerViewAdapter adapter = new ChatRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);

        // Add user's first message
        userMessage = viewModel.getPresenter().getNewUserMessage(user_input);
        adapter.addItem(userMessage);

        // Set up chat input functionality
        // Enable scrolling in the chat input box
        EditText chatInput = findViewById(R.id.chat_input);
        chatInput.setMovementMethod(new ScrollingMovementMethod());
        chatInput.setVerticalScrollBarEnabled(true);
        chatInput.setMaxLines(3); // Max lines to have before enabling scrolling
        chatInput.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

        // Send user message on click on "send" icon
        ImageView send_icon = findViewById(R.id.send_button);
        send_icon.setOnClickListener(v -> {
            user_input = chatInput.getText().toString().trim();
            userMessage = viewModel.getPresenter().getNewUserMessage(user_input);
            adapter.addItem(userMessage);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            chatInput.setText("");

            // TODO Disable send option after sending the message (to wait for bot response)
        });
    }
}
