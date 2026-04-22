package com.pkk.mealmate_recipesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pkk.mealmate_recipesapp.fragment.CategoriesFragment;
import com.pkk.mealmate_recipesapp.fragment.FavouritesFragment;
import com.pkk.mealmate_recipesapp.fragment.HomeFragment;
import com.pkk.mealmate_recipesapp.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAddRecipe, fabGroceryList;
    private BottomNavigationView navView;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        navView = findViewById(R.id.nav_view);
        fabAddRecipe = findViewById(R.id.fab_add_recipe);
        fabGroceryList = findViewById(R.id.fab_grocery_list); // ✅ Removed MealPlan FAB

        // Set default fragment on first launch
        if (savedInstanceState == null) {
            activeFragment = new HomeFragment();
            loadFragment(activeFragment);
        }

        // Handle bottom navigation selection
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                activeFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_category) {
                activeFragment = new CategoriesFragment();
            } else if (itemId == R.id.navigation_favorite) {
                activeFragment = new FavouritesFragment();
            } else if (itemId == R.id.navigation_profile) {
                activeFragment = new ProfileFragment();
            } else {
                return false;
            }
            loadFragment(activeFragment);
            return true;
        });

        // FAB Click Listeners
        fabAddRecipe.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddRecipeActivity.class)));

        fabGroceryList.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GroceryListActivity.class))); // ✅ Open Grocery List directly
    }

    // Load fragment method
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .commit();
    }
}
