package com.pkk.mealmate_recipesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pkk.mealmate_recipesapp.adapters.GroceryListAdapter;
import com.pkk.mealmate_recipesapp.databinding.ActivityGroceryBinding;
import com.pkk.mealmate_recipesapp.models.GroceryList;
import java.util.ArrayList;
import java.util.List;

public class GroceryListActivity extends AppCompatActivity {

    private ActivityGroceryBinding binding;
    private FirebaseFirestore db;
    private GroceryListAdapter adapter;
    private List<GroceryList> groceryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroceryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        groceryList = new ArrayList<>();
        adapter = new GroceryListAdapter(groceryList);

        binding.rvGroceryList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGroceryList.setAdapter(adapter);

        // Fetch data from Firestore
        loadGroceryList();

        // Handle Share Button
        binding.btnShareGrocery.setOnClickListener(v -> shareGroceryList());

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void loadGroceryList() {
        db.collection("Ingredients")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        groceryList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            GroceryList item = document.toObject(GroceryList.class);
                            groceryList.add(item);
                        }

                        // Log to confirm data is fetched
                        Log.d("GroceryListActivity", "Ingredients fetched: " + groceryList);

                        // Notify adapter
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("GroceryListActivity", "Error fetching data", task.getException());
                    }
                });
    }

    private void shareGroceryList() {
        StringBuilder ingredients = new StringBuilder("Here's my grocery list:\n");

        // Loop through the fetched grocery list and append ingredient names
        for (GroceryList item : groceryList) {
            ingredients.append("- ").append(item.getName()).append("\n");
        }

        // Share the grocery list via SMS
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, ingredients.toString());
        startActivity(Intent.createChooser(intent, "Share via"));
    }
}
