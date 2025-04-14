package com.example.androidanimation;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {
    ImageView iv;

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

        iv = findViewById(R.id.iemoji);

        iv.setTranslationX(0);
        iv.setTranslationY(0);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Animation an = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.emoji_animation);
                //iv.startAnimation(an);


                // Load the animation
                Animation an = AnimationUtils.loadAnimation(MainActivity2.this, R.anim.emoji_animation);

                // Reset the view position if needed
                iv.setTranslationX(0);
                iv.setTranslationY(0);

                // Start the animation
                iv.startAnimation(an);
            }
        });
    }
}