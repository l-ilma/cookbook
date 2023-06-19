package com.example.cookbook.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cookbook.entity.Ingredient;

import java.util.List;

@Dao
public interface IngredientsDao {
    @Insert
    void insertAll(List<Ingredient> ingredients);
    @Insert
    long add(Ingredient ingredient);
    @Query("UPDATE Ingredient SET name = :name, quantity = :quantity, measure = :measure WHERE recipeId = :recipeId AND id = :ingredientId")
    void update(long ingredientId, long recipeId, String name, int quantity, String measure);
    @Query("DELETE FROM Ingredient WHERE id = :id")
    void delete(long id);

}
