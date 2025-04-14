package com.example.mydatabase;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class Display extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display);

        TextView tv=findViewById(R.id.textView);
        MyDBHelper db=new MyDBHelper(this);
        Cursor c=db.allData();
        String str="";
        while (c.moveToNext())
        {
            str += c.getString(1)+"\n"+c.getString(2)+"\n"+c.getString(3)+"\n"+c.getString(4)+"\n"+c.getString(5)+"\n"+c.getString(6)+" ";
        }
        tv.setText(" "+str);
    }
}