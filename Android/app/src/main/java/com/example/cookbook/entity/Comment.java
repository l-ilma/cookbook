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
        ),
        @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId"
        )
})
public class Comment extends BaseEntity {
    public long userId;

    public long recipeId;

    public String text;

    public Comment(long userId, long recipeId, String text) {
        this.userId = userId;
        this.recipeId = recipeId;
        this.text = text;
    }
}
