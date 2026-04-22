package com.pkk.mealmate_recipesapp;

import com.pkk.mealmate_recipesapp.R;  // ✅ Correct import for app resources


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pkk.mealmate_recipesapp.databinding.ActivityRecipeDetailsBinding;
import com.pkk.mealmate_recipesapp.models.Recipe;
import androidx.appcompat.app.AlertDialog;

import java.util.HashMap;
import java.util.Map;

public class RecipeDetailsActivity extends AppCompatActivity {
    ActivityRecipeDetailsBinding binding;
    FirebaseFirestore db;
    String recipeId;
    boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        recipeId = getIntent().getStringExtra("recipeId");

        setupButtons();
        if (recipeId != null) loadRecipe();
        else finish();
    }

    private void setupButtons() {
        binding.imgEdit.setOnClickListener(v -> openEditActivity());
        binding.btnDelete.setOnClickListener(v -> confirmDelete());
        binding.imgFvrt.setOnClickListener(v -> toggleFavorite());
    }

    private void loadRecipe() {
        db.collection("Recipes").document(recipeId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        Recipe recipe = task.getResult().toObject(Recipe.class);
                        if (recipe != null) {
                            updateUI(recipe);
                            checkFavoriteStatus();  // ✅ Check if recipe is a favorite
                        }
                    } else {
                        Toast.makeText(this, "Error loading recipe", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkFavoriteStatus() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            db.collection("Favorites")
                    .document(userId + "_" + recipeId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            binding.imgFvrt.setImageResource(R.drawable.ic_favorite_filled);  // if this exists instead

                            isFavorite = true;
                        } else {
                            binding.imgFvrt.setImageResource(R.drawable.ic_favourite);
                            isFavorite = false;
                        }
                    });
        }
    }

    private void updateUI(Recipe recipe) {
        binding.tvName.setText(recipe.getName());
        binding.tcCategory.setText(recipe.getCategory());
        binding.tvDescription.setText(recipe.getDescription() != null ? recipe.getDescription() : "No description available.");
        binding.tvCalories.setText(recipe.getCalories() + " Calories");

        Glide.with(this)
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.image_placeholder)
                .into(binding.imgRecipe);
    }

    private void openEditActivity() {
        Intent intent = new Intent(this, AddRecipeActivity.class);
        intent.putExtra("recipeId", recipeId);
        startActivityForResult(intent, 1);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Recipe")
                .setMessage("Are you sure? This cannot be undone!")
                .setPositiveButton("Delete", (d, w) -> deleteRecipe())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteRecipe() {
        db.collection("Recipes").document(recipeId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Recipe deleted", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void toggleFavorite() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(this, "Please login to save favorites", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference favoriteRef = db.collection("Favorites").document(userId + "_" + recipeId);

        if (isFavorite) {
            // Remove from favorites
            favoriteRef.delete().addOnSuccessListener(aVoid -> {
                binding.imgFvrt.setImageResource(R.drawable.ic_favourite);
                isFavorite = false;
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            });
        } else {
            // Add to favorites
            Map<String, Object> favData = new HashMap<>();
            favData.put("userId", userId);
            favData.put("recipeId", recipeId);

            favoriteRef.set(favData).addOnSuccessListener(aVoid -> {
                binding.imgFvrt.setImageResource(R.drawable.ic_favorite_filled);
                isFavorite = true;
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) loadRecipe();
    }
}
