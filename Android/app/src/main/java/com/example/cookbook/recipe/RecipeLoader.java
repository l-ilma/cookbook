package com.example.cookbook.recipe;

import com.example.cookbook.models.Ingredient;
import com.example.cookbook.models.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeLoader {
    public static Map<Integer, Recipe> load(){
        Map<Integer, Recipe> recipeMap = new HashMap<>();
        List<Recipe> recipeList = new ArrayList<>();

// Recipe 1
        Ingredient ingredient1 = new Ingredient("Flour", 2, "cups");
        Ingredient ingredient2 = new Ingredient("Sugar", 1, "cup");
        List<Ingredient> ingredients1 = Arrays.asList(ingredient1, ingredient2);

        String instructions1 = "1. Preheat the oven to 350°F (175°C).\n" +
                "2. In a large mixing bowl, combine the flour and sugar.\n" +
                "3. Add the remaining ingredients and mix until well combined.\n" +
                "4. Drop spoonfuls of dough onto a greased baking sheet.\n" +
                "5. Bake for 10-12 minutes or until golden brown.";

        Recipe recipe1 = new Recipe(1, null, instructions1, "Chocolate Chip Cookies",
                100, ingredients1, false,false);
        recipeList.add(recipe1);
        recipeMap.put(1, recipe1);

// Recipe 2
        Ingredient ingredient3 = new Ingredient("Spaghetti", 8, "ounces");
        Ingredient ingredient4 = new Ingredient("Ground Beef", 1, "pound");
        List<Ingredient> ingredients2 = Arrays.asList(ingredient3, ingredient4);



        String instructions2 = "1. Cook the spaghetti according to package instructions.\n" +
                "2. In a separate pan, cook the ground beef until browned.\n" +
                "3. Drain the spaghetti and add it to the pan with the ground beef.\n" +
                "4. Add your favorite sauce and simmer for 5 minutes.\n" +
                "5. Serve hot and garnish with grated cheese.";

        Recipe recipe2 = new Recipe(2, null, instructions2, "Spaghetti and Meatballs",
                75, ingredients2, false, true);
        recipeList.add(recipe2);
        recipeMap.put(2, recipe2);

// Recipe 3
        Ingredient ingredient5 = new Ingredient("Cucumber", 2, "medium-sized");
        Ingredient ingredient6 = new Ingredient("Red Onion", 1, "small");
        List<Ingredient> ingredients3 = Arrays.asList(ingredient5, ingredient6);

        String instructions3 = "1. Slice the cucumbers and red onion into thin rounds.\n" +
                "2. In a mixing bowl, combine the cucumbers and red onion.\n" +
                "3. In a separate bowl, mix together the dressing ingredients.\n" +
                "4. Pour the dressing over the cucumber and onion mixture.\n" +
                "5. Toss well to coat the vegetables. Refrigerate for 1 hour before serving.";

        Recipe recipe3 = new Recipe(3, null, instructions3, "Cucumber Salad",
                50, ingredients3, true, false);
        recipeList.add(recipe3);
        recipeMap.put(3, recipe3);

// Recipe 4
        Ingredient ingredient7 = new Ingredient("Pizza Dough", 1, "ball");
        Ingredient ingredient8 = new Ingredient("Tomato Sauce", 1, "cup");
        List<Ingredient> ingredients4 = Arrays.asList(ingredient7, ingredient8);

        String instructions4 = "1. Preheat the oven to the recommended temperature for your pizza dough.\n" +
                "2. Roll out the pizza dough into your desired shape.\n" +
                "3. Spread the tomato sauce evenly over the dough.\n" +
                "4. Add your favorite toppings such as cheese, vegetables, and meats.\n" +
                "5. Bake the pizza in the oven for the recommended time or until the crust is golden brown.";

        Recipe recipe4 = new Recipe(4, null, instructions4, "Homemade Pizza",
                120, ingredients4, true, true);
        recipeList.add(recipe4);
        recipeMap.put(4, recipe4);

// Recipe 5
        Ingredient ingredient9 = new Ingredient("Flour", 1, "cup");
        Ingredient ingredient10 = new Ingredient("Milk", 1, "cup");
        List<Ingredient> ingredients5 = Arrays.asList(ingredient9, ingredient10);

        String instructions5 = "1. In a mixing bowl, whisk together the flour, milk, and other ingredients.\n" +
                "2. Heat a non-stick pan or griddle over medium heat.\n" +
                "3. Pour a ladleful of the batter onto the pan and spread it evenly.\n" +
                "4. Cook for a few minutes until bubbles form on the surface.\n" +
                "5. Flip the pancake and cook for another minute or until golden brown.";

        Recipe recipe5 = new Recipe(5, null, instructions5, "Homemade Pizza",
                120, ingredients5, false,false);
        recipeList.add(recipe5);
        recipeMap.put(5, recipe5);

        return recipeMap;
    }
}
