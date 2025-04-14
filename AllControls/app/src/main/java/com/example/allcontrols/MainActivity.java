package com.example.allcontrols;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText edt;
    AutoCompleteTextView actv;
    RadioButton rb1,rb2,rb3;
    RadioGroup rg;
    CheckBox cb1,cb2,cb3;
    Spinner sp;
    Button btn;
    ListView lv;
    Intent i;
    String[] nm = {"Trusha", "Radha", "Krishna", "Gopi", "Kanha"};
    String sl;
    SeekBar sb;
    RatingBar ratb;
    Switch s;
    ToggleButton tb;
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

        DatePicker dp = findViewById(R.id.datePicker);
        TimePicker tp = findViewById(R.id.timePicker);

        edt=findViewById(R.id.editText1);
        actv=findViewById(R.id.autoCompleteTextView);
        rb1=findViewById(R.id.radioButton1);
        rb2=findViewById(R.id.radioButton2);
        rb3=findViewById(R.id.radioButton3);
        rg=findViewById(R.id.radioGroup);
        cb1=findViewById(R.id.checkbox1);
        cb2=findViewById(R.id.checkbox2);
        cb3=findViewById(R.id.checkbox3);
        sp=findViewById(R.id.spinner1);
        lv=findViewById(R.id.listView);
        sb=findViewById(R.id.seekBar);
        ratb=findViewById(R.id.ratingBar);
        s=findViewById(R.id.switch1);
        tb=findViewById(R.id.toggleButton);

        btn=findViewById(R.id.submit);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String selectedRBText = "";
                if (rb1.isChecked()) {
                    selectedRBText = rb1.getText().toString();
                } else if (rb2.isChecked()) {
                    selectedRBText = rb2.getText().toString();
                } else if (rb3.isChecked()) {
                    selectedRBText = rb3.getText().toString();
                }

                String selectedRGText = "";
                int selectedId = rg.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(MainActivity.this, "Please select a radio button", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    selectedRGText = selectedRadioButton.getText().toString();
                }

                String scb="";
                if (cb1.isChecked()) {
                    scb=scb+cb1.getText().toString()+",";
                }
                if (cb2.isChecked()) {
                    scb=scb+cb2.getText().toString()+",";
                }
                if (cb3.isChecked()) {
                    scb=scb+cb3.getText().toString()+",";
                }

                if (scb.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please select at least one checkbox", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (sl.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please select an item", Toast.LENGTH_SHORT).show();
                    return;
                }

                int day = dp.getDayOfMonth();
                int month = dp.getMonth() + 1;
                int year = dp.getYear();
                String sd = day + "/" + month + "/" + year;

                tp.setIs24HourView(true);
                int hour = tp.getHour();
                int minute = tp.getMinute();
                String st = String.format("%02d:%02d", hour, minute);

                int sbv = sb.getProgress();
                String sbString = String.valueOf(sbv);

                String ratbv=String.valueOf(ratb.getRating());

                boolean isSwitchOn = s.isChecked();
                String ss = isSwitchOn ? "ON" : "OFF";

                boolean a = tb.isChecked();
                String tbs = a ? "ON":"OFF";

                i=new Intent(MainActivity.this, MainActivity2.class);
                i.putExtra("editText",edt.getText().toString());
                i.putExtra("ACTV",actv.getText().toString());
                i.putExtra("radioButtons", selectedRBText);
                i.putExtra("radioGroup", selectedRGText);
                i.putExtra("checkBox", scb);
                i.putExtra("spinner",sp.getSelectedItem().toString());
                i.putExtra("listView", sl);
                i.putExtra("datePicker", sd);
                i.putExtra("timePicker",st);
                i.putExtra("seekBar",sbString);
                i.putExtra("ratingBar",ratbv);
                i.putExtra("switch",ss);
                i.putExtra("toggleButton",tbs);
                startActivity(i);
            }
        });
        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(rb1.isChecked())
                {
                    rb2.setChecked(false);
                    rb3.setChecked(false);
                }
            }
        });
        rb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(rb2.isChecked())
                {
                    rb1.setChecked(false);
                    rb3.setChecked(false);
                }
            }
        });
        rb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(rb3.isChecked())
                {
                    rb1.setChecked(false);
                    rb2.setChecked(false);
                }
            }
        });
        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.sa));
        sp.setAdapter(adp);

        String ar1[] = {"hemant", "herin", "haresh", "hari", "hasmukh"};
        ArrayAdapter<String> adp1 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ar1);
        actv.setThreshold(1);
        actv.setAdapter(adp1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nm);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sl = nm[position];
            }
        });
    }
}