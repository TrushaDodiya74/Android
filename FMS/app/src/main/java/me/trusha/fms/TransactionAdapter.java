package me.trusha.fms;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactionList;
    private Context context;
    private DatabaseReference dbRef;
    private boolean showActions;
    private String useremail;
    private TransactionHistory activity;

    public TransactionAdapter(List<Transaction> transactionList, Context context, boolean showActions, String useremail) {
        this.transactionList = transactionList;
        this.context = context;
        this.showActions = showActions;
        this.useremail = useremail;

        if (context instanceof TransactionHistory) {
            this.activity = (TransactionHistory) context;
        }

        dbRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(useremail)
                .child("transactions");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        String formattedAmount = NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(transaction.getAmount());

        holder.tvId.setText("ID: " + transaction.getId());
        holder.tvAmount.setText("Amount: " + formattedAmount);
        holder.tvCategory.setText("Category: " + transaction.getCategory());
        holder.tvDate.setText("Date: " + transaction.getDate());
        holder.tvType.setText("Type: " + transaction.getType());
        holder.tvDescription.setText("Description: "+transaction.getDescription());

        if (transaction.getType().equalsIgnoreCase("Income")) {
            holder.tvType.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.tvType.setTextColor(Color.parseColor("#F44336"));
        }

        if (showActions) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

            holder.btnEdit.setOnClickListener(v -> showEditDialog(transaction, position));

            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Transaction")
                        .setMessage("Are you sure you want to delete this transaction?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            dbRef.child(transaction.getId()).removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        if (activity != null) {
                                            activity.refreshAfterDelete();
                                        }
                                        Toast.makeText(context, "Transaction Deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    private void showEditDialog(Transaction transaction, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_transaction, null);

        if (dialogView == null) {
            Toast.makeText(context, "Error inflating dialog_edit_transaction.xml", Toast.LENGTH_LONG).show();
            return;
        }

        builder.setView(dialogView);

        EditText etAmount = dialogView.findViewById(R.id.et_amount);
        EditText etDate = dialogView.findViewById(R.id.et_date);
        EditText etDescription = dialogView.findViewById(R.id.et_description);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinner_category);

        // Get reference to user's categories in Firebase
        DatabaseReference userCategoriesRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(useremail)
                .child("categories");

        userCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categories = new ArrayList<>();
                categories.add("Select Category"); // Default option

                // Add all categories from Firebase
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String category = categorySnapshot.getKey();
                    if (category != null) {
                        categories.add(category);
                    }
                }

                // Create adapter with the categories from Firebase
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        context,
                        R.layout.spinner_item,
                        categories
                );
                adapter.setDropDownViewResource(R.layout.spinner_item);
                spinnerCategory.setAdapter(adapter);

                // Set the selected category
                if (transaction.getCategory() != null) {
                    int position = categories.indexOf(transaction.getCategory());
                    if (position >= 0) {
                        spinnerCategory.setSelection(position);
                    }
                }

                // Rest of your dialog setup...
                etAmount.setText(String.valueOf(transaction.getAmount()));
                etDate.setText(transaction.getDate());
                etDescription.setText(transaction.getDescription());

                etDate.setOnClickListener(v -> showDatePicker(etDate));

                builder.setPositiveButton("Update", (dialog, which) -> {
                    String updatedAmount = etAmount.getText().toString();
                    String updatedDate = etDate.getText().toString();
                    String updatedDescription = etDescription.getText().toString();
                    String updatedCategory = spinnerCategory.getSelectedItem().toString();

                    if (updatedAmount.isEmpty() || updatedDate.isEmpty() || updatedDescription.isEmpty() ||
                            updatedCategory.equals("Select Category")) {
                        Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double amount = Double.parseDouble(updatedAmount);
                        transaction.setAmount(amount);
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Invalid amount format", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    transaction.setDate(updatedDate);
                    transaction.setDescription(updatedDescription);
                    transaction.setCategory(updatedCategory);

                    dbRef.child(transaction.getId()).setValue(transaction)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Transaction Updated!", Toast.LENGTH_SHORT).show();
                                notifyItemChanged(position);
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT).show());
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                builder.create().show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker(EditText etDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) ->
                etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvAmount, tvCategory, tvDate, tvType, tvDescription;
        ImageView btnEdit, btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}