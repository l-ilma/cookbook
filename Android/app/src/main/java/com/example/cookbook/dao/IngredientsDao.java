package com.example.cookbook.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.cookbook.entity.Ingredient;

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

    @Transaction
    @Query("SELECT name from ingredient where recipeId = :id")
    List<String> getIngredientsForRecipe(long id);

    @Insert
    long add(Ingredient ingredient);

    @Query("UPDATE Ingredient SET name = :name, quantity = :quantity, measure = :measure WHERE recipeId = :recipeId AND id = :ingredientId")
    void update(long ingredientId, long recipeId, String name, int quantity, String measure);

    @Query("DELETE FROM Ingredient WHERE id = :id")
    void delete(long id);

    @Delete
    void removeAll(List<Ingredient> ingredients);

    @Update
    void updateAll(List<Ingredient> ingredients);
}
