package me.trusha.fms;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddTransaction extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private TextInputEditText etAmount, etDate, etDescription;
    private Spinner spinnerCategory;
    private MaterialButton btnSaveTransaction;
    private RadioGroup radioGroup;
    private String selectedCategory = "Food";
    private String transactionType = "Expense";
    private String transactionId = null;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        etAmount = findViewById(R.id.et_amount);
        etDate = findViewById(R.id.et_date);
        etDescription = findViewById(R.id.et_description);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnSaveTransaction = findViewById(R.id.btn_save_transaction);
        radioGroup = findViewById(R.id.radioGroupType);

        dbRef = FirebaseDatabase.getInstance().getReference("Transactions");

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(AddTransaction.this, Dashboard.class));
                finish();
            } else if (id == R.id.nav_add_transaction) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_transaction_history) {
                startActivity(new Intent(AddTransaction.this, TransactionHistory.class));
                finish();
            } else if (id == R.id.nav_reports) {
                startActivity(new Intent(AddTransaction.this, Reports.class));
                finish();
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(AddTransaction.this, Settings.class));
                finish();
            } else if (id == R.id.nav_logout) {
                logout();
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(AddTransaction.this,
                    (view, year1, month1, dayOfMonth) ->
                            etDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                    year, month, day);
            datePicker.show();
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = findViewById(checkedId);
            if (selectedButton != null) {
                transactionType = selectedButton.getText().toString();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("transaction_id")) {
            transactionId = intent.getStringExtra("transaction_id");
            etAmount.setText(intent.getStringExtra("amount"));
            etDate.setText(intent.getStringExtra("date"));
            etDescription.setText(intent.getStringExtra("description"));
            selectedCategory = intent.getStringExtra("category");
            transactionType = intent.getStringExtra("type");

            int spinnerPosition = adapter.getPosition(selectedCategory);
            spinnerCategory.setSelection(spinnerPosition);

            btnSaveTransaction.setText("Update Transaction");
        }

        btnSaveTransaction.setOnClickListener(v -> {
            if (transactionId == null) {
                saveTransaction();
            } else {
                updateTransaction(transactionId);
            }
        });
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString();
        String date = etDate.getText().toString();
        String description = etDescription.getText().toString();

        if (amountStr.isEmpty() || date.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all details!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        dbRef.orderByKey().limitToLast(1).get().addOnSuccessListener(snapshot -> {
            int newTransactionId = 1;

            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                String lastId = dataSnapshot.getKey();
                newTransactionId = Integer.parseInt(lastId) + 1;
            }

            Transaction transaction = new Transaction(
                    String.valueOf(newTransactionId),
                    amount,
                    date,
                    description,
                    selectedCategory,
                    transactionType
            );

            dbRef.child(String.valueOf(newTransactionId)).setValue(transaction)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, transactionType + " Added Successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save transaction!", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void updateTransaction(String transactionId) {
        String amountStr = etAmount.getText().toString();
        String date = etDate.getText().toString();
        String description = etDescription.getText().toString();

        if (amountStr.isEmpty() || date.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all details!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        dbRef.child(transactionId).child("amount").setValue(amount);
        dbRef.child(transactionId).child("date").setValue(date);
        dbRef.child(transactionId).child("description").setValue(description);
        dbRef.child(transactionId).child("category").setValue(selectedCategory);
        dbRef.child(transactionId).child("type").setValue(transactionType)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Transaction Updated Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update transaction!", Toast.LENGTH_SHORT).show();
                });
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
