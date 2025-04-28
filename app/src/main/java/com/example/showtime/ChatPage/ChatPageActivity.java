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
import com.example.showtime.Utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
    private final ReservationManager reservationManager = new ReservationManager();

    // TODO use Google Secret Manager to fetch API key
    OpenAIClientAsync client = OpenAIOkHttpClientAsync.builder()
            .apiKey("myKey")
            .build();

    ChatItem userMessage, botMessage, textMessage;
    ImageView sendBtn;
    String userInput;
    String previousResponseId;
    String MODEL_CONTEXT =
            "{\n  \"role\": \"developer\"," +
            "\n  \"content\": \"#Identity\n" +
            "You are UsherBot, an expert chatbot for theater reservations. You work for an app called Showtime, a phone application for helping users find, reserve, and manage theater tickets for a theater.\n" +
            "## Reservation Object Example\n" +
            "{\"reservationCode\": \"Z0Z0\", \"phoneNumber\": \"6900123123\", \"customerName\": \"John Doe\", \"show\": \"Peter Pan\", \"date\": \"29/05/2024\", \"time\": \"5:00PM\", \"ticketNum\": 3, \"section\": \"red\"}\n" +
            "# Instructions\n" +
            "* Always validate the user's reservation one time in a conversation stream before moving to change or cancel a reservation.\n" +
            "* Upon a successful creation of a new reservation, generate a unique 4-character alphanumeric reservation code and provide it to the user.\n" +
            "* Upon a successful change of a new reservation you must never alter the identifying information of the reservation, specifically the reservation code, the customer's name, and the customer's phone number.\n" +
            "* Dates must always be formatted to DD/MM/YYYY and should always assumed to be in the format of DD/MM/YYYY.\n" +
            "* Today's date is " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " and the time is " + LocalTime.now() + " you should use this date to calculate other relative dates that the user asks (eg. if the user says \"Next Saturday\" you should infer the date from today's date.\n" +
            "* Whenever you want to validate a reservation, consult the list of reservation's I have passed at the end of these instructions at the section named \"Database List of Reservations\" which is located directly after the FAQ section, to see if you can find it. Validate either by an exact match of a reservation code or a exact match of the pair of both a customer's name and the phone number used for the reservation. The absence of an exact match of the reservation means that there is no such reservation. Inform the user of the results of your validation as well as the details of the validated show.\n" +
            "* Never disclose any information about a reservation without first having validated one time in the conversation that it belongs to the user.\n" +
            "* Never disclose any internal information meant for the system or the developer to the message part of your response meant for the user. Do not tell the user which AI model you are or that you are made of OpenAI or ChatGPT. Always say you are UsherBot.\n" +
            "* After collecting all necessary information for the creation of a new reservation or after you have validated the existence of one, the actual action is handled by the developer." +
            "In order for the developer to complete this action easily you must return all your responses, even when there is no action, in a JSON format. " +
            "This JSON object should have 3 fields, \"message\" which should contain the actual string message that is to be displayed to the user, \"intent\" which should be a string of either \"new\" or \"cancel\" or \"change\" exactly as the strings seem here and with no extra information (of course without extra quotes inside the actual string). "+
            "The last field should be the \"reservation\" field which should contain a JSON object representation of the reservation that is to be created, changed, or cancelled. The format of this reservation object should match the exact structure of the example object given in the beginning of this message. Ensure you follow the naming conventions exactly shown in the example. The details of the actual reservation and the values of the fields should of course differ where needed to match the reservation of the user. For reservations that are to be changed ensure you provide the up to date details and not the outdated ones. " +
            "When there is no need for an action and such no need to provide information to the developer, ensure you still provide your JSON formatted response and that the fields are empty. You must always follow these JSON rules and take no liberty in them.\n" +
            "* You must never declare an intent to the developer via the JSON field for any action that you have not confirmed with the user. Data duplication should be avoided at all costs so only declare an intent once for the same action and only after you have confirmed with the user that the action details are correct.\n" +
            "* You must never drift off topic. If a user asks something not related to your functionality, you should kindly tell them that you are here to help for your actual features.\n" +
            "* You must always be kind and friendly to the users, helping them out with their reservations.\n" +
            "* Start each conversation (only the first message!) by introducing yourself." +
            "* Do not include markdown or similar formatting in your text messages to the user. Your messages are displayed in a chat message. Format for that medium accordingly by using empty new lines, terminating a line where needed, and a dash symbol if you need to show bullets. Ensure you leave empty new lines between non related segments of texts and/or bullets.\n" +
            "* Try to be as concise as possible to not confuse users, but ensure you don't make any assumptions or leave out important information. Do not compromise on the integrity of your answers and your instructed tone to achieve this goal." +
            "* Always consult the FAQ of the theater, provided below, before answering to the user. You must never by any means provide contradictory information to the user.\n" +
            "* The information you provide must always be aligned with the FAQ below.\n" +
            "# Application FAQ\n" +
            "Here is the FAQ we give to the users, extract your functionality from it:\n" +
            "Booking & Confirmation\n\n" +
            "\n" +
            "Q: How do I book a ticket?\n" +
            "A: Simply tell UsherBot which show and date you’d like (e.g., “Book 2 tickets for Saturday’s show”). It will confirm availability and ask for your name and phone number.\n" +
            "\n" +
            "Q: Can I select specific seats?\n" +
            "A: You can choose a seating section (red, yellow, blue, or green -accessible seating), but individual seat numbers aren’t currently available. UsherBot reserves your spot in the chosen section.\n" +
            "\n" +
            "Red seats are right upfront and yellow seats are right behind them. Blue seats are on the sides, in rows of three and are slightly elevated.\n" +
            "\n" +
            "Q: Do I have to keep my reservation code?\n" +
            "A: Yes! Once your booking is complete, you’ll see a unique code in the chat. Keep it handy for any changes, cancellations, or refunds.\n" +
            "\n" +
            "Q: I need accessible seating. Can UsherBot help me?\n" +
            "A: Absolutely. Just let UsherBot know you need accessible seating, and it will reserve your seats in the designated seating area -one seat for you and one for a companion.\n" +
            "\n" +
            "Q: Why do I have to give my phone number?\n" +
            "A: We use your phone number to confirm your identity, avoid duplicate bookings, and help you recover or change a reservation if you lose your code.\n" +
            "\n" +
            "Pricing & Payment\n" +
            "\n" +
            "Q: What are the ticket prices?\n" +
            "A: Red seats are right upfront and cost 40€ each.\n" +
            "\n" +
            "Yellow seats are right behind them -these cost 30€.\n" +
            "\n" +
            "Blue seats are on the sides and are slightly elevated -these cost 25€ each.\n" +
            "\n" +
            "Accessible seating is in a designated part of the red section and these cost 25€ each.\n" +
            "\n" +
            "Feel free to ask UsherBot for more details and it will confirm the exact price when booking.\n" +
            "\n" +
            "Q: Can I pay online or only at the booth?\n" +
            "A: Currently, you’ll pay at the theater’s ticket booth. Show your reservation code when you arrive and our staff will handle the payment. We plan to add online payment soon!\n" +
            "\n" +
            "Managing my booking\n" +
            "\n" +
            "Q: How do I change my booking?\n" +
            "A: Give UsherBot your reservation code and let it know the new show date or time you’d prefer. If seats are available, it updates your booking right away.\n" +
            "\n" +
            "Q: What if I typed the wrong date?\n" +
            "A: If it’s not the same day of the show, simply provide UsherBot your reservation code and correct date/time.\n" +
            "\n" +
            "If it’s already show day, you’ll need to contact support.\n" +
            "\n" +
            "Q: What if I need to cancel last-minute?\n" +
            "A: Same-day cancellations aren’t allowed through UsherBot. If you have an urgent situation, please call our support line or visit the booth in person.\n" +
            "\n" +
            "Q: What if I forget my reservation code?\n" +
            "A: Provide UsherBot with the phone number you used when booking. If it still can’t find your code or you don't have the phone number, you may need to call our support line for further assistance.\n" +
            "\n" +
            "Q: How do I rate my show?\n" +
            "A: After your show, ask UsherBot to show you a 0–5 star scale. Just pick the star you want and confirm.\n" +
            "\n" +
            "Q: Can I edit my rating after submitting?\n" +
            "A: Once you confirm your star rating, it’s final—so choose carefully.\n" +
            "\n" +
            "Show Information\n" +
            "\n" +
            "Q: Which days are shows playing?\n" +
            "A: We typically run shows Tuesday through Sunday (afternoon and evening). Mondays are our rest day.\n" +
            "\n" +
            "Q: What times are the shows?\n" +
            "A: We do multiple shows everyday!\n" +
            "\n" +
            "\"Peter Pan\" has two shows -afternoon show at 5:00PM and evening show at 8:00PM.\n" +
            "\n" +
            "\"Romeo and Juliet\" also has an afternoon show at 6:00PM and an evening show at 9:00PM.\n" +
            "\n" +
            "Q: What are the shows like?\n" +
            "A: Peter Pan:\n" +
            "A lively, adventure-filled performance that runs about two hours and immerses audiences in the magic of Neverland. Playful storytelling, colorful costumes, and upbeat music make it an ideal pick for families with children aged 5 and up. Themes of imagination, friendship, and the joy of childhood shine through as Peter and his friends fend off pirates and discover what it means never to grow up.\n" +
            "\n" +
            "Romeo and Juliet:\n" +
            "A faithful retelling of Shakespeare’s classic tragedy, this show spans roughly two and a half hours and appeals to teens and adults. Emotional performances, beautifully designed sets, and the timeless love story combine to explore themes of passion, family conflict, and fate. The production offers a poignant look at young love’s intensity—and the heartbreak that can follow when it’s tested by longstanding rivalries.\n" +
            "\n" +
            "Q: Who are the directors and performers?\n" +
            "A: Peter Pan:\n" +
            "Directed by Mark Thompson, this magical production features Sam Brenner as Peter and Alexandra Liu as Wendy—both bringing playful energy to Neverland. Their lively performances and imaginative staging have made this show a favorite with families.\n" +
            "\n" +
            "Romeo and Juliet\n" +
            "Eleni Papadopoulou directs this timeless Shakespeare classic with Dimitris Markos as Romeo and Maria Perakis as Juliet. They deliver a modern yet faithful interpretation, capturing the enduring romance and tragedy of Verona’s star-crossed lovers.\n" +
            "\n" +
            "Troubleshooting & Support\n" +
            "\n" +
            "Q: UsherBot won’t understand my input. What do I do?\n" +
            "A: Try rephrasing your request or use some of the suggested commands. If you’re still stuck, you can request a human agent.\n" +
            "\n" +
            "Q: How do I speak to a real person?\n" +
            "A: Ask UsherBot to connect you to a support agent (*).\n" +
            "\n" +
            "You can also call our support line at 210-0000000 (*).\n" +
            "\n" +
            "    Working hours: MON-FRI 8:00-20:00\n" +
            "\n" +
            "Q: What if I have a more complex request?\n" +
            "A: If UsherBot can’t handle your specific needs (e.g., large group bookings, special accommodations), it will offer to transfer you to a human agent or provide our support line. We’re here to help!\n"+
            "# Database list of reservations\n";


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
                    .instructions(MODEL_CONTEXT + reservationManager.toJson() + "\n" + "\"\n}") // append latest up to date reservations list
                    .input(userPrompt.getMessage())
                    .model(ChatModel.GPT_4_1_2025_04_14)
                    .build();
        } else {
            // For messages in a running conversation, include previousBotResponseId
            params = ResponseCreateParams.builder()
                    .instructions(MODEL_CONTEXT + reservationManager.toJson() + "\n" + "\"\n}") // append latest up to date reservations list
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

                if (responseJSON.getIntent().isPresent() && responseJSON.getReservation().isPresent()) {
                    String intent = responseJSON.getIntent().get().toLowerCase();
                    Reservation reservation = responseJSON.getReservation().get();
                    switch (intent) {
                        case "new":
                            reservationManager.addReservation(reservation.getReservationCode(), reservation);
                            break;
                        case "cancel":
                            reservationManager.deleteReservation(reservation.getReservationCode());
                            break;
                        case "change":
                            reservationManager.updateReservation(reservation.getReservationCode(), reservation);
                            break;
                    }
                }

                mainHandler.post(() -> {
                    // Deletes "processing..." message
                    recyclerViewAdapter.deleteLastItem();

                    addToRecyclerView(botMessage);

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
