package me.trusha.fms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FirstPage extends AppCompatActivity {

    TextView tv;
    ProgressBar pb;
    Handler h = new Handler();
    int prog = 0;

    private SharedPreferences sharedPreferences;
    private static final String PIN_KEY = "AppPin";

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        pb = findViewById(R.id.progressbar);
        tv = findViewById(R.id.textView);
        img = findViewById(R.id.imageView);

        sharedPreferences = getSharedPreferences("SecurityPrefs", MODE_PRIVATE);
        String savedPin = sharedPreferences.getString(PIN_KEY, "");

        boolean isFromVerifyPin = getIntent().getBooleanExtra("FROM_VERIFY_PIN", false);

        if (!savedPin.isEmpty() && !isFromVerifyPin) {
            startActivity(new Intent(this, VerifyPinActivity.class));
            finish();
            return;
        }

        new Thread(() -> {
            while (prog < 100) {
                h.post(() -> {
                    prog++;
                    pb.setProgress(prog);
                    tv.setText(prog + "/100");
                    if (prog == 100) {
                            Intent intent = new Intent(FirstPage.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                    }
                });
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}