package com.example.cookbook.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = CASCADE
        )
})
public class Ingredient extends BaseEntity {
    public long recipeId;

    public String name;

    public int quantity;

    public String measure;

    public Ingredient(long recipeId, String name, int quantity, String measure) {
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.measure = measure;
    }
}
