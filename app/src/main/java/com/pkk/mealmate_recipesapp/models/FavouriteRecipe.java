package com.pkk.mealmate_recipesapp.models;

public class FavouriteRecipe {
    private String userId;
    private String recipeId;

    public FavouriteRecipe() {
        // Required empty constructor for Firestore
    }

    public FavouriteRecipe(String userId, String recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRecipeId() { return recipeId; }
    public void setRecipeId(String recipeId) { this.recipeId = recipeId; }
}
