package com.pkk.mealmate_recipesapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pkk.mealmate_recipesapp.databinding.ActivitySingUpBinding;
import com.pkk.mealmate_recipesapp.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SingUpActivity extends AppCompatActivity {
    ActivitySingUpBinding binding;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnSignup.setOnClickListener(view -> signup());
        binding.tvLogin.setOnClickListener(view -> finish());
    }

    private void signup() {
        String name = Objects.requireNonNull(binding.etName.getText()).toString().trim();
        String email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your name, email and password", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            createNewUser(name, email, password);
        }
    }

    private void createNewUser(String name, String email, String password) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating user...");
        dialog.setCancelable(false);
        dialog.show();

        FirebaseApp.initializeApp(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User account created successfully
                        saveUserToFirestore(name, email);
                    } else {
                        // Account creation failed
                        dialog.dismiss();
                        Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String name, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getUid());

        // Creating user data in HashMap format
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("name", name);
        user.put("email", email);
        user.put("profileImage", "");
        user.put("phone", "");

        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.set(user).addOnCompleteListener(task -> {
            dialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(SingUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SingUpActivity.this, MainActivity.class));
                finishAffinity();
            } else {
                Toast.makeText(SingUpActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
