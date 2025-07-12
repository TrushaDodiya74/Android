package me.trusha.fms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class SecuritySettings extends AppCompatActivity {
    private EditText etOldPin, etNewPin, etConfirmPin;
    private Button btnSavePin, btnResetPin;
    private SharedPreferences sharedPreferences;
    private static final String PIN_KEY = "AppPin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_settings);

        etOldPin = findViewById(R.id.et_old_pin);
        etNewPin = findViewById(R.id.et_new_pin);
        etConfirmPin = findViewById(R.id.et_confirm_pin);
        btnSavePin = findViewById(R.id.btn_save_pin);
        btnResetPin = findViewById(R.id.btn_reset_pin);

        sharedPreferences = getSharedPreferences("SecurityPrefs", MODE_PRIVATE);

        btnSavePin.setOnClickListener(v -> {
            String oldPin = etOldPin.getText().toString();
            String newPin = etNewPin.getText().toString();
            String confirmPin = etConfirmPin.getText().toString();
            String savedPin = sharedPreferences.getString(PIN_KEY, "");

            if (newPin.isEmpty() || confirmPin.isEmpty()) {
                Toast.makeText(this, "Enter new PIN in both fields", Toast.LENGTH_SHORT).show();
            } else if (!newPin.equals(confirmPin)) {
                Toast.makeText(this, "New PINs do not match!", Toast.LENGTH_SHORT).show();
            } else if (!savedPin.isEmpty() && !savedPin.equals(oldPin)) {
                Toast.makeText(this, "Old PIN is incorrect!", Toast.LENGTH_SHORT).show();
            } else {
                sharedPreferences.edit().putString(PIN_KEY, newPin).apply();
                Toast.makeText(this, "PIN Set Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnResetPin.setOnClickListener(v -> {
            sharedPreferences.edit().remove(PIN_KEY).apply();
            Toast.makeText(this, "PIN Reset Successfully!", Toast.LENGTH_SHORT).show();
        });
    }
}
