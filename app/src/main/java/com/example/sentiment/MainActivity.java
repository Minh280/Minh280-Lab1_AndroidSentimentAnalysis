package com.example.sentiment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;  // Input field for text
    private Button analyzeButton;  // Button to trigger sentiment analysis
    private TextView emojiView;  // TextView to display emoji based on sentiment
    private LinearLayout layout;  // Layout to change background color based on sentiment
    private SentimentAnalysis sentimentAnalyzer;  // Sentiment analysis object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        inputText = findViewById(R.id.inputText);  // EditText for user input
        analyzeButton = findViewById(R.id.analyzeButton);  // Button for triggering analysis
        emojiView = findViewById(R.id.emojiView);  // TextView for displaying emojis
        layout = findViewById(R.id.layout);  // LinearLayout to change background color

        sentimentAnalyzer = new SentimentAnalysis();  // Initialize sentiment analyzer

        // Set button click listener
        analyzeButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();
            if (text.isEmpty()) {
                // Show toast if input is empty
                Toast.makeText(MainActivity.this, "Please enter some text to analyze.", Toast.LENGTH_SHORT).show();
            } else {
                // Start sentiment analysis if input is not empty
                analyzeSentiment(text);
            }
        });
    }

    private void analyzeSentiment(String text) {
        sentimentAnalyzer.analyze(text, new SentimentAnalysis.Callback() {
            @Override
            public void onSuccess(String sentiment) {
                runOnUiThread(() -> {
                    Log.d("Sentiment", "Response: " + sentiment);
                    String cleanSentiment = sentiment.trim().toLowerCase().replace("\n", "").replace(".", "");
                    Log.d("CleanSentiment", cleanSentiment);

                    // Update UI based on sentiment
                    switch (cleanSentiment) {
                        case "positive":
                            layout.setBackgroundColor(Color.GREEN);
                            emojiView.setText("😃");
                            break;
                        case "negative":
                            layout.setBackgroundColor(Color.RED);
                            emojiView.setText("😞");
                            break;
                        case "neutral":
                            layout.setBackgroundColor(Color.GRAY);
                            emojiView.setText("😐");
                            break;
                        default:
                            layout.setBackgroundColor(Color.YELLOW); // fallback for errors
                            emojiView.setText("❓");
                            break;
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("Sentiment", "Error: " + error);
                    layout.setBackgroundColor(Color.YELLOW); // fallback for errors
                    emojiView.setText("❓");
                });
            }
        });
    }
}
