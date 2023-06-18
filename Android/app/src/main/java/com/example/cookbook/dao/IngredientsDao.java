package com.example.cookbook.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.cookbook.entity.Ingredient;
import com.example.cookbook.models.RecipeWithLikes;

import java.util.List;

@Dao
public interface IngredientsDao {
    @Insert
    void insertAll(List<Ingredient> ingredients);
    @Transaction
    @Query("SELECT * from ingredient")
    LiveData<List<Ingredient>> getAllIngredients();
    @Transaction
    @Query("SELECT DISTINCT name from ingredient")
    List<String> getAllDistinctIngredientNames();
}
