package com.example.showtime.ChatPage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.ChatItem.BotMessage;
import com.example.showtime.ChatItem.UserMessage;
import com.example.showtime.HelpPage.HelpPageActivity;
import com.example.showtime.LandingPage.LandingPageActivity;
import com.example.showtime.R;
import com.example.showtime.Utils.Utils;

import org.json.JSONException;

public class ChatPageActivity extends AppCompatActivity {
    private ChatPageViewModel viewModel;
    String userInput;
    UserMessage userMessage;
    BotMessage botMessage;
    final PopupWindow[] clearChatPopup = {null};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        RelativeLayout relativeLayout = findViewById(R.id.relative_chat_page);

        // Get user input from LandingPageActivity
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            userInput = intent.getStringExtra(Utils.USER_INPUT);
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
        userMessage = viewModel.getPresenter().getNewUserMessage(userInput);
        adapter.addItem(userMessage);

        // Set up chat input functionality
        // Enable scrolling in the chat input box
        EditText chatInput = findViewById(R.id.chat_input);
        chatInput.setMovementMethod(new ScrollingMovementMethod());
        chatInput.setVerticalScrollBarEnabled(true);
        chatInput.setMaxLines(3); // Max lines to have before enabling scrolling
        chatInput.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

        // Send user message on click on "send" icon
        ImageView sendBtn = findViewById(R.id.send_button);
        sendBtn.setOnClickListener(v -> {
            userInput = chatInput.getText().toString().trim();
            userMessage = viewModel.getPresenter().getNewUserMessage(userInput);
            adapter.addItem(userMessage);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            chatInput.setText("");

            // TODO Disable send option after sending the message (to wait for bot response)
        });

        // Show popup when clear chat button is clicked
        ImageView clearChatBtn = findViewById(R.id.clear_chat_button);
        clearChatBtn.setOnClickListener(v -> {
            if (adapter.getItemCount() == 0) return;

            // Inflate popup layout
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View pop_up = layoutInflater.inflate(R.layout.popup_confirm_clear_chat, null);

            // Create and show confirm rating popup
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            // The array is used because
            // the variable needs to be final
            // for the onClickListener
            clearChatPopup[0] = new PopupWindow(pop_up, width, height, true);
            clearChatPopup[0].showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);

            // Dismiss popup when "Cancel" button is clicked
            Button cancelButton = pop_up.findViewById(R.id.btn_cancel_clear_chat);
            cancelButton.setOnClickListener(cancel -> {
                clearChatPopup[0].dismiss();
                clearChatPopup[0] = null;
            });

            Button confirmButton = pop_up.findViewById(R.id.btn_confirm_clear_chat);
            confirmButton.setOnClickListener(confirm -> {
                adapter.clearChat();
                clearChatPopup[0].dismiss();
                clearChatPopup[0] = null;
            });
        });
    }
}
