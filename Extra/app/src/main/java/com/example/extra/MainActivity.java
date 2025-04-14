package com.example.extra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    int i=0;

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

        EditText et=findViewById(R.id.editTextText);
        EditText et1=findViewById(R.id.editTextText2);
        EditText et2=findViewById(R.id.editTextText3);
        Button p=findViewById(R.id.button);
        Button add=findViewById(R.id.button2);
        Button bar=findViewById(R.id.button3);
        TextView t=findViewById(R.id.textView);
        TextView t1=findViewById(R.id.textView2);
        TextView t3=findViewById(R.id.textView3);

        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=et.getText().toString();
                t.setText(str);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a=Integer.parseInt(et1.getText().toString());
                int b=Integer.parseInt(et2.getText().toString());
                t1.setText(" "+(a+b));
            }
        });

        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ar[]=getResources().getStringArray(R.array.sa);
                t3.setText(" "+ar[i]);
                i++;
            }
        });

        Button btn=findViewById(R.id.button5);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nm=t.getText().toString();
                Intent intent=new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("message",nm);
                startActivity(intent);
            }
        });
    }
}