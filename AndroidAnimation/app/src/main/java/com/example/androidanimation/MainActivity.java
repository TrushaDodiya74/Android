package com.example.androidanimation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btn10,btn11,btn12,btn13,btn14,btn15;
    ImageView iv;

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
        btn1=findViewById(R.id.b1);
        btn2=findViewById(R.id.b2);
        btn3=findViewById(R.id.b3);
        btn4=findViewById(R.id.b4);
        btn5=findViewById(R.id.b5);
        btn6=findViewById(R.id.b6);
        btn7=findViewById(R.id.b7);
        btn8=findViewById(R.id.b8);
        btn9=findViewById(R.id.b9);
        btn10=findViewById(R.id.b10);
        btn11=findViewById(R.id.b11);
        btn12=findViewById(R.id.b12);
        btn13=findViewById(R.id.b13);
        btn14=findViewById(R.id.b14);
        btn15=findViewById(R.id.bnext);
        iv=findViewById(R.id.imageView2);

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
                iv.startAnimation(an);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                iv.startAnimation(an);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                iv.startAnimation(an);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
                iv.startAnimation(an);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out);
                iv.startAnimation(an);
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
                iv.startAnimation(an);
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.cross_fading);
                iv.startAnimation(an);
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bounce);
                iv.startAnimation(an);
            }
        });

        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move_left);
                iv.startAnimation(an);
            }
        });

        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move_right);
                iv.startAnimation(an);
            }
        });

        btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
                iv.startAnimation(an);
            }
        });

        btn12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);
                iv.startAnimation(an);
            }
        });

        btn13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sequential);
                iv.startAnimation(an);
            }
        });

        btn14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation an;
                an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.together);
                iv.startAnimation(an);
            }
        });

        btn15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, MainActivity2.class);
                startActivity(i);
                finish();
            }
        });
    }
}