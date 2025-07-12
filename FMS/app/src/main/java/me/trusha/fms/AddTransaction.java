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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private DatabaseReference mDatabase;
    private String useremail;

    private List<String> categories = new ArrayList<>();
    private DatabaseReference categoriesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        useremail = getIntent().getStringExtra("email");
        if (useremail == null || useremail.isEmpty()) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(useremail).child("transactions");
        categoriesRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(useremail)
                .child("categories");

        initializeViews();
        setupNavigation();
        loadCategories();
        setupSpinner();
        setupDatePicker();
        setupRadioGroup();
        checkForEditTransaction();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        etAmount = findViewById(R.id.et_amount);
        etDate = findViewById(R.id.et_date);
        etDescription = findViewById(R.id.et_description);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnSaveTransaction = findViewById(R.id.btn_save_transaction);
        radioGroup = findViewById(R.id.radioGroupType);
    }

    private void setupNavigation() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                intent = new Intent(this, Dashboard.class);
            } else if (id == R.id.nav_add_category) {
                intent = new Intent(AddTransaction.this, AddCategory.class);
            } else if (id == R.id.nav_transaction_history) {
                intent = new Intent(this, TransactionHistory.class);
            } else if (id == R.id.nav_reports) {
                intent = new Intent(this, Reports.class);
            } else if (id == R.id.nav_settings) {
                intent = new Intent(this, Settings.class);
            } else if (id == R.id.nav_logout) {
                logout();
                return true;
            }

            if (intent != null) {
                intent.putExtra("email", useremail);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserEmail = headerView.findViewById(R.id.tv_user_email);
        String email = ((MyApp) getApplication()).getUserEmail();
        tvUserEmail.setText(email != null ? email : "Guest User");
    }

    private void loadCategories() {
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                categories.add("Select Category");

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String category = dataSnapshot.getKey();
                    if (category != null) {
                        categories.add(category);
                    }
                }

                setupSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddTransaction.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                categories
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedCategory = categories.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            int position = categories.indexOf(selectedCategory);
            if (position >= 0) {
                spinnerCategory.setSelection(position);
            }
        }
    }


    private void setupDatePicker() {
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
    }

    private void setupRadioGroup() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = findViewById(checkedId);
            if (selectedButton != null) {
                transactionType = selectedButton.getText().toString();
            }
        });
    }

    private void checkForEditTransaction() {
        Intent intent = getIntent();
        if (intent.hasExtra("transaction_id")) {
            transactionId = intent.getStringExtra("transaction_id");
            etAmount.setText(intent.getStringExtra("amount"));
            etDate.setText(intent.getStringExtra("date"));
            etDescription.setText(intent.getStringExtra("description"));
            selectedCategory = intent.getStringExtra("category");
            transactionType = intent.getStringExtra("type");

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategory.getAdapter();
            int spinnerPosition = adapter.getPosition(selectedCategory);
            spinnerCategory.setSelection(spinnerPosition);

            btnSaveTransaction.setText("Update Transaction");
        }

        btnSaveTransaction.setOnClickListener(v -> {
            if (transactionId == null) {
                saveTransaction();
            } else {
                updateTransaction();
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

        mDatabase.orderByKey().limitToLast(1).get().addOnSuccessListener(snapshot -> {
            int newTransactionId = 1;

            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                String lastKey = dataSnapshot.getKey();
                if (lastKey != null && lastKey.startsWith("Transaction_")) {
                    String numStr = lastKey.replace("Transaction_", "");
                    newTransactionId = Integer.parseInt(numStr) + 1;
                }
            }

            String newTransactionKey = "Transaction_" + newTransactionId;

            Map<String, Object> transaction = new HashMap<>();
            transaction.put("id", newTransactionKey);
            transaction.put("amount", amount);
            transaction.put("date", date);
            transaction.put("description", description);
            transaction.put("category", selectedCategory);
            transaction.put("type", transactionType);

            mDatabase.child(newTransactionKey).setValue(transaction)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, transactionType + " Added Successfully!", Toast.LENGTH_SHORT).show();
                        clearForm();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save transaction!", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void updateTransaction() {
        String amountStr = etAmount.getText().toString();
        String date = etDate.getText().toString();
        String description = etDescription.getText().toString();

        if (amountStr.isEmpty() || date.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all details!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("amount", amount);
        transactionData.put("date", date);
        transactionData.put("description", description);
        transactionData.put("category", selectedCategory);
        transactionData.put("type", transactionType);

        mDatabase.child(transactionId).updateChildren(transactionData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Transaction Updated Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update transaction!", Toast.LENGTH_SHORT).show();
                });
    }

    private void redirectTo(Class<?> cls) {
        Intent intent = new Intent(AddTransaction.this, cls);
        intent.putExtra("useremail", useremail);
        startActivity(intent);
        finish();
    }

    private void clearForm() {
        etAmount.setText("");
        etDate.setText("");
        etDescription.setText("");
        spinnerCategory.setSelection(0);
        radioGroup.clearCheck();
        selectedCategory = "Food";
    }
    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}