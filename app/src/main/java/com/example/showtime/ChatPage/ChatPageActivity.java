package com.example.showtime.ChatPage;

import android.annotation.SuppressLint;
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
import android.widget.RatingBar;
import android.widget.TextView;

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
import com.example.showtime.RecyclerView.ChatRecyclerViewAdapter;
import com.example.showtime.RecyclerView.ItemSelectionListener;
import com.example.showtime.RecyclerView.SuggestedQuestionsRecyclerViewAdapter;
import com.example.showtime.Reservation.Reservation;
import com.example.showtime.Reservation.ReservationManager;
import com.example.showtime.Utils.ResponseJSON;
import com.example.showtime.Utils.SuggestedQuestion;
import com.example.showtime.Utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.openai.client.OpenAIClientAsync;
import com.openai.client.okhttp.OpenAIOkHttpClientAsync;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputMessage;
import com.openai.models.responses.ResponseOutputText;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ChatPageActivity extends AppCompatActivity implements ItemSelectionListener {
    private ChatPageViewModel viewModel;
    private RecyclerView chatRecyclerView;
    private ChatRecyclerViewAdapter chatRecyclerViewAdapter;
    private SuggestedQuestionsRecyclerViewAdapter questionsRecyclerViewAdapter;
    private final static ReservationManager reservationManager = new ReservationManager();

    OpenAIClientAsync client = OpenAIOkHttpClientAsync.builder()
            .apiKey("myKey")
            .build();

    ChatItem userMessage, botMessage, textMessage, ticketBanner, botImgMessage, rateBanner;
    List<SuggestedQuestion> questions;
    ImageView sendBtn;
    String userInput;
    String previousResponseId;
    boolean supportHasJoined = false;


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

        setUpRecyclerViews();

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
            addToChatRecyclerView(userMessage);
            chatInput.setText("");

            // Do not send the mssage to the bot,
            // if support has joined the chat.
            if (!supportHasJoined) {
                waitForBotMsg((UserMessage) userMessage);
            }
        });

        setUpClearChatButton();
    }

    void addToChatRecyclerView(ChatItem item) {
        // Add new object to chat's RecyclerView and scroll to bottom
        chatRecyclerViewAdapter.addItem(item);
        chatRecyclerView.scrollToPosition(chatRecyclerViewAdapter.getItemCount() - 1);
    }

    void toggleSendButton(boolean state) {
        sendBtn.setEnabled(state);
        sendBtn.setClickable(state);
        sendBtn.setAlpha(0.5f + 0.5f * (state ? 1 : 0));
    }

    void waitForBotMsg(UserMessage userPrompt) {
        // Disable send button and clear suggested questions
        toggleSendButton(false);
        questionsRecyclerViewAdapter.clearQuestions();

        // Have a small delay before showing the "processing..." message
        new Handler().postDelayed(() -> {
            textMessage = viewModel.getPresenter().getNewTextMessage("processing...");
            addToChatRecyclerView(textMessage);
        }, 400);


        // Query OpenAI
        ResponseCreateParams params;
        if (previousResponseId == null || previousResponseId.isEmpty()) {
            // For first-time messages, don't include previousBotResponseId
            params = ResponseCreateParams.builder()
                    .instructions(Utils.getModelContext() + reservationManager.toJson() + "\n" + "\"\n}") // append latest up to date reservations list
                    .input(userPrompt.getMessage())
                    .model(ChatModel.GPT_4_1_2025_04_14)
                    .build();
        } else {
            // For messages in a running conversation, include previousBotResponseId
            params = ResponseCreateParams.builder()
                    .instructions(Utils.getModelContext() + reservationManager.toJson() + "\n" + "\"\n}") // append latest up to date reservations list
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
                botImgMessage = null;
                rateBanner = null;

                if (responseJSON.getIntent().isPresent()) {
                    String intent = responseJSON.getIntent().get().toLowerCase();
                    // Show help button
                    if (intent.equals("help")) {
                        questionsRecyclerViewAdapter.setShowHelpButton(true);
                    }

                    // Handle reservation related actions
                    if (responseJSON.getIntent().isPresent() && responseJSON.getReservation().isPresent()) {
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
                }

                // If the model has responded with both a ticket banner and a show extra instruction,
                // always prefer to show the ticket banner and skip the images.
                if (ticketBanner == null && responseJSON.getShowExtraValue().isPresent()) {
                    String showExtraValue = responseJSON.getShowExtraValue().get().toLowerCase();
                    switch (showExtraValue) {
                        case "seating_chart":
                            int resourceId = R.drawable.seating_chart;
                            botImgMessage = viewModel.getPresenter().getNewBotImageMessage(resourceId);
                            break;
                        case "rate":
                            Reservation reservation = responseJSON.getReservation().get();
                            rateBanner = viewModel.getPresenter().getNewRateBanner(reservation);
                            break;
                    }
                }

                // Get suggested questions from the model
                questions = responseJSON.getSuggestedQuestions();

            } catch (Exception e) {
                e.printStackTrace();
                botMessage = viewModel.getPresenter().getNewBotMessage("I didn't quite understand that, can you ask again?");
                questions = new ArrayList<>();
            } finally {
                // Update chat
                mainHandler.post(() -> {
                    // Deletes "processing..." message
                    chatRecyclerViewAdapter.deleteLastItem();

                    addToChatRecyclerView(botMessage);
                    if (ticketBanner != null) {
                        addToChatRecyclerView(ticketBanner);
                    }
                    if (botImgMessage != null) {
                        addToChatRecyclerView(botImgMessage);
                    }
                    if (rateBanner != null) {
                        addToChatRecyclerView(rateBanner);
                    }

                    // Enable send button
                    toggleSendButton(true);

                    // Show new suggested questions
                    questionsRecyclerViewAdapter.setQuestions(questions);
                });
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

    void setUpRecyclerViews() {
        // Chat recycler view
        chatRecyclerView = findViewById(R.id.chat_page_recycler_view);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(this, this);
        chatRecyclerView.setAdapter(chatRecyclerViewAdapter);

        // Add user's first message from the LandingPage
        previousResponseId = null; // Ensure that chatbot does not remember previous conversation
        userMessage = viewModel.getPresenter().getNewUserMessage(userInput);
        chatRecyclerViewAdapter.addItem(userMessage);

        // Suggested question recycler view
        RecyclerView questionsRecyclerView = findViewById(R.id.suggested_questions_recycler_view);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        questionsRecyclerViewAdapter = new SuggestedQuestionsRecyclerViewAdapter(this, this);
        questionsRecyclerView.setAdapter(questionsRecyclerViewAdapter);

        // After set-up, generate response to user's first question
        waitForBotMsg((UserMessage) userMessage);
    }

    void setUpClearChatButton() {
        // Show popup when clear chat button is clicked
        ImageView clearChatBtn = findViewById(R.id.clear_chat_button);
        clearChatBtn.setOnClickListener(v -> {
            if (chatRecyclerViewAdapter.getItemCount() == 0) return;

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
                chatRecyclerViewAdapter.clearChat();
                previousResponseId = null; // Ensure that chatbot does not remember previous conversation
                supportHasJoined = false;
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

    // ItemSelectionListener implementation
    @Override
    public void onRating(RatingBar ratingBar, String showName, int rating) {
        @SuppressLint("DefaultLocale") String rating_text = String.format("%d.0 / 5.0", rating);
        // Show popup when rating is selected
        if (chatRecyclerViewAdapter.getItemCount() == 0) return;

        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_confirm_rating, null);

        // Create the dialog
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(popupView)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        // Set up text
        TextView showNameTxt = popupView.findViewById(R.id.rating_showName);
        showNameTxt.setText(showName);
        TextView ratingTxt = popupView.findViewById(R.id.rating_score);
        ratingTxt.setText(rating_text);

        // Set up the buttons inside the popup
        Button cancelButton = popupView.findViewById(R.id.btn_cancel_rating);
        Button confirmButton = popupView.findViewById(R.id.btn_confirm_rating);

        cancelButton.setOnClickListener(cancel -> dialog.dismiss());

        confirmButton.setOnClickListener(confirm -> {
            // Show the rating as a user message for UsherBot to respond
            userMessage = viewModel.getPresenter().getNewUserMessage(rating_text);
            addToChatRecyclerView(userMessage);
            waitForBotMsg((UserMessage) userMessage);

            // Disable rating bar after confirmed rating
            ratingBar.setIsIndicator(true);
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();

        // Force dialog to match parent width
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onSuggestedQuestionClick(SuggestedQuestion suggestedQuestion) {
        if (suggestedQuestion.getQuestion().isEmpty()) return;

        // Create userMessage object
        userInput = suggestedQuestion.getQuestion().trim();
        userMessage = viewModel.getPresenter().getNewUserMessage(userInput);

        // Add new object to RecyclerView
        addToChatRecyclerView(userMessage);

        waitForBotMsg((UserMessage) userMessage);
    }

    @Override
    public void onHelpButtonClick() {
        // Show a "processing..." message with a small delay
        new Handler().postDelayed(() -> {
            questionsRecyclerViewAdapter.clearQuestions();
            textMessage = viewModel.getPresenter().getNewTextMessage("processing...");
            addToChatRecyclerView(textMessage);

            // Have a longer delay before showing the John text
            // (total delay is 3400 with the processing message delay)
            new Handler().postDelayed(() -> {
                chatRecyclerViewAdapter.deleteLastItem();

                String text = "**John** has joined the chat";
                textMessage = viewModel.getPresenter().getNewTextMessage(text);
                addToChatRecyclerView(textMessage);

                // Have another small delay before showing a default bot message
                new Handler().postDelayed(() -> {
                    botMessage = viewModel.getPresenter().getNewBotMessage(
                            "Feel free to continue explaining your request—they can see everything we’ve already discussed and will get you squared away!"
                    );
                    addToChatRecyclerView(botMessage);
                    supportHasJoined = true;
                }, 400);

            }, 3000);
        }, 400);
    }
}
