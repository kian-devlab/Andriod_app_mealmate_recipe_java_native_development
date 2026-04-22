package com.pkk.mealmate_recipesapp.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pkk.mealmate_recipesapp.adapters.CategoryAdapter;
import com.pkk.mealmate_recipesapp.databinding.FragmentCategoryBinding;
import com.pkk.mealmate_recipesapp.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private FirebaseFirestore db;
    private CategoryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        adapter = new CategoryAdapter();
        binding.rvCategories.setAdapter(adapter);

        loadCategories(); // Load categories from Firestore

        // Add category button
        binding.btnAddCategory.setOnClickListener(view1 -> showAddCategoryDialog());
    }

    private void loadCategories() {
        CollectionReference categoryRef = db.collection("Categories");

        categoryRef.get().addOnCompleteListener(task -> {
            List<Category> categories = new ArrayList<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    categories.add(document.toObject(Category.class));
                }
            }

            // ✅ Ensure RecyclerView updates even when no categories exist
            CategoryAdapter adapter = (CategoryAdapter) binding.rvCategories.getAdapter();
            if (adapter != null) {
                adapter.setCategoryList(categories);
            }
        });
    }


    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("New Category");

        // Create Layout for Dialog
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        // Input Fields
        final EditText etCategoryName = new EditText(getContext());
        etCategoryName.setHint("Enter Category Name");
        layout.addView(etCategoryName);

        final EditText etCategoryImage = new EditText(getContext());
        etCategoryImage.setHint("Enter Image URL (Optional)");
        layout.addView(etCategoryImage);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String categoryName = etCategoryName.getText().toString().trim();
            String categoryImage = etCategoryImage.getText().toString().trim();

            if (!categoryName.isEmpty()) {
                addNewCategory(categoryName, categoryImage);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void addNewCategory(String categoryName, String categoryImage) {
        CollectionReference categoryRef = db.collection("Categories");
        String id = categoryRef.document().getId();
        Category newCategory = new Category(id, categoryName, categoryImage);

        categoryRef.document(id).set(newCategory).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Category Added", Toast.LENGTH_SHORT).show();
                loadCategories(); // Refresh category list
            } else {
                Toast.makeText(getContext(), "Failed to add category", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
