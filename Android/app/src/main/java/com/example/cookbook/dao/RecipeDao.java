package com.example.cookbook.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.cookbook.entity.Recipe;
import com.example.cookbook.models.CompositeRecipe;
import com.example.cookbook.models.RecipeWithLikes;

import java.util.List;

@Dao
public interface RecipeDao {
    @Transaction
    @Query("SELECT * from Recipe")
    LiveData<List<RecipeWithLikes>> getAllRecipes();

    @Transaction
    @Query("SELECT * from Recipe WHERE id = :id")
    LiveData<CompositeRecipe> findById(long id);

    @Transaction
    @Query("SELECT price from Recipe")
    List<Double> getAllPrices();

    @Insert
    void insertAll(List<Recipe> recipes);
}
