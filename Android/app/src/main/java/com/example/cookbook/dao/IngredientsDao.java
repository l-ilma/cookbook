package com.example.cookbook.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.cookbook.entity.Ingredient;

import java.util.List;

@Dao
public interface IngredientsDao {
    @Insert
    void insertAll(List<Ingredient> ingredients);
}
