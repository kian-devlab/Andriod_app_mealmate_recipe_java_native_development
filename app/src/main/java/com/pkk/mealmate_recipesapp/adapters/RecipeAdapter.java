package com.pkk.mealmate_recipesapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pkk.mealmate_recipesapp.R;
import com.pkk.mealmate_recipesapp.RecipeDetailsActivity;
import com.pkk.mealmate_recipesapp.databinding.ItemRecipeBinding;
import com.pkk.mealmate_recipesapp.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {
    private final List<Recipe> recipeList = new ArrayList<>();

    // Update the recipe list
    public void setRecipeList(List<Recipe> newRecipeList) {
        if (newRecipeList != null) {
            recipeList.clear();
            recipeList.addAll(newRecipeList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeHolder(ItemRecipeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.onBind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeHolder extends RecyclerView.ViewHolder {
        ItemRecipeBinding binding;

        public RecipeHolder(@NonNull ItemRecipeBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(Recipe recipe) {
            Glide.with(binding.getRoot().getContext())
                    .load(recipe.getImageUrl() != null ? recipe.getImageUrl() : R.drawable.image_placeholder)
                    .centerCrop()
                    .placeholder(R.drawable.image_placeholder)
                    .into(binding.bgImgRecipe);

            binding.tvRecipeName.setText(recipe.getName());

            // Handle item click
            binding.getRoot().setOnClickListener(view -> {
                Intent intent = new Intent(binding.getRoot().getContext(), RecipeDetailsActivity.class);
                intent.putExtra("recipeId", recipe.getId());  // Pass the recipe ID
                binding.getRoot().getContext().startActivity(intent);  // Use the context of the root view
            });
        }
    }
}