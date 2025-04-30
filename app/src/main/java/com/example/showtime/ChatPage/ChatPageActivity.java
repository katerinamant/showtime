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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.ChatItem.ChatItem;
import com.example.showtime.ChatItem.UserMessage;
import com.example.showtime.HelpPage.HelpPageActivity;
import com.example.showtime.LandingPage.LandingPageActivity;
import com.example.showtime.R;
import com.example.showtime.Reservation.Reservation;
import com.example.showtime.Reservation.ReservationManager;
import com.example.showtime.Utils.ResponseJSON;
import com.example.showtime.Utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.openai.client.OpenAIClientAsync;
import com.openai.client.okhttp.OpenAIOkHttpClientAsync;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

import com.openai.models.responses.ResponseOutputMessage;
import com.openai.models.responses.ResponseOutputText;

public class ChatPageActivity extends AppCompatActivity {
    private ChatPageViewModel viewModel;
    private RecyclerView recyclerView;
    private ChatRecyclerViewAdapter recyclerViewAdapter;
    private final static ReservationManager reservationManager = new ReservationManager();

    // TODO use Google Secret Manager to fetch API key
    OpenAIClientAsync client = OpenAIOkHttpClientAsync.builder()
            .apiKey("myKey")
            .build();

    ChatItem userMessage, botMessage, textMessage, ticketBanner;
    ImageView sendBtn;
    String userInput;
    String previousResponseId;


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
        sendBtn.setOnClickListener(v -> {
            if (chatInput.getText().toString().isEmpty()) return;

            // Create userMessage object
            userInput = chatInput.getText().toString().trim();
            userMessage = viewModel.getPresenter().getNewUserMessage(userInput);

            // Add new object to RecyclerView and clear input text
            addToRecyclerView(userMessage);
            chatInput.setText("");

            waitForBotMsg((UserMessage) userMessage);
        });

        setUpClearChatButton();
    }

    void addToRecyclerView(ChatItem item) {
        // Add new object to RecyclerView and scroll to bottom
        recyclerViewAdapter.addItem(item);
        recyclerView.scrollToPosition(recyclerViewAdapter.getItemCount() - 1);
    }

    void toggleSendButton(boolean state) {
        sendBtn.setEnabled(state);
        sendBtn.setClickable(state);
        sendBtn.setAlpha(0.5f + 0.5f * (state ? 1 : 0));
    }

    void waitForBotMsg(UserMessage userPrompt) {
        // Disable send button
        toggleSendButton(false);

        // Have a small delay before showing the "processing..." message
        new Handler().postDelayed(() -> {
            textMessage = viewModel.getPresenter().getNewTextMessage("processing...");
            addToRecyclerView(textMessage);
        }, 400);


        // Query OpenAI
        ResponseCreateParams params;
        if (previousResponseId == null || previousResponseId.isEmpty()) {
            // For first-time messages, don't include previousBotResponseId
            params = ResponseCreateParams.builder()
                    .instructions(Utils.MODEL_CONTEXT + reservationManager.toJson() + "\n" + "\"\n}") // append latest up to date reservations list
                    .input(userPrompt.getMessage())
                    .model(ChatModel.GPT_4_1_2025_04_14)
                    .build();
        } else {
            // For messages in a running conversation, include previousBotResponseId
            params = ResponseCreateParams.builder()
                    .instructions(Utils.MODEL_CONTEXT + reservationManager.toJson() + "\n" + "\"\n}") // append latest up to date reservations list
                    .previousResponseId(previousResponseId)
                    .input(userPrompt.getMessage())
                    .model(ChatModel.GPT_4_1_2025_04_14)
                    .build();
        }
        CompletableFuture<Response> responseFuture = client.responses().create(params);

        // Set up handler
        Handler mainHandler = new Handler();

        // Parse model response
        responseFuture.thenAccept(response -> {
            try {
                if (response.error().isPresent()) {
                    throw new RuntimeException(response.error().toString());
                }

                if (response.output().isEmpty()) {
                    throw new RuntimeException("Model response output field is empty: " + response);
                }

                // TODO don't get the first one rather the one which has the field "type": "output_text"
                Optional<ResponseOutputMessage> outputMessageJson = response.output().get(0).message();
                if (!outputMessageJson.isPresent()) {
                    throw new RuntimeException("Model returned no output: " + response);
                }

                List<ResponseOutputMessage.Content> outputMessageContent = outputMessageJson.get().content();
                if (outputMessageContent.isEmpty()) {
                    throw new RuntimeException("Model returned empty response: " + response);
                }

                // Extract the actual text from the output
                String modelRawResponse = outputMessageContent.get(0)
                        .outputText()
                        .map(ResponseOutputText::text)
                        .orElse("{\"message\": \"I didn't quite understand that, can you ask again?\", \"intent\": null, \"reservation\": null}");

                ResponseJSON responseJSON = ResponseJSON.fromJson(modelRawResponse);

                previousResponseId = response.id();
                botMessage = viewModel.getPresenter().getNewBotMessage(responseJSON.getMessage().orElse("I didn't quite understand that, can you ask again?"));
                ticketBanner = null;

                if (responseJSON.getIntent().isPresent() && responseJSON.getReservation().isPresent()) {
                    String intent = responseJSON.getIntent().get().toLowerCase();
                    Reservation reservation = responseJSON.getReservation().get();
                    switch (intent) {
                        case "new":
                            reservationManager.addReservation(reservation.getReservationCode(), reservation);
                            ticketBanner = viewModel.getPresenter().getNewTicketBanner(reservation);
                            break;
                        case "cancel":
                            reservationManager.deleteReservation(reservation.getReservationCode());
                            break;
                        case "change":
                            reservationManager.updateReservation(reservation.getReservationCode(), reservation);
                            ticketBanner = viewModel.getPresenter().getNewTicketBanner(reservation);
                            break;
                    }
                }

                mainHandler.post(() -> {
                    // Deletes "processing..." message
                    recyclerViewAdapter.deleteLastItem();

                    addToRecyclerView(botMessage);
                    if (ticketBanner != null) {
                        addToRecyclerView(ticketBanner);
                    }

                    // Enable send button
                    toggleSendButton(true);
                });
            } catch (Exception e) {
                e.printStackTrace();
                botMessage = viewModel.getPresenter().getNewBotMessage("I didn't quite understand that, can you ask again?");
            }
        });
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
        previousResponseId = null; // Ensure that chatbot does not remember previous conversation
        userMessage = viewModel.getPresenter().getNewUserMessage(userInput);
        recyclerViewAdapter.addItem(userMessage);

        waitForBotMsg((UserMessage) userMessage);
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
                previousResponseId = null; // Ensure that chatbot does not remember previous conversation
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
