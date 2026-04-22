package com.pkk.mealmate_recipesapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkk.mealmate_recipesapp.AllRecipesActivity;
import com.pkk.mealmate_recipesapp.databinding.FragmentHomeBinding;
import com.pkk.mealmate_recipesapp.adapters.HorizontalRecipeAdapter;
import com.pkk.mealmate_recipesapp.models.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ValueEventListener recipeListener;
    private DatabaseReference recipeReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecipes();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        binding.tvSeeAllFavourite.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireContext(), AllRecipesActivity.class);
            intent.putExtra("type", "favourite");
            startActivity(intent);
        });

        binding.tvSeeAllPopulars.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireContext(), AllRecipesActivity.class);
            intent.putExtra("type", "popular");
            startActivity(intent);
        });
    }

    private void performSearch() {
        String query = Objects.requireNonNull(binding.etSearch.getText()).toString().trim();
        Intent intent = new Intent(requireContext(), AllRecipesActivity.class);
        intent.putExtra("type", "search");
        intent.putExtra("query", query);
        startActivity(intent);
    }

    private void loadRecipes() {
        HorizontalRecipeAdapter popularAdapter = new HorizontalRecipeAdapter();
        HorizontalRecipeAdapter favouriteAdapter = new HorizontalRecipeAdapter();

        binding.rvPopulars.setAdapter(popularAdapter);
        binding.rvFavouriteMeal.setAdapter(favouriteAdapter);

        recipeReference = FirebaseDatabase.getInstance().getReference("Recipes");

        recipeListener = recipeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Recipe> recipes = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipes.add(recipe);
                    }
                }
                updateUI(recipes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    private void updateUI(List<Recipe> recipes) {
        Collections.shuffle(recipes);

        HorizontalRecipeAdapter popularAdapter = (HorizontalRecipeAdapter) binding.rvPopulars.getAdapter();
        if (popularAdapter != null) {
            popularAdapter.setRecipeList(recipes.subList(0, Math.min(5, recipes.size())));
        }

        HorizontalRecipeAdapter favouriteAdapter = (HorizontalRecipeAdapter) binding.rvFavouriteMeal.getAdapter();
        if (favouriteAdapter != null) {
            favouriteAdapter.setRecipeList(recipes.subList(0, Math.min(5, recipes.size())));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recipeReference != null && recipeListener != null) {
            recipeReference.removeEventListener(recipeListener);
        }
        binding = null;
    }
}