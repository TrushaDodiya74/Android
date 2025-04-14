package com.example.mydatabase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        Button b = findViewById(R.id.buttonRegister);
        EditText nm1 = findViewById(R.id.editTextName);
        EditText rn1 = findViewById(R.id.editTextRollNo);
        EditText ad1 = findViewById(R.id.editTextAddress);
        EditText ct1 = findViewById(R.id.editTextCity);
        EditText ag1 = findViewById(R.id.editTextAge);
        EditText em1 = findViewById(R.id.editTextEmail);
        EditText ps1 = findViewById(R.id.editTextPassword);
        MyDBHelper db = new MyDBHelper(this);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nm = nm1.getText().toString();
                String rn = rn1.getText().toString();
                String ad = ad1.getText().toString();
                String ct = ct1.getText().toString();
                int ag = Integer.parseInt(ag1.getText().toString());
                String em = em1.getText().toString();
                String ps = ps1.getText().toString();

                long result = db.insertStudent(nm,rn,ad,ct,ag,em,ps);
                if (result != -1) {
                    Toast.makeText(getApplicationContext(), "Student Registered Successfully", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}