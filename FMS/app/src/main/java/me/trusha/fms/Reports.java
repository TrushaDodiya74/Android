package com.example.fms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Reports extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private TextView tvSummary;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private DatabaseReference dbRef;
    private List<Transaction> transactionList = new ArrayList<>();
    private int totalIncome = 0, totalExpense = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(Reports.this, Dashboard.class));
                finish();
            } else if (id == R.id.nav_add_transaction) {
                startActivity(new Intent(Reports.this, AddTransaction.class));
                finish();
            } else if (id == R.id.nav_transaction_history) {
                startActivity(new Intent(Reports.this, TransactionHistory.class));
                finish();
            } else if (id == R.id.nav_reports) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(Reports.this, Settings.class));
                finish();
            } else if (id == R.id.nav_logout) {
                logout();
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        try {
            tvSummary = findViewById(R.id.tv_summary);
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            dbRef = FirebaseDatabase.getInstance().getReference("Transactions");

            loadTransactions();

        } catch (Exception e) {
            Log.e("ReportsActivity", "Error: " + e.getMessage());
        }
    }

    private void loadTransactions() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    transactionList.clear();
                    totalIncome = 0;
                    totalExpense = 0;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Transaction transaction = dataSnapshot.getValue(Transaction.class);
                        if (transaction != null) {
                            transactionList.add(transaction);

                            if ("Income".equals(transaction.getType())) {
                                totalIncome += transaction.getAmount();
                            } else {
                                totalExpense += transaction.getAmount();
                            }
                        }
                    }

                    tvSummary.setText("Total Income: ₹" + totalIncome + " | Total Expense: ₹" + totalExpense);

                    adapter = new TransactionAdapter(transactionList, Reports.this, false); // ❌ Edit/Delete disabled

                    recyclerView.setAdapter(adapter);

                } catch (Exception e) {
                    Log.e("ReportsActivity", "Error in onDataChange: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReportsActivity", "Firebase Error: " + error.getMessage());
                tvSummary.setText("Error Loading Data: " + error.getMessage());
            }
        });
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears activity stack
        startActivity(intent);
        finish();
    }
}
