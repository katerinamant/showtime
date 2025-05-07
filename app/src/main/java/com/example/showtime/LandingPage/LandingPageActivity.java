package com.example.showtime.LandingPage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.showtime.ChatPage.ChatPageActivity;
import com.example.showtime.R;
import com.example.showtime.Utils.Utils;

public class LandingPageActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        // Send the message on keyboard next action
        // or when the sent icon is clicked
        EditText landing_page_edit_txt = findViewById(R.id.landing_edit_txt);
        landing_page_edit_txt.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT) {
                Intent intent = new Intent(LandingPageActivity.this, ChatPageActivity.class);

                // Add user input to intent
                String user_input = landing_page_edit_txt.getText().toString().trim();
                intent.putExtra("user_input", user_input);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // The icon inside the EditText is not actually clickable,
        // so we use its placement to find out when it's touched
        landing_page_edit_txt.setOnTouchListener((v, event) -> {
            // Start when the user finished their action
            // (lifts their finger)
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Get the drawable shown at the end
                // (the sent icon at the right side)
                Drawable drawableEnd = landing_page_edit_txt.getCompoundDrawables()[2]; // index 2 = end/right

                if (drawableEnd != null) {
                    // Get the width of the sent icon
                    int iconWidth = drawableEnd.getBounds().width();

                    // Check if the touch was inside the icon area
                    // (near the end of the EditText)
                    float touchX = event.getRawX();
                    float iconStartX = landing_page_edit_txt.getRight() - iconWidth - landing_page_edit_txt.getPaddingEnd();

                    if (touchX >= iconStartX) {
                        // Initiate new intent
                        Intent intent = new Intent(LandingPageActivity.this, ChatPageActivity.class);

                        // Add user input to intent
                        String user_input = landing_page_edit_txt.getText().toString().trim();
                        intent.putExtra("user_input", user_input);
                        startActivity(intent);
                        return true;
                    }
                }
            }
            return false;
        });

        // Set up prompt button intents
        Button[] buttons = new Button[]{
                findViewById(R.id.btn_prompt_1),
                findViewById(R.id.btn_prompt_2),
                findViewById(R.id.btn_prompt_3),
                findViewById(R.id.btn_prompt_4)
        };

        for (Button btn : buttons) {
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(LandingPageActivity.this, ChatPageActivity.class);
                String user_input = btn.getText().toString();
                intent.putExtra(Utils.USER_INPUT, user_input);
                startActivity(intent);
            });
        }
    }
}
