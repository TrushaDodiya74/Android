package com.example.allcontrols;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomePage extends AppCompatActivity {

    TextView tv;
    ProgressBar pb;
    Handler h=new Handler();
    int prog=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pb=findViewById(R.id.progressbar);
        tv=findViewById(R.id.textView);

        Intent i=new Intent(HomePage.this, MainActivity.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (prog<100)
                {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            prog++;
                            pb.setProgress(prog);
                            tv.setText(prog+"/100");
                            if(prog==100)
                            {
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}