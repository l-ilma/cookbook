package com.example.cookbook.recipe;

import com.example.cookbook.models.Comment;
import com.example.cookbook.models.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecipeRepository {
    private static RecipeRepository recipeRepository;
    private Map<Integer, Recipe> recipes;

    private RecipeRepository() throws ParseException {
        recipes = RecipeLoader.load();
    }

    public static RecipeRepository getInstance() throws ParseException {
        if(recipeRepository == null){
            recipeRepository = new RecipeRepository();
        }
        return recipeRepository;
    }

    public List<Recipe> getRecipes() {
        return new ArrayList(recipes.values());
    }

    public List<Recipe> getFavourites() {
        return recipes.values().stream().filter(x -> x.liked).collect(Collectors.toList());
    }

    public List<Recipe> getMyRecipes() {
        return recipes.values().stream().filter(x -> x.my).collect(Collectors.toList());
    }

    public void likeRecipe(Integer id){
        recipes.get(id).liked = true;
    }

    public void unlikeRecipe(Integer id){
        recipes.get(id).liked = false;
    }

    public void deleteRecipe(Integer id){
        recipes.remove(id);
    }

    public void uploadRecipeWithComment(int id, Comment comment) {
        recipes.get(id).comments.add(comment);
    }
}
