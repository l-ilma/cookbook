package com.example.cookbook.recipe;

import com.example.cookbook.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeRepository {
    private static RecipeRepository recipeRepository;
    private List<Recipe> allRecipes;
    private List<Recipe> favourites;
    private List<Recipe> myRecipes;

    private RecipeRepository(){
        favourites = new ArrayList<>();
        myRecipes = new ArrayList<>();
        allRecipes = RecipeLoader.load();
    }

    public static RecipeRepository getInstance(){
        if(recipeRepository == null){
            recipeRepository = new RecipeRepository();
        }
        return recipeRepository;
    }

    public void addFavorites(Recipe recipe) {
        favourites.add(recipe);
    }

    public void removeFavorites(Recipe recipe){
        favourites.remove(recipe);
    }

    public void addMyRecipe(Recipe recipe){
        myRecipes.add(recipe);
        allRecipes.add(recipe);
    }

    public void deleteRecipe(Recipe recipe){
        myRecipes.remove(recipe);
    }


}
