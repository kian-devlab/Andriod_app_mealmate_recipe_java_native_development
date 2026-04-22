package com.pkk.mealmate_recipesapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pkk.mealmate_recipesapp.adapters.IngredientAdapter;
import com.pkk.mealmate_recipesapp.databinding.ActivityAddRecipeBinding;
import com.pkk.mealmate_recipesapp.models.Ingredient;
import com.pkk.mealmate_recipesapp.models.Recipe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddRecipeActivity extends AppCompatActivity {
    private boolean isEditing = false;
    private String existingRecipeId;
    private ActivityAddRecipeBinding binding;
    private FirebaseFirestore db;
    private List<Ingredient> ingredientList;
    private IngredientAdapter ingredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        ingredientList = new ArrayList<>();

        setupCategoryDropdown();
        setupMealTypeDropdown();
        setupDatePicker();
        setupIngredientRecyclerView();
        setupAddIngredientButton();
        setupCreateRecipeButton();
        existingRecipeId = getIntent().getStringExtra("recipeId");
        if (existingRecipeId != null) {
            isEditing = true;
            binding.btnCreateRecipe.setText("Update Recipe");
            loadExistingRecipe();
        }
    }

    private void loadExistingRecipe() {
        db.collection("Recipes").document(existingRecipeId).get()
                .addOnSuccessListener(document -> {
                    Recipe recipe = document.toObject(Recipe.class);
                    if (recipe != null) {
                        binding.etRecipeName.setText(recipe.getName());
                        binding.etCookingTime.setText(recipe.getCookingTime());
                        binding.etCalories.setText(recipe.getCalories());
                        binding.etCategory.setText(recipe.getCategory());
                        binding.etDate.setText(recipe.getDate());
                        binding.etMealType.setText(recipe.getMealType());
                        binding.etImageUrl.setText(recipe.getImageUrl());

                        ingredientList.clear();
                        if (recipe.getIngredients() != null) {
                            ingredientList.addAll(recipe.getIngredients());
                        }
                        ingredientAdapter.notifyDataSetChanged();
                    }
                });
    }


    private void setupCategoryDropdown() {
        db.collection("Categories").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> categoryList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String categoryName = document.getString("name");
                        if (categoryName != null) {
                            categoryList.add(categoryName);
                        }
                    }

                    Log.d("CATEGORY_DEBUG", "Categories fetched: " + categoryList);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categoryList);
                    binding.etCategory.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Log.e("CATEGORY_DEBUG", "Failed to load categories!", e));
    }

    private void setupMealTypeDropdown() {
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mealTypes);
        binding.etMealType.setAdapter(adapter);
    }

    private void setupDatePicker() {
        binding.etDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (DatePicker view1, int year, int month, int dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        binding.etDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupIngredientRecyclerView() {
        ingredientAdapter = new IngredientAdapter(ingredientList);
        binding.rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        binding.rvIngredients.setAdapter(ingredientAdapter);
    }

    private void setupAddIngredientButton() {
        binding.fabAddIngredient.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddRecipeActivity.this);
            builder.setTitle("Add Ingredient");

            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_ingredient, null);
            builder.setView(dialogView);

            EditText etIngredientName = dialogView.findViewById(R.id.et_ingredient_name);
            EditText etIngredientQuantity = dialogView.findViewById(R.id.et_ingredient_quantity);

            builder.setPositiveButton("Add", (dialog, which) -> {
                String ingredientName = etIngredientName.getText().toString().trim();
                String quantity = etIngredientQuantity.getText().toString().trim();

                if (!TextUtils.isEmpty(ingredientName) && !TextUtils.isEmpty(quantity)) {
                    ingredientList.add(new Ingredient(ingredientName, quantity));
                    ingredientAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AddRecipeActivity.this, "Enter ingredient name and quantity!", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        });
    }

    private void setupCreateRecipeButton() {
        binding.btnCreateRecipe.setOnClickListener(view -> saveRecipeToFirestore());
    }

    private void saveRecipeToFirestore() {
        String recipeName = Objects.requireNonNullElse(binding.etRecipeName.getText(), "").toString().trim();
        String cookingTime = Objects.requireNonNullElse(binding.etCookingTime.getText(), "").toString().trim();
        String calories = Objects.requireNonNullElse(binding.etCalories.getText(), "").toString().trim();
        String category = Objects.requireNonNullElse(binding.etCategory.getText(), "").toString().trim();
        String date = Objects.requireNonNullElse(binding.etDate.getText(), "").toString().trim();
        String mealType = Objects.requireNonNullElse(binding.etMealType.getText(), "").toString().trim();
        String imageUrl = Objects.requireNonNullElse(binding.etImageUrl.getText(), "").toString().trim();

        if (TextUtils.isEmpty(recipeName) || TextUtils.isEmpty(cookingTime) || TextUtils.isEmpty(calories) ||
                TextUtils.isEmpty(category) || TextUtils.isEmpty(date) || TextUtils.isEmpty(mealType) ||
                TextUtils.isEmpty(imageUrl) || ingredientList.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Convert ingredientList to Firestore-friendly format
        List<Map<String, String>> ingredientsMap = new ArrayList<>();
        for (Ingredient ingredient : ingredientList) {
            Map<String, String> ingMap = new HashMap<>();
            ingMap.put("name", ingredient.getName());
            ingMap.put("quantity", ingredient.getQuantity());
            ingredientsMap.add(ingMap);
        }

        // ✅ Directly include Firestore ID
        String recipeId = db.collection("recipes").document().getId();

//        Map<String, Object> recipe = new HashMap<>();
//        recipe.put("id", recipeId);
//        recipe.put("name", recipeName);
//        recipe.put("cookingTime", cookingTime);
//        recipe.put("calories", calories);
//        recipe.put("category", category);
//        recipe.put("date", date);
//        recipe.put("mealType", mealType);
//        recipe.put("imageUrl", imageUrl);
//        recipe.put("ingredients", ingredientsMap);
//
//        binding.btnCreateRecipe.setEnabled(false);  // ✅ Disable button to prevent multiple clicks
//
//        db.collection("Recipes").document(recipeId).set(recipe)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(this, "Recipe Created!", Toast.LENGTH_SHORT).show();
//                    setResult(RESULT_OK);
//                    finish();
//                })
//                .addOnFailureListener(e -> {
//                    binding.btnCreateRecipe.setEnabled(true);  // ✅ Re-enable button on failure
//                    Toast.makeText(this, "Error saving recipe!", Toast.LENGTH_SHORT).show();
//                });
//    }
//}
        Map<String, Object> recipeData = new HashMap<>();
        recipeData.put("name", recipeName);
        recipeData.put("cookingTime", cookingTime);
        recipeData.put("calories", calories);
        recipeData.put("category", category);
        recipeData.put("date", date);
        recipeData.put("mealType", mealType);
        recipeData.put("imageUrl", imageUrl);
        recipeData.put("ingredients", ingredientsMap);
        recipeData.put("favorite", false); // Default value when creating a new recipe


        if (isEditing) {
            db.collection("Recipes").document(existingRecipeId)
                    .update(recipeData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Recipe updated!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        binding.btnCreateRecipe.setEnabled(true);
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            String newId = db.collection("Recipes").document().getId();
            recipeData.put("id", newId);
            recipeData.put("favorite", false);

            db.collection("Recipes").document(newId)
                    .set(recipeData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Recipe created!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        binding.btnCreateRecipe.setEnabled(true);
                        Toast.makeText(this, "Creation failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
