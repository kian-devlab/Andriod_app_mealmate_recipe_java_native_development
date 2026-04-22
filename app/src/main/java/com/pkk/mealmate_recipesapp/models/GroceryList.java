package com.pkk.mealmate_recipesapp.models;

public class GroceryList {
    private String name;

    public GroceryList() {
        // Required empty constructor for Firestore serialization
    }

    public GroceryList(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
