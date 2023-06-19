package com.example.cookbook.repository;

import android.content.Context;

import com.example.cookbook.AppDatabase;
import com.example.cookbook.dao.IngredientsDao;
import com.example.cookbook.entity.Ingredient;

public class IngredientsRepository {
    final IngredientsDao ingredientsDao;

    public IngredientsRepository(Context context) {
        ingredientsDao = AppDatabase.getInstance(context).ingredientsDao();
    }

    public void updateIngredientForRecipe(long ingredientId, long recipeId, String name, String measure, int quantity) {
        ingredientsDao.update(ingredientId, recipeId, name, quantity, measure);
    }

    public long addIngredientToRecipe(Ingredient ingredient) {
        return ingredientsDao.add(ingredient);
    }

    public void removeIngredient(long id) {
        ingredientsDao.delete(id);
    }
}
