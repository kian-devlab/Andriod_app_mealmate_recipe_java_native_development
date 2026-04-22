package com.pkk.mealmate_recipesapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pkk.mealmate_recipesapp.R;
import com.pkk.mealmate_recipesapp.models.GroceryList;

import java.util.List;

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.GroceryViewHolder> {

    private final List<GroceryList> groceryList;

    public GroceryListAdapter(List<GroceryList> groceryList) {
        this.groceryList = groceryList;
    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grocery, parent, false);
        return new GroceryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
        GroceryList groceryItem = groceryList.get(position);

        // Set the item name in the TextView
        holder.tvGroceryItem.setText(groceryItem.getName());

        // Handle checkbox state (optional: you can add more logic here)
        holder.cbGroceryItem.setChecked(false); // Initial unchecked state
    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public static class GroceryViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroceryItem;
        CheckBox cbGroceryItem;

        public GroceryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroceryItem = itemView.findViewById(R.id.tv_grocery_item);
            cbGroceryItem = itemView.findViewById(R.id.cb_grocery_item);
        }
    }
}
