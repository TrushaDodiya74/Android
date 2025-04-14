package com.example.fms;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TransactionHistory extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList, filteredList;
    private DatabaseReference dbRef;
    private TextInputEditText etFilterDate;
    private Spinner spinnerFilterCategory;
    private String selectedDate = "", selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_transactions);
        etFilterDate = findViewById(R.id.et_filter_date);
        spinnerFilterCategory = findViewById(R.id.spinner_filter_category);

        MaterialButton btnClearDate = findViewById(R.id.btn_clear_date);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new TransactionAdapter(filteredList, this, true);
        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("Transactions");

        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_transaction_history) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_add_transaction) {
                startActivity(new Intent(TransactionHistory.this, AddTransaction.class));
                finish();
                ;
            } else if (id == R.id.nav_dashboard) {
                startActivity(new Intent(TransactionHistory.this, Dashboard.class));
                finish();
            } else if (id == R.id.nav_reports) {
                startActivity(new Intent(TransactionHistory.this, Reports.class));
                finish();
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(TransactionHistory.this, Settings.class));
                finish();
            } else if (id == R.id.nav_logout) {
                logout();
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        loadTransactions();
        etFilterDate.setOnClickListener(v -> showDatePicker());

        String[] categoriesArray = getResources().getStringArray(R.array.categories);

        List<String> categoriesList = new ArrayList<>();
        categoriesList.add("All");
        categoriesList.addAll(Arrays.asList(categoriesArray));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, categoriesList);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerFilterCategory.setAdapter(adapter);

        spinnerFilterCategory.setSelection(0);

        spinnerFilterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categoriesList.get(position);
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnClearDate.setOnClickListener(v -> {
            selectedDate = "";
            etFilterDate.setText("");
            applyFilters();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            etFilterDate.setText(selectedDate);
            applyFilters();
        }, year, month, day);
        datePicker.show();
    }

    private void loadTransactions() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        transactionList.add(transaction);
                    }
                }
                applyFilters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TransactionHistory.this, "Failed to load transactions!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        filteredList.clear();
        for (Transaction transaction : transactionList) {
            boolean matchesDate = selectedDate.isEmpty() || (transaction.getDate() != null && transaction.getDate().equals(selectedDate));
            boolean matchesCategory = selectedCategory.equals("All") || (transaction.getCategory() != null && transaction.getCategory().equals(selectedCategory));

            if (matchesDate && matchesCategory) {
                filteredList.add(transaction);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
