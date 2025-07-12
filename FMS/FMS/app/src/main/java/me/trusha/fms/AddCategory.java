package me.trusha.fms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddCategory extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private TextInputEditText etNewCategory;
    private TextInputLayout tilNewCategory;
    private TextInputLayout tilCategories;
    private MaterialButton btnAddCategory, btnDeleteCategory;
    private DatabaseReference dbRef;
    private String useremail;
    private List<String> categories = new ArrayList<>();
    //private ArrayAdapter<String> categoriesAdapter;

    private Spinner spinnerCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        useremail = getIntent().getStringExtra("email");
        if (useremail == null || useremail.isEmpty()) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(useremail)
                .child("categories");

        initializeViews();
        setupNavigation();
        setupAdapters();
        setupButtons();
        loadCategories();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        etNewCategory = findViewById(R.id.et_new_category);
        tilNewCategory = findViewById(R.id.til_new_category);
        spinnerCategories = findViewById(R.id.spinner_categories);
        tilCategories = findViewById(R.id.til_categories);
        btnAddCategory = findViewById(R.id.btn_add_category);
        btnDeleteCategory = findViewById(R.id.btn_delete_category);
    }

    private void setupNavigation() {
        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_dashboard) {
                intent = new Intent(this, Dashboard.class);
            } else if (id == R.id.nav_add_transaction) {
                intent = new Intent(this, AddTransaction.class);
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
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        View headerView = navigationView.getHeaderView(0);
        TextView tvUserEmail = headerView.findViewById(R.id.tv_user_email);
        String email = ((MyApp) getApplication()).getUserEmail();
        tvUserEmail.setText(email != null ? email : "Guest User");
    }

    private void setupAdapters() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                categories
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(spinnerAdapter);
    }

    private void setupButtons() {
        btnAddCategory.setOnClickListener(v -> addCategory());
        btnDeleteCategory.setOnClickListener(v -> deleteCategory());
    }

    private void addCategory() {
        String newCategory = etNewCategory.getText().toString().trim();

        if (newCategory.isEmpty()) {
            tilNewCategory.setError("Category name cannot be empty");
            return;
        }

        if (newCategory.length() < 3) {
            tilNewCategory.setError("Category name too short");
            return;
        }

        if (categories.contains(newCategory)) {
            tilNewCategory.setError("Category already exists");
            return;
        }

        tilNewCategory.setError(null);

        dbRef.child(newCategory).setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    etNewCategory.setText("");
                    loadCategories();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteCategory() {
        if (spinnerCategories.getSelectedItemPosition() <= 0) {
            tilCategories.setError("Please select a valid category");
            return;
        }

        String categoryToDelete = spinnerCategories.getSelectedItem().toString();

        tilCategories.setError(null);

        dbRef.child(categoryToDelete).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Category deleted successfully", Toast.LENGTH_SHORT).show();
                    loadCategories();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCategories() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                categories.add("Select Category");

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String category = dataSnapshot.getKey();
                    if (category != null && !category.isEmpty()) {
                        categories.add(category);
                    }
                }

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        AddCategory.this,
                        R.layout.spinner_item,
                        categories
                );
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategories.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddCategory.this,
                        "Failed to load categories: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}