package com.example.extra;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

public class MainActivity2 extends AppCompatActivity {

    Spinner sp;
    AutoCompleteTextView atv;
    Button b;
    TextView tvcl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        Log.d(TAG, "onCreate: Checking permissions");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
        }

        atv = findViewById(R.id.autoCompleteTextView);
        TextView tvrg = findViewById(R.id.textViewRG);
        RadioGroup rg = findViewById(R.id.radioGroup);

        b = findViewById(R.id.btn);
        tvcl = findViewById(R.id.textViewCL);

        Intent intent = getIntent();
        String msg = intent.getStringExtra("message");

        TextView textView = findViewById(R.id.textView4);
        textView.setText(msg);

        sp = findViewById(R.id.spinner);
        String[] ar = {"PHP", "Java", "Python", "C"};
        ArrayAdapter<String> adp = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ar);
        sp.setAdapter(adp);

        String[] ar1 = {"hemant", "herin", "haresh", "hari", "hasmukh"};
        ArrayAdapter<String> adp1 = new ArrayAdapter<>(getApplicationContext(), R.layout.textview_layout, ar1);
        atv.setThreshold(1);
        atv.setAdapter(adp1);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton srb = findViewById(checkedId);

                // Display the selected RadioButton's text in TextView
                if (srb != null) {
                    tvrg.setText("Selected Option of RG: " + srb.getText().toString());
                }
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readContacts();
            }
        });
    }

    public void readContacts() {
        ContentResolver cr = getContentResolver();
        Cursor c = null;
        try {
            c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        } catch (SecurityException e) {
            Log.e(TAG, "readContacts: Permission error", e);
        }

        StringBuilder contacts = new StringBuilder();
        if (c != null && c.getCount() > 0) {
            Log.d(TAG, "readContacts: Contacts found.");
            while (c.moveToNext()) {
                String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.append("Name: ").append(name)
                        .append("\nNumber: ").append(number)
                        .append("\n\n");
            }
            c.close();
        } else {
            Log.d(TAG, "readContacts: No contacts available.");
            contacts.append("No contacts available.");
        }
        tvcl.setText(contacts.toString());
    }
}
