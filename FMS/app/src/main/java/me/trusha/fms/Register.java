package me.trusha.fms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.buttonRegister).setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                mDatabase.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(Register.this, "User with this email already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            mDatabase.orderByKey().limitToLast(1).get().addOnSuccessListener(lastUserSnapshot -> {
                                int newUserId = 1;

                                for (DataSnapshot dataSnapshot : lastUserSnapshot.getChildren()) {
                                    String lastKey = dataSnapshot.getKey();
                                    if (lastKey != null && lastKey.startsWith("user_")) {
                                        String numStr = lastKey.replace("user_", "");
                                        newUserId = Integer.parseInt(numStr) + 1;
                                    }
                                }

                                String newUserKey = "user_" + newUserId;

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("email", email);
                                userData.put("password", password);

                                mDatabase.child(newUserKey).setValue(userData)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Register.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(Register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Register.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}