package com.example.cookbook.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        primaryKeys = {"userId", "recipeId"},
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE),
                @ForeignKey(entity = Recipe.class, parentColumns = "id", childColumns = "recipeId", onDelete = CASCADE),
        }
)
public class UserLikesRecipeCrossRef {
    public long userId;
    public long recipeId;

    public UserLikesRecipeCrossRef(long userId, long recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }
}
