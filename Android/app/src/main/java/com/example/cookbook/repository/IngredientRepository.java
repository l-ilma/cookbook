package com.example.cookbook.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.cookbook.AppDatabase;
import com.example.cookbook.dao.IngredientsDao;
import com.example.cookbook.entity.Ingredient;

import java.util.List;

public class IngredientRepository {
    private final IngredientsDao ingredientsDao;
    public IngredientRepository(Context context) {
        ingredientsDao = AppDatabase.getInstance(context).ingredientsDao();
    }

    public LiveData<List<Ingredient>> getAllIngredients() {
        return ingredientsDao.getAllIngredients();
    }

    public List<String> getAllIngredientNames(){
        return ingredientsDao.getAllDistinctIngredientNames();
    }
}

