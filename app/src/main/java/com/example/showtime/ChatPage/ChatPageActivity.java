package com.example.showtime.ChatPage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.ChatItem.BotMessage;
import com.example.showtime.ChatItem.UserMessage;
import com.example.showtime.HelpPage.HelpPageActivity;
import com.example.showtime.LandingPage.LandingPageActivity;
import com.example.showtime.R;
import com.example.showtime.Utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ChatPageActivity extends AppCompatActivity {
    private ChatPageViewModel viewModel;
    private RecyclerView recyclerView;
    private ChatRecyclerViewAdapter recyclerViewAdapter;
    String userInput;
    UserMessage userMessage;
    BotMessage botMessage;
    ImageView sendBtn;
    final PopupWindow[] clearChatPopup = {null};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        sendBtn = findViewById(R.id.send_button);

        // Get user input from LandingPageActivity
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            userInput = intent.getStringExtra(Utils.USER_INPUT);
        }

        viewModel = new ViewModelProvider(this).get(ChatPageViewModel.class);

        setUpHeaderButtons();

        setUpRecyclerView();

        // Set up chat input functionality
        // Enable scrolling in the chat input box
        EditText chatInput = findViewById(R.id.chat_input);
        chatInput.setMovementMethod(new ScrollingMovementMethod());
        chatInput.setVerticalScrollBarEnabled(true);
        chatInput.setMaxLines(3); // Max lines to have before enabling scrolling
        chatInput.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

        // Send user message on click on "send" icon
        toggleSendButton(true);
        sendBtn.setOnClickListener(v -> {
            if (chatInput.getText().toString().isEmpty()) return;

            // Create userMessage object
            userInput = chatInput.getText().toString().trim();
            userMessage = viewModel.getPresenter().getNewUserMessage(userInput);

            // Add new object to RecyclerView, scroll to bottom and clear input text
            recyclerViewAdapter.addItem(userMessage);
            recyclerView.scrollToPosition(recyclerViewAdapter.getItemCount() - 1);
            chatInput.setText("");

            waitForBotMsg();
        });

        setUpClearChatButton();
    }

    void toggleSendButton(boolean state) {
        sendBtn.setEnabled(state);
        sendBtn.setClickable(state);
        sendBtn.setAlpha(0.5f + 0.5f * (state ? 1 : 0));
    }

    void waitForBotMsg() {
        // Disable send button
        toggleSendButton(false);

        // Create botMessage object
        // TODO Get actual bot message
        botMessage = viewModel.getPresenter().getNewBotMessage("Yes.");

        // Add new object to RecyclerView and scroll to bottom
        recyclerViewAdapter.addItem(botMessage);
        recyclerView.scrollToPosition(recyclerViewAdapter.getItemCount() - 1);

        // TODO Delay based on text's length (if actual delay is not enough)
        new Handler().postDelayed(() -> {
            // Delay 5 seconds and enable send button
            sendBtn.setEnabled(true);
            sendBtn.setClickable(true);
            sendBtn.setAlpha(1f);
        }, 5000);
    }

    void setUpHeaderButtons() {
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
    }

    void setUpRecyclerView() {
        recyclerView = findViewById(R.id.chat_page_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new ChatRecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Add user's first message from the LandingPage
        userMessage = viewModel.getPresenter().getNewUserMessage(userInput);
        recyclerViewAdapter.addItem(userMessage);

        waitForBotMsg();
    }

    void setUpClearChatButton() {
        // Show popup when clear chat button is clicked
        ImageView clearChatBtn = findViewById(R.id.clear_chat_button);
        clearChatBtn.setOnClickListener(v -> {
            if (recyclerViewAdapter.getItemCount() == 0) return;

            // Inflate the custom layout
            LayoutInflater inflater = LayoutInflater.from(this);
            View popupView = inflater.inflate(R.layout.popup_confirm_clear_chat, null);

            // Create the dialog
            AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                    .setView(popupView)
                    .create();
            dialog.setCanceledOnTouchOutside(false);

            // Set up the buttons inside the popup
            Button cancelButton = popupView.findViewById(R.id.btn_cancel_clear_chat);
            Button confirmButton = popupView.findViewById(R.id.btn_confirm_clear_chat);

            cancelButton.setOnClickListener(cancel -> dialog.dismiss());

            confirmButton.setOnClickListener(confirm -> {
                recyclerViewAdapter.clearChat();
                dialog.dismiss();
            });

            // Show the dialog
            dialog.show();

            // Force dialog to match parent width
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
    }
}
