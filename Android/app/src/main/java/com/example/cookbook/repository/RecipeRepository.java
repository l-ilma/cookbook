package com.example.cookbook.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.cookbook.AppDatabase;
import com.example.cookbook.dao.RecipeDao;
import com.example.cookbook.entity.Recipe;
import com.example.cookbook.models.CompositeRecipe;
import com.example.cookbook.models.RecipeWithLikes;

import java.util.List;

public class RecipeRepository {
    private final RecipeDao recipeDao;

    public RecipeRepository(Context context) {
        recipeDao = AppDatabase.getInstance(context).recipeDao();
    }

    public LiveData<CompositeRecipe> findById(long recipeId) {
        return recipeDao.findById(recipeId);
    }

    public long add(Recipe recipe) {
        return recipeDao.add(recipe);
    }

    public void updateRecipe(long id, String name, String instructions, String imagePath) {
        recipeDao.update(id, name, instructions, imagePath);
    }

    public LiveData<List<RecipeWithLikes>> getAllRecipes() {
        return recipeDao.getAllRecipes();
    }

    public List<Double> getAllPrices() { return recipeDao.getAllPrices(); }
}
