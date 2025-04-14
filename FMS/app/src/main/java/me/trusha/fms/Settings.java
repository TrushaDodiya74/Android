package com.example.fms;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Settings extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private SwitchMaterial switchDarkMode, switchNotification;
    private MaterialButton btnSetSecurity, btnClearData, btnExportPdf;
    private SharedPreferences sharedPreferences;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchNotification = findViewById(R.id.switch_notification);
        btnSetSecurity = findViewById(R.id.btn_set_security);
        btnClearData = findViewById(R.id.btn_clear_data);
        btnExportPdf = findViewById(R.id.btn_export_pdf);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(Settings.this, Dashboard.class));
                finish();
            } else if (id == R.id.nav_add_transaction) {
                startActivity(new Intent(Settings.this, AddTransaction.class));
                finish();
            } else if (id == R.id.nav_transaction_history) {
                startActivity(new Intent(Settings.this, TransactionHistory.class));
                finish();
            } else if (id == R.id.nav_reports) {
                startActivity(new Intent(Settings.this, Reports.class));
                finish();
            } else if (id == R.id.nav_settings) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_logout) {
                logout();
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);
        switchDarkMode.setChecked(isDarkMode);
        setDarkMode(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            setDarkMode(isChecked);
            sharedPreferences.edit().putBoolean("DarkMode", isChecked).apply();
        });

        boolean isNotificationEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        switchNotification.setChecked(isNotificationEnabled);

        switchNotification.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            Toast.makeText(Settings.this, "Notification " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        btnSetSecurity.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.this, SecuritySettings.class);
            startActivity(intent);
        });

        btnClearData.setOnClickListener(v -> clearAppData());

        btnExportPdf.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                exportDataToPDF();
            } else {
                requestStoragePermission();
            }
        });
    }

    private void setDarkMode(boolean enable) {
        if (enable) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void clearAppData() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Clear Data")
                .setMessage("Are you sure you want to clear all data? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
                    sharedPreferences.edit().clear().apply();

                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Transactions");
                    dbRef.removeValue();

                    Toast.makeText(this, "App data cleared successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void exportDataToPDF() {
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference("Transactions");
        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(Settings.this, "No data found in Firebase!", Toast.LENGTH_SHORT).show();
                    return;
                }

                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "transactions_report.pdf");

                try {
                    PdfWriter writer = new PdfWriter(new FileOutputStream(file));
                    PdfDocument pdf = new PdfDocument(writer);
                    Document document = new Document(pdf);

                    document.add(new Paragraph("Transaction Report")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(20));

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Transaction transaction = dataSnapshot.getValue(Transaction.class);
                        if (transaction != null) {
                            String row = "ID: " + transaction.getId() +
                                    ", Type: " + transaction.getType() +
                                    ", Amount: " + transaction.getAmount() +
                                    ", Date: " + transaction.getDate() +
                                    ", Category: " + transaction.getCategory() +
                                    ", Description: " + transaction.getDescription();
                            document.add(new Paragraph(row));
                        }
                    }

                    document.close();
                    Toast.makeText(Settings.this, "PDF exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(Settings.this, "File not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Settings.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Settings.this, "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportDataToPDF();
            } else {
                Toast.makeText(this, "Permission denied! Cannot export PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}