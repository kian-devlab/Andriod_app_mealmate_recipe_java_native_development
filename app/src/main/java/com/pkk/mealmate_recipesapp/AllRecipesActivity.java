package com.pkk.mealmate_recipesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pkk.mealmate_recipesapp.adapters.RecipeAdapter;
import com.pkk.mealmate_recipesapp.databinding.ActivityAllRecipesBinding;
import com.pkk.mealmate_recipesapp.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class AllRecipesActivity extends AppCompatActivity {
    private ActivityAllRecipesBinding binding;
    private FirebaseFirestore db;
    private String type;
    private RecipeAdapter adapter;  // ✅ Store adapter as a field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllRecipesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        adapter = new RecipeAdapter();  // ✅ Initialize adapter once
        binding.rvRecipes.setAdapter(adapter);
        binding.rvRecipes.setLayoutManager(new GridLayoutManager(this, 2));

        type = getIntent().getStringExtra("type");
        if (type == null) type = ""; // ✅ Prevent NullPointerException

        // ✅ Load recipes only once
        if (type.equalsIgnoreCase("category")) {
            filterByCategory();
        } else if (type.equalsIgnoreCase("search")) {
            loadByRecipes();
        } else {
            loadAllRecipes();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ✅ Remove duplicate load calls - Firestore already listens for changes in loadAllRecipes()
    }

    private void loadByRecipes() {
        String query = getIntent().getStringExtra("query");
        if (query == null || query.trim().isEmpty()) return; // ✅ Prevent unnecessary calls

        db.collection("Recipes")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThan("name", query + 'z') // ✅ More accurate search
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Recipe> recipes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            recipes.add(document.toObject(Recipe.class));
                        }
                        adapter.setRecipeList(recipes);  // ✅ Directly update adapter
                    } else {
                        Log.e("FirestoreError", "Error fetching recipes", task.getException());
                    }
                });
    }

    private void loadAllRecipes() {
        db.collection("Recipes")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error fetching recipes", error);
                        return;
                    }

                    List<Recipe> recipes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        recipes.add(document.toObject(Recipe.class));
                    }

                    adapter.setRecipeList(recipes);  // ✅ Directly update adapter
                });
    }

    private void filterByCategory() {
        String category = getIntent().getStringExtra("category");
        if (category == null || category.trim().isEmpty()) return; // ✅ Prevent unnecessary calls

        db.collection("Recipes")
                .whereEqualTo("category", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Recipe> recipes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            recipes.add(document.toObject(Recipe.class));
                        }
                        adapter.setRecipeList(recipes);  // ✅ Directly update adapter
                    } else {
                        Log.e("FirestoreError", "Error fetching filtered recipes", task.getException());
                    }
                });
    }
}
