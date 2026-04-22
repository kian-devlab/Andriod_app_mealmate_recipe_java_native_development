package com.pkk.mealmate_recipesapp.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pkk.mealmate_recipesapp.adapters.RecipeAdapter;
import com.pkk.mealmate_recipesapp.databinding.FragmentFavouritesBinding;
import com.pkk.mealmate_recipesapp.models.Recipe;
import java.util.ArrayList;
import java.util.List;

/**
 * Favourites Fragment: Displays user's favorite recipes.
 */
public class FavouritesFragment extends Fragment {
    FragmentFavouritesBinding binding;
    private FirebaseFirestore db;
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference favoritesRef = db.collection("Favorites");
        CollectionReference recipesRef = db.collection("Recipes");

        favoritesRef.whereEqualTo("userId", userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> favoriteRecipeIds = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    favoriteRecipeIds.add(document.getString("recipeId"));
                }

                if (favoriteRecipeIds.isEmpty()) {
                    showNoFavorites();
                    return;
                }

                // Fetch the actual recipes
                recipesRef.whereIn("id", favoriteRecipeIds).get().addOnCompleteListener(recipeTask -> {
                    if (recipeTask.isSuccessful()) {
                        List<Recipe> recipes = new ArrayList<>();
                        for (QueryDocumentSnapshot recipeDoc : recipeTask.getResult()) {
                            recipes.add(recipeDoc.toObject(Recipe.class));
                        }

                        if (recipes.isEmpty()) {
                            showNoFavorites();
                        } else {
                            showFavorites(recipes);
                        }
                    } else {
                        Log.e("FavouritesFragment", "Error fetching recipes", recipeTask.getException());
                    }
                });

            } else {
                Log.e("FavouritesFragment", "Error fetching favorite IDs", task.getException());
            }
        });
    }

    private void showNoFavorites() {
        binding.rvFavourites.setVisibility(View.GONE);
        binding.noFavourites.setVisibility(View.VISIBLE);
        Toast.makeText(requireContext(), "No Favorites", Toast.LENGTH_SHORT).show();
    }

    private void showFavorites(List<Recipe> recipes) {
        binding.rvFavourites.setVisibility(View.VISIBLE);
        binding.noFavourites.setVisibility(View.GONE);
        binding.rvFavourites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        RecipeAdapter adapter = new RecipeAdapter();
        adapter.setRecipeList(recipes);
        binding.rvFavourites.setAdapter(adapter);
    }
}
