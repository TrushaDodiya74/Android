package com.example.allcontrols;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView textView1=findViewById(R.id.tvAns1);
        TextView textView2=findViewById(R.id.tvAns2);
        TextView textView3=findViewById(R.id.tvAns3);
        TextView textView4=findViewById(R.id.tvAns4);
        TextView textView5=findViewById(R.id.tvAns5);
        TextView textView6=findViewById(R.id.tvAns6);
        TextView textView7=findViewById(R.id.tvAns7);
        TextView textView8=findViewById(R.id.tvAns8);
        TextView textView9=findViewById(R.id.tvAns9);
        TextView textView10=findViewById(R.id.tvAns10);
        TextView textView11=findViewById(R.id.tvAns11);
        TextView textView12=findViewById(R.id.tvAns12);
        TextView textView13=findViewById(R.id.tvAns13);

        Intent i=getIntent();
        String msg1=i.getStringExtra("editText");
        String msg2=i.getStringExtra("ACTV");
        String msg3=i.getStringExtra("radioButtons");
        String msg4=i.getStringExtra("radioGroup");
        String msg5=i.getStringExtra("checkBox");
        String msg6=i.getStringExtra("spinner");
        String msg7=i.getStringExtra("listView");
        String msg8=i.getStringExtra("datePicker");
        String msg9=i.getStringExtra("timePicker");
        String msg10=i.getStringExtra("seekBar");
        String msg11=i.getStringExtra("ratingBar");
        String msg12=i.getStringExtra("switch");
        String msg13=i.getStringExtra("toggleButton");

        textView1.setText(msg1 != null ? msg1 : "N/A");
        textView2.setText(msg2 != null ? msg2 : "N/A");
        textView3.setText(msg3 != null ? msg3 : "N/A");
        textView4.setText(msg4 != null ? msg4 : "N/A");
        textView5.setText(msg5 != null ? msg5 : "N/A");
        textView6.setText(msg6 != null ? msg6 : "N/A");
        textView7.setText(msg7 != null ? msg7 : "N/A");
        textView8.setText(msg8 != null ? msg8 : "N/A");
        textView9.setText(msg9 != null ? msg9 : "N/A");
        textView10.setText(msg10 != null ? msg10 : "N/A");
        textView11.setText(msg11 != null ? msg11 : "N/A");
        textView12.setText(msg12 != null ? msg12 : "N/A");
        textView13.setText(msg13 != null ? msg13 : "N/A");
    }
}