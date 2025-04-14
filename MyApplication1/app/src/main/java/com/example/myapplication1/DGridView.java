package com.example.myapplication1;

import android.os.Bundle;
import android.widget.GridView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DGridView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dgrid_view);
        String[] str1 = {"Data 0","Data 1","Data 2","Data 3","Data 4"};
        String[] str2 = {"Data 0","Data 1","Data 2","Data 3","Data 4"};
        int[] i1 = {R.drawable.abcd,R.drawable.abcd,R.drawable.abcd,R.drawable.abcd,R.drawable.abcd};
        AdapterGridclass adapterGridclass = new AdapterGridclass(getApplicationContext(),str1,str2,i1);
        GridView gv = findViewById(R.id.dgridview);
        gv.setAdapter(adapterGridclass);

    }
}