package com.pkk.mealmate_recipesapp.models;

public class GroceryItem {
    private String itemName;
    private int quantity;

    public GroceryItem() {} // Required empty constructor for Firestore

    public GroceryItem(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }
}
