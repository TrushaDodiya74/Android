package me.trusha.fms;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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
    private DatabaseReference dbRef,categoriesRef;
    private TextInputEditText etFilterDate;
    private Spinner spinnerFilterCategory;
    private String selectedDate = "", selectedCategory = "All";
    private String useremail;
    private List<String> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        useremail = getIntent().getStringExtra("email");
        if (useremail == null || useremail.isEmpty()) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
        adapter = new TransactionAdapter(filteredList, this, true, useremail);
        recyclerView.setAdapter(adapter);


        dbRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(useremail)
                .child("transactions");

        categoriesRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(useremail)
                .child("categories");

        setupNavigation();
        loadCategories(); // Load categories first
        setupDateFilter();
        loadTransactions();
    }

    private void setupNavigation() {
        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                intent = new Intent(this, Dashboard.class);
            } else if (id == R.id.nav_add_category) {
                intent = new Intent(TransactionHistory.this, AddCategory.class);
            } else if (id == R.id.nav_add_transaction) {
                intent = new Intent(this, AddTransaction.class);
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

    private void loadCategories() {
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                categories.add("All"); // Default option

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String category = dataSnapshot.getKey();
                    if (category != null) {
                        categories.add(category);
                    }
                }

                setupCategoryFilter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TransactionHistory.this,
                        "Failed to load categories: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupCategoryFilter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                categories
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerFilterCategory.setAdapter(adapter);


        if (selectedCategory != null && !selectedCategory.equals("All")) {
            int position = categories.indexOf(selectedCategory);
            if (position >= 0) {
                spinnerFilterCategory.setSelection(position);
            } else {
                spinnerFilterCategory.setSelection(0);
                selectedCategory = "All";
            }
        }

        spinnerFilterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories.get(position);
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

   /* private void setupFilters() {
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        findViewById(R.id.btn_clear_date).setOnClickListener(v -> {
            selectedDate = "";
            etFilterDate.setText("");
            applyFilters();
        });
    }*/

    private void setupDateFilter() {
        etFilterDate.setOnClickListener(v -> showDatePicker());

        findViewById(R.id.btn_clear_date).setOnClickListener(v -> {
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
                        transaction.setId(dataSnapshot.getKey());
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

    public void refreshAfterDelete() {
        loadTransactions();
    }

   private void applyFilters() {
       filteredList.clear();
       for (Transaction transaction : transactionList) {
           boolean matchesDate = selectedDate.isEmpty() ||
                   (transaction.getDate() != null && transaction.getDate().equals(selectedDate));
           boolean matchesCategory = selectedCategory.equals("All") ||
                   (transaction.getCategory() != null && transaction.getCategory().equals(selectedCategory));

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