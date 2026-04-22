package com.pkk.mealmate_recipesapp.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.PropertyName;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Parcelable {
    private String id;
    private String name;
    private String cookingTime;
    private String calories;
    private String category;
    private String date;
    private String mealType;
    private String description;
    private String imageUrl;

    private boolean favorite;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    private List<Ingredient> ingredients = new ArrayList<>(); // 🔥 Initialize to prevent null issues

    public Recipe() {
        // Required empty constructor for Firebase
    }

    public Recipe(String id, String name, String cookingTime, String calories, String category,
                  String date, String mealType, String description, String imageUrl, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.cookingTime = cookingTime;
        this.calories = calories;
        this.category = category;
        this.date = date;
        this.mealType = mealType;
        this.description = description;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>(); // ✅ Avoid null values
    }

    protected Recipe(Parcel in) {
        id = in.readString();
        name = in.readString();
        cookingTime = in.readString();
        calories = in.readString();
        category = in.readString();
        date = in.readString();
        mealType = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        if (ingredients == null) { // 🔥 Prevent potential null issues
            ingredients = new ArrayList<>();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(cookingTime);
        dest.writeString(calories);
        dest.writeString(category);
        dest.writeString(date);
        dest.writeString(mealType);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeTypedList(ingredients);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    // ✅ Add Getters with Firebase Annotations
    @PropertyName("id")
    public String getId() { return id; }

    @PropertyName("name")
    public String getName() { return name; }

    @PropertyName("cookingTime")
    public String getCookingTime() { return cookingTime; }

    @PropertyName("calories")
    public String getCalories() { return calories; }

    @PropertyName("category")
    public String getCategory() { return category; }

    @PropertyName("date")
    public String getDate() { return date; }

    @PropertyName("mealType")
    public String getMealType() { return mealType; }

    @PropertyName("description")
    public String getDescription() { return description; }

    @PropertyName("imageUrl")
    public String getImageUrl() { return imageUrl; }

    @PropertyName("ingredients")
    public List<Ingredient> getIngredients() { return ingredients; }

    // ✅ Add Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCookingTime(String cookingTime) { this.cookingTime = cookingTime; }
    public void setCalories(String calories) { this.calories = calories; }
    public void setCategory(String category) { this.category = category; }
    public void setDate(String date) { this.date = date; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }
}
