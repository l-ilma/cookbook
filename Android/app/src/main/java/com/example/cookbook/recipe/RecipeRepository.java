package com.example.cookbook.recipe;

import com.example.cookbook.models.Recipe;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeRepository {
    private static RecipeRepository recipeRepository;
    private List<Recipe> recipes;

    private RecipeRepository(){
        recipes = RecipeLoader.load();
    }

    public static RecipeRepository getInstance(){
        if(recipeRepository == null){
            recipeRepository = new RecipeRepository();
        }
        return recipeRepository;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public List<Recipe> getFavourites() {
        return recipes.stream().filter(x -> x.liked).collect(Collectors.toList());
    }

    public List<Recipe> getMyRecipes() {
        return recipes.stream().filter(x -> x.my).collect(Collectors.toList());
    }

    public void deleteRecipe(Recipe recipe){
        recipes.remove(recipe);
    }


}
