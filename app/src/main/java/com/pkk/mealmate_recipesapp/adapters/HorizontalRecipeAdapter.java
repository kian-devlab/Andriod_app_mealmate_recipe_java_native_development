package com.pkk.mealmate_recipesapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pkk.mealmate_recipesapp.R;
import com.pkk.mealmate_recipesapp.RecipeDetailsActivity;
import com.pkk.mealmate_recipesapp.databinding.ItemRecipeHorizontalBinding;
import com.pkk.mealmate_recipesapp.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class HorizontalRecipeAdapter extends RecyclerView.Adapter<HorizontalRecipeAdapter.RecipeHolder> {
    private final List<Recipe> recipeList = new ArrayList<>(); // 🔥 Prevent reassignment issues

    public void setRecipeList(List<Recipe> recipes) {
        this.recipeList.clear(); // ✅ Clear existing list
        this.recipeList.addAll(recipes); // ✅ Add new recipes instead of reassigning
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HorizontalRecipeAdapter.RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeHolder(ItemRecipeHorizontalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalRecipeAdapter.RecipeHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.onBind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeHolder extends RecyclerView.ViewHolder {
        ItemRecipeHorizontalBinding binding;

        public RecipeHolder(@NonNull ItemRecipeHorizontalBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(Recipe recipe) {
            // ✅ Handle null/empty image URL
            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(recipe.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.image_placeholder) // ✅ Use better placeholder
                        .into(binding.bgImgRecipe);
            } else {
                Glide.with(binding.getRoot().getContext())
                        .load(R.drawable.image_placeholder)
                        .into(binding.bgImgRecipe);
            }

            binding.tvRecipeName.setText(recipe.getName());

            binding.getRoot().setOnClickListener(view -> {
                Intent intent = new Intent(binding.getRoot().getContext(), RecipeDetailsActivity.class);
                intent.putExtra("recipeId", recipe.getId()); // ✅ Send ID instead of object
                binding.getRoot().getContext().startActivity(intent);
            });
        }
    }
}