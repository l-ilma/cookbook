package com.example.cookbook.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.cookbook.AppDatabase;
import com.example.cookbook.dao.RecipeDao;
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

    public LiveData<List<RecipeWithLikes>> getAllRecipes() {
        return recipeDao.getAllRecipes();
    }
}
