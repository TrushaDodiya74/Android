package com.example.fms;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private DatabaseReference dbRef;
    private MaterialButton btnAddIncome, btnAddExpense;
    private TextView tvi, tve;
    private int totalIncome = 0, totalExpense = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        createNotificationChannel();

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_transactions);
        btnAddIncome = findViewById(R.id.btn_add_income);
        btnAddExpense = findViewById(R.id.btn_add_expense);
        tvi = findViewById(R.id.tv_income);
        tve = findViewById(R.id.tv_expense);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList, this, false); // Edit/Delete disabled
        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("Transactions");

        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_add_transaction) {
                startActivity(new Intent(Dashboard.this, AddTransaction.class));
                finish();
            } else if (id == R.id.nav_transaction_history) {
                startActivity(new Intent(Dashboard.this, TransactionHistory.class));
                finish();
            } else if (id == R.id.nav_reports) {
                startActivity(new Intent(Dashboard.this, Reports.class));
                finish();
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(Dashboard.this, Settings.class));
                finish();
            } else if (id == R.id.nav_logout) {
                logout();
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        loadTransactions();

        btnAddIncome.setOnClickListener(view -> {
            Intent intent = new Intent(Dashboard.this, AddTransaction.class);
            intent.putExtra("transaction_type", "Income");
            startActivity(intent);
        });

        btnAddExpense.setOnClickListener(view -> {
            Intent intent = new Intent(Dashboard.this, AddTransaction.class);
            intent.putExtra("transaction_type", "Expense");
            startActivity(intent);
        });
    }

    private void loadTransactions() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Dashboard", "DataSnapshot received: " + snapshot);
                transactionList.clear();
                totalIncome = 0;
                totalExpense = 0;

                List<Transaction> allTransactions = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        Transaction transaction = dataSnapshot.getValue(Transaction.class);

                        if (transaction == null) {
                            Log.e("Dashboard", "Transaction is null! Skipping...");
                            continue;
                        }

                        if ("Income".equals(transaction.getType())) {
                            totalIncome += transaction.getAmount();
                        } else {
                            totalExpense += transaction.getAmount();
                        }

                        allTransactions.add(transaction);
                    } catch (Exception e) {
                        Log.e("Dashboard", "Error in parsing transaction: " + e.getMessage());
                    }
                }

                allTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

                int limit = Math.min(5, allTransactions.size());
                for (int i = 0; i < limit; i++) {
                    transactionList.add(allTransactions.get(i));
                }

                int totalBalance = totalIncome - totalExpense;

                tvi.setText("Income: " + totalIncome);
                tve.setText("Expense: " + totalExpense);
                TextView tvTotalBalance = findViewById(R.id.tv_total_balance);
                tvTotalBalance.setText("₹" + totalBalance);

                adapter.notifyDataSetChanged();

                if (totalExpense > totalIncome && sharedPreferences.getBoolean("notifications_enabled", true)) {
                    sendNotification("Expense Alert", "Your expenses have exceeded your income!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Dashboard", "Firebase Error: " + error.getMessage());
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Expense Alert";
            String description = "Notifications for expense alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel("expense_alert_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "expense_alert_channel")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}