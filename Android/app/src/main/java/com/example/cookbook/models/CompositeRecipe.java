package com.example.cookbook.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.cookbook.entity.Comment;
import com.example.cookbook.entity.Ingredient;
import com.example.cookbook.entity.Recipe;
import com.example.cookbook.entity.User;
import com.example.cookbook.entity.UserLikesRecipeCrossRef;

import java.util.List;

public class CompositeRecipe {
    public CompositeRecipe(Recipe recipe, List<Comment> comments, List<Ingredient> ingredients) {
        this.recipe = recipe;
        this.comments = comments;
        this.ingredients = ingredients;
    }

    @Embedded
    public Recipe recipe;

    @Relation(parentColumn = "id", entityColumn = "recipeId")
    public List<Comment> comments;

    @Relation(parentColumn = "id", entityColumn = "recipeId")
    public List<Ingredient> ingredients;

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
