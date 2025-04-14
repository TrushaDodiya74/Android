package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {
    
    int count=0;
    int falg=0;
    String bt1,bt2,bt3,bt4,bt5,bt6,bt7,bt8,bt9;
    Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn=findViewById(R.id.bnext);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent);
                finish();
            }
        });
        b1=findViewById(R.id.b1);
        b2=findViewById(R.id.b2);
        b3=findViewById(R.id.b3);
        b4=findViewById(R.id.b4);
        b5=findViewById(R.id.b5);
        b6=findViewById(R.id.b6);
        b7=findViewById(R.id.b7);
        b8=findViewById(R.id.b8);
        b9=findViewById(R.id.b9);
        b10=findViewById(R.id.bclear);
        
        b10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b1.setEnabled(true);
                b2.setEnabled(true);
                b3.setEnabled(true);
                b4.setEnabled(true);
                b5.setEnabled(true);
                b6.setEnabled(true);
                b7.setEnabled(true);
                b8.setEnabled(true);
                b9.setEnabled(true);

                b1.setText("");
                b2.setText("");
                b3.setText("");
                b4.setText("");
                b5.setText("");
                b6.setText("");
                b7.setText("");
                b8.setText("");
                b9.setText("");
                count=0;
                falg=0;
            }
        });
    }
    public void disableall()
    {
        b1.setEnabled(false);
        b2.setEnabled(false);
        b3.setEnabled(false);
        b4.setEnabled(false);
        b5.setEnabled(false);
        b6.setEnabled(false);
        b7.setEnabled(false);
        b8.setEnabled(false);
        b9.setEnabled(false);
    }
    public void myvalidate(View v)
    {
        Button b=findViewById(v.getId());
        if(b.getText().toString().equals(""))
        {
            count++;
            if(falg==0)
            {
                b.setText("X");
                falg=1;
            }
            else
            {
                b.setText("O");
                falg=0;
            }
            bt1=b1.getText().toString();
            bt2=b2.getText().toString();
            bt3=b3.getText().toString();
            bt4=b4.getText().toString();
            bt5=b5.getText().toString();
            bt6=b6.getText().toString();
            bt7=b7.getText().toString();
            bt8=b8.getText().toString();
            bt9=b9.getText().toString();

            if (bt1.equals(bt2)&&bt1.equals(bt3)&&!bt1.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Winner"+bt1,Toast.LENGTH_LONG).show();
                disableall();
            }
            if (bt4.equals(bt5)&&bt4.equals(bt6)&&!bt4.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Winner"+bt4,Toast.LENGTH_LONG).show();
                disableall();
            }
            if (bt7.equals(bt8)&&bt7.equals(bt9)&&!bt7.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Winner"+bt7,Toast.LENGTH_LONG).show();
                disableall();
            }
            if (bt1.equals(bt4)&&bt1.equals(bt7)&&!bt1.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Winner"+bt1,Toast.LENGTH_LONG).show();
                disableall();
            }
            if (bt2.equals(bt5)&&bt2.equals(bt8)&&!bt2.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Winner"+bt2,Toast.LENGTH_LONG).show();
                disableall();
            }
            if (bt3.equals(bt6)&&bt3.equals(bt9)&&!bt3.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Winner"+bt3,Toast.LENGTH_LONG).show();
                disableall();
            }
            if (bt1.equals(bt5)&&bt1.equals(bt9)&&!bt1.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Winner"+bt1,Toast.LENGTH_LONG).show();
                disableall();
            }
            if (bt3.equals(bt5)&&bt3.equals(bt7)&&!bt3.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Winner"+bt3,Toast.LENGTH_LONG).show();
                disableall();
            }
        }
    }
}