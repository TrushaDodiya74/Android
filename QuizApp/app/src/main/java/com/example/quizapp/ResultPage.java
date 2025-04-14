package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ResultPage extends AppCompatActivity {

    private TextView resultText, attemptedText, notAttemptedText, correctText, incorrectText;
    private Button btnRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        resultText = findViewById(R.id.resultText);
        attemptedText = findViewById(R.id.attemptedText);
        notAttemptedText = findViewById(R.id.notAttemptedText);
        correctText = findViewById(R.id.correctText);
        incorrectText = findViewById(R.id.incorrectText);
        btnRestart = findViewById(R.id.btnRestart);

        // Get data from intent
        Intent intent = getIntent();
        int attempted = intent.getIntExtra("attempted", 0);
        int notAttempted = intent.getIntExtra("not_attempted", 0);
        int correct = intent.getIntExtra("correct", 0);
        int incorrect = intent.getIntExtra("incorrect", 0);

        // Display results
        resultText.setText("Final Score");
        notAttemptedText.setText("Not Attempted: " + notAttempted);
        attemptedText.setText("Attempted: " + attempted);
        incorrectText.setText("Incorrect: " + incorrect);
        correctText.setText("Your Score: " + correct);

        btnRestart.setOnClickListener(v -> {
            Intent restartIntent = new Intent(ResultPage.this, MainActivity.class);
            startActivity(restartIntent);
            finish();
        });
    }
}
