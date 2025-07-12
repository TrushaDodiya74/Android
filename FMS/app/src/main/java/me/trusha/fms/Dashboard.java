package me.trusha.fms;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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
    private MaterialButton btnAddIncome, btnAddExpense;
    private TextView tvi, tve, tvTotalBalance;
    private int totalIncome = 0, totalExpense = 0;
    private DatabaseReference mDatabase;
    private String useremail;
    private static final String CHANNEL_ID = "expense_alert_channel";

    private boolean isFirstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        useremail = getIntent().getStringExtra("email");
        if (useremail == null || useremail.isEmpty()) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(useremail)
                .child("transactions");

        initializeViews();
        setupRecyclerView();
        setupNavigation();
        setupButtons();



        createNotificationChannel();


        loadUserTransactions();
    }

    private void checkAndSendNotification() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalIncome = 0, totalExpense = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        if ("Income".equals(transaction.getType())) {
                            totalIncome += transaction.getAmount();
                        } else {
                            totalExpense += transaction.getAmount();
                        }
                    }
                }

                if (totalExpense > totalIncome) {
                    sendNotification("Expense Alert", "Your expenses exceed your income");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this, "Failed to check transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_transactions);
        btnAddIncome = findViewById(R.id.btn_add_income);
        btnAddExpense = findViewById(R.id.btn_add_expense);
        tvi = findViewById(R.id.tv_income);
        tve = findViewById(R.id.tv_expense);
        tvTotalBalance = findViewById(R.id.tv_total_balance);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList, this, false, useremail);
        recyclerView.setAdapter(adapter);
    }

    private void setupNavigation() {
        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_add_transaction) {
                intent = new Intent(Dashboard.this, AddTransaction.class);
            } else if (id == R.id.nav_transaction_history) {
                intent = new Intent(Dashboard.this, TransactionHistory.class);
            } else if (id == R.id.nav_reports) {
                intent = new Intent(Dashboard.this, Reports.class);
            } else if (id == R.id.nav_settings) {
                intent = new Intent(Dashboard.this, Settings.class);
            } else if (id == R.id.nav_add_category) {
                intent = new Intent(Dashboard.this, AddCategory.class);
            } else if (id == R.id.nav_logout) {
                logout();
                return true;
            }

            if (intent != null) {
                intent.putExtra("email", useremail);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserEmail = headerView.findViewById(R.id.tv_user_email);
        String email = ((MyApp) getApplication()).getUserEmail();
        tvUserEmail.setText(email != null ? email : "Guest User");
    }

    private void setupButtons() {
        btnAddIncome.setOnClickListener(view -> {
            Intent intent = new Intent(Dashboard.this, AddTransaction.class);
            intent.putExtra("email", useremail);
            intent.putExtra("transaction_type", "Income");
            startActivity(intent);
        });

        btnAddExpense.setOnClickListener(view -> {
            Intent intent = new Intent(Dashboard.this, AddTransaction.class);
            intent.putExtra("email", useremail);
            intent.putExtra("transaction_type", "Expense");
            startActivity(intent);
        });
    }

    private void loadUserTransactions() {
        mDatabase.orderByChild("date").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionList.clear();
                totalIncome = 0;
                totalExpense = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        if ("Income".equals(transaction.getType())) {
                            totalIncome += transaction.getAmount();
                        } else {
                            totalExpense += transaction.getAmount();
                        }
                        transactionList.add(0, transaction);
                    }
                }

                updateUI();

                if (isFirstRun && totalExpense > totalIncome) {
                    sendNotification("Expense Alert", "Your expenses exceed your income");
                }
                isFirstRun = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        int totalBalance = totalIncome - totalExpense;
        tvi.setText("Income: ₹" + totalIncome);
        tve.setText("Expense: ₹" + totalExpense);
        tvTotalBalance.setText("₹" + totalBalance);
        adapter.notifyDataSetChanged();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Expense Alert",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for expense alerts");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String title, String message) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        if (!notificationsEnabled) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Intent intent = new Intent(this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}