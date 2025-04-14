package com.example.mydatabase;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Button doReg=findViewById(R.id.doReg);
        Button doDisp=findViewById(R.id.doDisp);
        Button doDel=findViewById(R.id.doDel);
        Button login=findViewById(R.id.buttonLogin);
        EditText e1 = findViewById(R.id.editTextEmail);
        EditText e2 = findViewById(R.id.editTextPassword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDBHelper db = new MyDBHelper(getApplicationContext());
                boolean isValid = db.checkLogin(e1.getText().toString(),e2.getText().toString());
                if (isValid) {
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        doReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        doDisp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Display.class));
            }
        });

        doDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDBHelper db=new MyDBHelper(getApplicationContext());
                db.delStud(e1.getText().toString());
                Toast.makeText(getApplicationContext(), "Student Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}