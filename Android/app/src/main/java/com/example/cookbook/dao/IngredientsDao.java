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
    @Insert
    long add(Ingredient ingredient);
    @Query("UPDATE Ingredient SET name = :name, quantity = :quantity, measure = :measure WHERE recipeId = :recipeId AND id = :ingredientId")
    void update(long ingredientId, long recipeId, String name, int quantity, String measure);
    @Query("DELETE FROM Ingredient WHERE id = :id")
    void delete(long id);

}
