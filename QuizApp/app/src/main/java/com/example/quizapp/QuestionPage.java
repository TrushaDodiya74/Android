package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuestionPage extends AppCompatActivity {

    private String[] questions;
    private String[][] options;
    private String[] correctAnswers;

    private TextView examNM, welcomeTV, questionText;
    private RadioGroup optionsGroup;
    private RadioButton optionA, optionB, optionC, optionD;
    private Button btnPrevious, btnNext, btnSkip, btnSubmit;

    private int currentQuestionIndex = 0;
    private int totalQuestions;
    private int attemptedCount = 0, notAttemptedCount, correctCount = 0, incorrectCount = 0;

    private String[] userAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_page);

        // Initialize UI components
        examNM = findViewById(R.id.examNM);
        welcomeTV = findViewById(R.id.welcomeTV);
        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Get user & exam name from Intent
        Intent i = getIntent();
        String userName = i.getStringExtra("nm");
        String examName = i.getStringExtra("exam");

        examNM.setText("Exam: " + examName);
        welcomeTV.setText("Welcome " + userName);

        // Get questions & answers from resources
        questions = getResources().getStringArray(R.array.que);
        String[] mcqA = getResources().getStringArray(R.array.mcqA);
        String[] mcqB = getResources().getStringArray(R.array.mcqB);
        String[] mcqC = getResources().getStringArray(R.array.mcqC);
        String[] mcqD = getResources().getStringArray(R.array.mcqD);
        correctAnswers = getResources().getStringArray(R.array.mcqCA);

        options = new String[][]{mcqA, mcqB, mcqC, mcqD};
        totalQuestions = questions.length;
        notAttemptedCount = totalQuestions;
        userAnswers = new String[totalQuestions];

        displayQuestion();

        // Option selection listener
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedOption = findViewById(checkedId);
            if (selectedOption != null) {
                userAnswers[currentQuestionIndex] = selectedOption.getText().toString();
            }
        });

        // Previous button logic
        btnPrevious.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                displayQuestion();
            }
        });

        // Next button logic
        btnNext.setOnClickListener(v -> {
            if (currentQuestionIndex < questions.length - 1) {
                currentQuestionIndex++;
                displayQuestion();
            }
        });

        // Skip button logic
        btnSkip.setOnClickListener(v -> {
            if (currentQuestionIndex < questions.length - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                Toast.makeText(QuestionPage.this, "This is the last question.", Toast.LENGTH_SHORT).show();
            }
        });

        // Submit button logic
        btnSubmit.setOnClickListener(v -> calculateResults());
    }

    private void displayQuestion() {
        // Set question text
        questionText.setText(questions[currentQuestionIndex]);

        // Set options text
        optionA.setText(options[0][currentQuestionIndex]);
        optionB.setText(options[1][currentQuestionIndex]);
        optionC.setText(options[2][currentQuestionIndex]);
        optionD.setText(options[3][currentQuestionIndex]);

        // Clear previous selection
        optionsGroup.setOnCheckedChangeListener(null); // Remove previous listener
        optionsGroup.clearCheck();

        // Reset user answer when revisiting
        if (userAnswers[currentQuestionIndex] == null) {
            optionsGroup.clearCheck();
        }

        // Restore previously selected option
        if (userAnswers[currentQuestionIndex] != null) {
            if (userAnswers[currentQuestionIndex].equals(optionA.getText().toString())) {
                optionA.setChecked(true);
            } else if (userAnswers[currentQuestionIndex].equals(optionB.getText().toString())) {
                optionB.setChecked(true);
            } else if (userAnswers[currentQuestionIndex].equals(optionC.getText().toString())) {
                optionC.setChecked(true);
            } else if (userAnswers[currentQuestionIndex].equals(optionD.getText().toString())) {
                optionD.setChecked(true);
            }
        }

        // Restore listener after setting default values
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedOption = findViewById(checkedId);
            if (selectedOption != null) {
                userAnswers[currentQuestionIndex] = selectedOption.getText().toString();
            }
        });
    }

    private void calculateResults() {
        attemptedCount = 0;
        correctCount = 0;
        incorrectCount = 0;

        for (int i = 0; i < totalQuestions; i++) {
            if (userAnswers[i] != null) {
                attemptedCount++;
                if (userAnswers[i].equals(correctAnswers[i])) {
                    correctCount++;
                } else {
                    incorrectCount++;
                }
            }
        }
        notAttemptedCount = totalQuestions - attemptedCount;

        // Pass data to ResultActivity
        Intent intent = new Intent(QuestionPage.this, ResultPage.class);
        intent.putExtra("attempted", attemptedCount);
        intent.putExtra("not_attempted", notAttemptedCount);
        intent.putExtra("correct", correctCount);
        intent.putExtra("incorrect", incorrectCount);
        startActivity(intent);
        finish();
    }
}
