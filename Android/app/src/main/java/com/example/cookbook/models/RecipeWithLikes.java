package com.example.cookbook.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.cookbook.entity.Recipe;
import com.example.cookbook.entity.User;
import com.example.cookbook.entity.UserLikesRecipeCrossRef;

import java.util.List;

public class RecipeWithLikes {
    @Embedded
    public Recipe recipe;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = UserLikesRecipeCrossRef.class,
                    parentColumn = "recipeId",
                    entityColumn = "userId"
            )
    )
    public List<User> likes;
}
