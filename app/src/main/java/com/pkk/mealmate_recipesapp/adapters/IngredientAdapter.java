package com.pkk.mealmate_recipesapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pkk.mealmate_recipesapp.R;
import com.pkk.mealmate_recipesapp.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<Ingredient> ingredientList;

    public IngredientAdapter(List<Ingredient> ingredientList) {
        this.ingredientList = new ArrayList<>(ingredientList); // Initialize list safely
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredientList.get(position);
        holder.tvIngredientName.setText(ingredient.getName());
        holder.tvIngredientQuantity.setText(ingredient.getQuantity());

        holder.btnDelete.setOnClickListener(v -> {
            ingredientList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    // ✅ Fix: Add this method to update ingredient list dynamically
    public void setIngredientList(List<Ingredient> newIngredientList) {
        if (newIngredientList != null) {
            ingredientList.clear();
            ingredientList.addAll(newIngredientList);
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredientName, tvIngredientQuantity;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
            tvIngredientQuantity = itemView.findViewById(R.id.tv_ingredient_quantity);
            btnDelete = itemView.findViewById(R.id.btn_delete_ingredient);
        }
    }
}
