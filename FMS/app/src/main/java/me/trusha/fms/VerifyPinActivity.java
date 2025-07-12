package me.trusha.fms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class VerifyPinActivity extends AppCompatActivity {

    private TextInputEditText etPin;
    private MaterialButton btnVerify;
    private SharedPreferences sharedPreferences;
    private static final String PIN_KEY = "AppPin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_pin);

        etPin = findViewById(R.id.et_pin);
        btnVerify = findViewById(R.id.btn_verify);
        sharedPreferences = getSharedPreferences("SecurityPrefs", MODE_PRIVATE);

        btnVerify.setOnClickListener(v -> {
            String enteredPin = etPin.getText().toString();
            String savedPin = sharedPreferences.getString(PIN_KEY, "");

            if (enteredPin.equals(savedPin)) {
                Intent intent = new Intent(VerifyPinActivity.this, FirstPage.class);
                intent.putExtra("FROM_VERIFY_PIN", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Incorrect PIN! Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}